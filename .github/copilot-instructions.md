# Copilot Instructions: Kotlin Spring Boot API

> **📝 Project Status**: ✅ **FULLY IMPLEMENTED** - Complete Kotlin Spring Boot API with Oracle database, Norwegian localization, Docker containerization, and React TypeScript frontend.

## Stack & Architecture - ✅ IMPLEMENTED
- **Stack**: Kotlin + Spring Boot 3.5.4 + Maven + Oracle XE 21.3 + OAuth2/JWT + Docker
- **Pattern**: RESTful API with SOLID principles and Norwegian localization
- **Layers**: Controllers → Facades → Repositories (Oracle JDBCTemplate, pure SQL)
- **Frontend**: React 18 + TypeScript + Tailwind CSS + Vite
- **Testing**: 60+ frontend tests passing, comprehensive backend coverage

## Implemented Features

### ✅ Backend API
- **User Management**: Complete CRUD with Norwegian validation (`/api/users/*`)
- **Norwegian Localization**: All error messages and validation in Norwegian
- **Oracle Database**: Containerized Oracle XE with sequence-based ID generation
- **Security**: OAuth2 JWT with method-level authorization
- **Documentation**: Interactive Swagger UI at `/swagger-ui.html`
- **Docker**: Multi-stage builds with automated deployment scripts
- **Health Monitoring**: Production-ready actuator endpoints

### ✅ Frontend App  
- **User Registration**: `/register` with Norwegian UI and validation
- **Profile Management**: `/profile` with edit functionality
- **Component Library**: Reusable accessible components
- **Norwegian Validation**: Mobile numbers (+47), email, form validation
- **Responsive Design**: Mobile-first with dark mode support

## Key Standards

### Code Structure
```
src/main/kotlin/
├── controller/     # @RestController - HTTP endpoints
├── facade/         # @Service @Transactional - business logic orchestration  
├── repository/     # @Repository - data access with JDBCTemplate
├── model/          # data classes for entities
├── dto/            # data classes for API contracts
├── config/         # @Configuration classes
└── exception/      # custom exceptions + @ControllerAdvice
```

### Kotlin & Spring Conventions
- **Naming**: PascalCase classes, camelCase functions, UPPER_SNAKE_CASE constants
- **Injection**: Constructor injection, program against interfaces
- **Kotlin**: data classes for DTOs/entities, null safety (`?`, `?.`), prefer `val`
- **Controllers**: `@RestController`, return `ResponseEntity<T>`, use `@Valid`
- **Security**: `@PreAuthorize` method-level, JWT validation, RBAC

### Testing & Documentation
- **Testing**: Kotest + MockK, `@SpringBootTest` for integration, TestContainers
- **API Docs**: OpenAPI 3.0, comprehensive `@Operation/@Schema` annotations
- **DB**: Oracle with Flyway migrations, HikariCP connection pooling

## Essential Patterns

### Controller Example
```kotlin
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management")
@PreAuthorize("hasRole('USER')")
class UserController(private val userFacade: UserFacade) {
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('USER') and (#id == authentication.principal.id or hasRole('ADMIN'))")
    fun getUser(@PathVariable id: Long): ResponseEntity<UserDto> =
        userFacade.findById(id)?.let { ResponseEntity.ok(it) } 
            ?: ResponseEntity.notFound().build()
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserDto> {
        val user = userFacade.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }
}
```

### Facade Example
```kotlin
@Service
@Transactional
class UserFacadeImpl(
    private val userRepository: UserRepository,
    private val auditRepository: AuditRepository
) : UserFacade {
    
    override fun createUser(request: CreateUserRequest): UserDto {
        val user = userRepository.save(User(email = request.email))
        auditRepository.logUserCreation(user.id)
        return user.toDto()
    }
}
```

### Repository Example
```kotlin
@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {
    
    fun findById(id: Long): User? = try {
        jdbcTemplate.queryForObject(
            "SELECT user_id, email FROM users WHERE user_id = ?",
            { rs, _ -> User(rs.getLong("user_id"), rs.getString("email")) },
            id
        )
    } catch (e: EmptyResultDataAccessException) { null }
    
    fun save(user: User): User {
        val id = jdbcTemplate.queryForObject(
            "INSERT INTO users (email) VALUES (?) RETURNING user_id",
            Long::class.java, user.email
        )
        return user.copy(id = id ?: 0)
    }
}
```

### Security Configuration
```kotlin
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2ResourceServer { it.jwt { } }
            .authorizeHttpRequests { authz ->
                authz.requestMatchers("/actuator/health", "/swagger-ui/**").permitAll()
                    .anyRequest().authenticated()
            }.build()
}
```

## Production Deployment - ✅ READY

### Automated Setup (Use This!)
```bash
# Complete production stack
./scripts/start-api.sh

# Frontend development
cd timeforing-app-gui && npm run dev

# Production builds
./scripts/start-api.sh --rebuild  # API rebuild
cd timeforing-app-gui && npm run build  # Frontend build
```

### Docker Integration - ✅ IMPLEMENTED
- **Oracle XE 21c**: Containerized with automated user setup via podman-compose
- **Multi-stage Build**: Maven dependency caching + Eclipse Temurin JRE
- **Health Checks**: Database connectivity verification and API monitoring
- **Persistent Storage**: Oracle data volumes with proper user privileges
- **Network Isolation**: Docker networking with service discovery

### Database Production Setup - ✅ READY
- **Connection**: `jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=oracledb)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=XEPDB1)))`
- **User**: `timeforing_user` with full privileges (auto-created)
- **Password**: `TimeTrack123` (configured via scripts)
- **Schema**: Norwegian user tables with sequence-based ID generation
}