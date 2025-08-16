# Plan for Fixing UserController and UserFacade Compilation Issues

## 1. Problem Summary
- The project fails to compile due to syntax errors in `UserController.kt` and `UserFacade.kt`.
- Main issues:
  - Broken or invalid OpenAPI annotations (e.g., `@ApiResponses` array, non-constant arguments).
  - Leftover or mismatched braces and class structure.
  - Inconsistent or duplicate DTO field names (`email` vs `epost`).
  - Outdated or commented endpoints and annotations causing confusion.
  - Duplicate or misplaced helper functions (e.g., `toProfileDto`).

## 2. Codebase Facts (from code)
- `UserController` endpoints:
  - `/register` (POST): Registers a user, expects `RegisterUserRequest`.
  - `/profile` (GET/PUT/DELETE): Get, update, or delete the current user profile (uses JWT for user ID).
  - `/check-email` (GET): Checks if an email is available, expects `epost` param.
- `UserFacade` interface:
  - `registerUser`, `getProfile`, `updateProfile`, `deleteProfile`, `findUserByEpost`, `isEmailAvailable`.
  - Implementation uses `UserRepository` and converts `User` to `UserProfileDto` via `toProfileDto`.
- DTOs use `epost` (not `email`) for all API contracts.
- All validation and error messages are in Norwegian.

## 3. Step-by-Step Fix Plan

### Step 1: Clean Up UserController
- Remove all broken or commented-out endpoints and annotations.
- Remove or fix all OpenAPI annotations that use non-constant arguments or are not required for compilation.
- Ensure only endpoints that match the current `UserFacade` interface and DTOs remain.
- Use only `epost` for email fields.
- Ensure class braces and structure are correct.

### Step 2: Clean Up UserFacade
- Remove any duplicate or misplaced helper functions (e.g., `toProfileDto`).
- Ensure all DTO conversions use `epost`.
- Remove unused variables (e.g., `dateFormatter`).
- Ensure only the current interface methods are implemented.

### Step 3: Verify DTO Consistency
- Ensure all DTOs use `epost` for email fields.
- Ensure regex and validation annotations are correct (e.g., `@Pattern(regexp = "^\\+47\\d{8}$")`).

### Step 4: Build and Test
- Run `mvn test -Dtest=UserFacadeTest` to verify compilation and test success.
- If errors remain, repeat steps 1-3 as needed.

## 4. Acceptance Criteria
- Project compiles without errors.
- All user profile endpoints work and match the DTOs and facade interface.
- Unit tests for `UserFacade` pass.
- No commented or broken code remains in controller or facade.

---

*This plan is based on the current state of the code and error logs. All changes will be made incrementally and tested after each major step.*
