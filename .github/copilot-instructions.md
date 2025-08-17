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
src/main/kotlin/
├── controller/     # @RestController - HTTP endpoints
├── facade/         # @Service @Transactional - business logic orchestration  
├── repository/     # @Repository - data access with JDBCTemplate
├── model/          # data classes for entities
├── dto/            # data classes for API contracts
├── config/         # @Configuration classes
└── exception/      # custom exceptions + @ControllerAdvice

### Feature-based Code Structure (2025)
```
src/main/kotlin/com/example/basespringbootapikotlin/feature/
    ├── user/      # All user-related code: controller, facade, repository, model, dto, etc.
    ├── project/   # All project-related code: controller, facade, repository, model, dto, etc.
    └── ...        # (Add more features as needed)
```

Each feature folder contains all files for that feature (no subpackages per layer). Example:
```
src/main/kotlin/com/example/basespringbootapikotlin/feature/user/
    UserController.kt
    UserFacade.kt
    UserFacadeImpl.kt
    UserRepository.kt
    UserDto.kt
**Note:** All package declarations and imports use `com.example.basespringbootapikotlin.feature.<feature>`.

    ```
- **API Docs**: OpenAPI 3.0, comprehensive `@Operation/@Schema` annotations
        ├── project/   # All project-related code: controller, facade, repository, model, dto, etc.
        └── ...        # (Add more features as needed)
    ```

    Each feature folder contains all files for that feature (no subpackages per layer). Example:
    ```
- **DB**: Oracle with Flyway migrations, HikariCP connection pooling
        ProjectController.kt
        ProjectFacade.kt
        ProjectFacadeImpl.kt
        ProjectRepository.kt
        Project.kt
        ProjectDto.kt
        ...
    ```

## Essential Patterns


// ...existing code...

### Repository Example
```kotlin
@Repository
class ProjectRepository(private val jdbcTemplate: JdbcTemplate) {
    // All queries are scoped by userSub (from JWT claims)
    fun findById(projectId: Long, userSub: String): Project? = try {
        jdbcTemplate.queryForObject(
            "SELECT project_id, navn, beskrivelse FROM projects WHERE project_id = ? AND user_sub = ? AND aktiv = 1",
            { rs, _ ->
                Project(
                    id = rs.getLong("project_id"),
                    navn = rs.getString("navn"),
                    beskrivelse = rs.getString("beskrivelse"),
                    userSub = userSub
                )
            },
            projectId, userSub
        )
    } catch (e: EmptyResultDataAccessException) { null }

    fun save(project: Project): Project {
        val id = jdbcTemplate.queryForObject(
            "INSERT INTO projects (navn, beskrivelse, user_sub) VALUES (?, ?, ?) RETURNING project_id",
            Long::class.java, project.navn, project.beskrivelse, project.userSub
        )
        return project.copy(id = id ?: 0)
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
- **User**: `timeforing_user` (for application DB access; not for user management)
- **Password**: `TimeTrack123` (configured via scripts)
- **Schema**: Only project and time entry tables remain; all user data is provided via JWT claims (stateless, no user table)
}