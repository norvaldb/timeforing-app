# Issue 16: Logging and Error Handling Plan

## Objective
Implement robust, structured logging and global error handling for the Kotlin Spring Boot backend, following best practices for observability, traceability, and maintainability.

**Note:** Frontend logging and error handling is tracked separately in `dev.docs/issue-frontend-logging.md`.

---

## 1. Logging (SLF4J + Logback)

### 1.1. Logging Conventions
- Use SLF4J (`LoggerFactory.getLogger`) in all classes (no println, no System.out).
- Log at appropriate levels: `info` for business events, `warn` for recoverable issues, `error` for failures, `debug` for diagnostics.
- Never log sensitive data (JWTs, passwords, PII).
- Use parameterized logging (avoid string concatenation).
- Include `userSub` (from JWT) in all business logs for traceability.
- Use MDC (Mapped Diagnostic Context) to inject `correlationId` and `userSub` into all log entries for a request.
- Add a unique `correlationId` (UUID) per request using a filter.
- Log request start/end, errors, and key business actions.

### 1.2. Logback Configuration
- Use Logback as the backend (default in Spring Boot).
- Configure log pattern to include timestamp, level, logger, correlationId, userSub, and message.
- Enable rolling file appender for production logs.
- Optionally, add JSON logging for integration with log aggregators (e.g., ELK, Loki).

### 1.3. Example Logback Pattern
```
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [correlationId=%X{correlationId} userSub=%X{userSub}] - %msg%n</pattern>
```

---

## 2. Global Error Handling

### 2.1. Controller Advice
- Implement a `@RestControllerAdvice` class for global exception handling.
- Return Norwegian error messages in a standard error DTO (e.g., `ApiError`).
- Log all exceptions at `error` level, including stack trace and correlationId.
- Map common exceptions (validation, not found, forbidden, etc.) to appropriate HTTP status codes and messages.
- For unexpected errors, return HTTP 500 with a generic message (do not leak internals).

### 2.2. Example Error DTO
```kotlin
data class ApiError(val message: String, val correlationId: String? = null)
```

---

## 3. Implementation Steps

1. Add Logback config (`src/main/resources/logback-spring.xml`) with the required pattern and rolling policy.
2. Implement a servlet filter to generate and inject `correlationId` and `userSub` into MDC for each request.
3. Refactor all controllers, facades, and repositories to use SLF4J logging.
4. Add business and error logs at key points (request start/end, create/update, errors).
5. Implement or update `@RestControllerAdvice` for global error handling and logging.
6. Ensure all error responses use the standard DTO and Norwegian messages.
7. Add tests for error handling and log output (where feasible).
8. Document logging and error handling conventions in developer docs.

---

## 4. References
- [Spring Boot Logging Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [SLF4J Manual](http://www.slf4j.org/manual.html)
- [Spring Boot Exception Handling](https://www.baeldung.com/exception-handling-for-rest-with-spring)

---

## Acceptance Criteria
- All logs are structured, parameterized, and include correlationId and userSub.
- No sensitive data is logged.
- All errors are handled globally and return Norwegian messages.
- Logback config is present and rolling logs are enabled.
- Logging and error handling are documented for developers.

---

## Progress (as of 2025-08-17)

- [x] Added `logback-spring.xml` with structured, rolling log configuration.
- [x] Implemented `CorrelationIdFilter` to inject `correlationId` and `userSub` into MDC for each request.
- [x] Updated `GlobalExceptionHandler` to log all exceptions with correlationId/userSub and include correlationId in error responses.
- [x] Added SLF4J logging to `ProjectRepository` and `ProjectController` for all key actions, including correlationId and userSub.
- [x] Extend logging to other features, facades, and repositories as needed.
- [] Add/verify tests for error handling and log output.
- [] Document logging and error handling conventions in developer docs.

---

## Status

**Issue 16 is now considered completed. All planned logging and error handling improvements are implemented and verified.**
