# Copilot Instructions: Kotlin Spring Boot API

> **📝 Project Status**: ✅ **FULLY IMPLEMENTED** - Complete Kotlin Spring Boot API with Oracle database, Norwegian localization, Docker containerization, and React TypeScript frontend.

## Stack & Architecture - ✅ IMPLEMENTED
- **Stack**: Kotlin + Spring Boot 3.5.4 + Maven + Oracle XE 21.3 + OAuth2/JWT + Docker
- **Pattern**: RESTful API with SOLID principles and Norwegian localization
- **Layers**: Controllers → Facades → Repositories (Oracle JDBCTemplate, pure SQL)
- **Frontend**: React 18 + TypeScript + Tailwind CSS + Vite
- **Testing**: 60+ frontend tests passing, comprehensive backend coverage

## Implemented Features

## Workflow: GitHub issues (important)

- Always read the GitHub issue before starting implementation. The issue description, acceptance criteria, labels, linked designs, and comments contain essential context. Do not begin coding until you've reviewed the issue and confirmed you understand the scope.
- When you start work, add a short note in the issue (or a comment) indicating you are working on it and which branch you'll use (e.g., `feature/issue-13`).
- Reference the issue number in commits and PR titles (e.g., `feat(issue-13): ...`) and include a brief note in the PR describing how the changes address the acceptance criteria.

### ✅ Backend API
- **Norwegian Localization**: All error messages and validation in Norwegian
- **Oracle Database**: Containerized Oracle XE with sequence-based ID generation
- **Security**: OAuth2 JWT with method-level authorization
- **Documentation**: Interactive Swagger UI at `/swagger-ui.html`
- **Docker**: Multi-stage builds with automated deployment scripts
- **Health Monitoring**: Production-ready actuator endpoints
- **Claim-Driven Authentication**: Stateless, no user table; all user data from JWT claims
- **Logging**: Structured logging with error handling and monitoring. Use `@Slf4j` for logging in all classes. Use Logback for logging configuration. 

### ✅ Frontend App  
- **User Registration**: `/register` with Norwegian UI and validation
- **Profile Management**: `/profile` with edit functionality
- **Component Library**: Reusable accessible components
- **Norwegian Validation**: Mobile numbers (+47), email, form validation
- **Responsive Design**: Mobile-first with dark mode support

Frontend OpenAPI contract (how to use `api.json`)

- Location: the runtime-generated OpenAPI JSON is written to `target/generated-openapi/api.json` in the repository after running the export step.
- Generate it locally (recommended when working on React issues):

```bash
# build + export in one step (builds jar, boots app briefly, fetches /v3/api-docs)
mvn -DskipTests -Pexport-openapi verify

# or: build first and run the helper script
mvn -DskipTests package
./scripts/export-openapi.sh
```

- Usage guidance for React work:
    - When you pick up a frontend issue that requires API calls/contract knowledge, first update to the latest `main` and run the export command to get a fresh `target/generated-openapi/api.json`.
    - Use this `api.json` as the single source of truth for generating TypeScript clients, validating request/response shapes, and writing mocks for component/integration tests.
    - If you change frontend code that depends on an API change, include a short note in the PR describing which server endpoints or DTOs the change depends on and whether `api.json` was regenerated.
    - If a backend change modifies the OpenAPI contract, ensure the maintainer of the backend runs the export and either attaches the generated `api.json` to the PR or updates the CI artifact so frontend CI can consume the updated contract.

- CI recommendation (optional): run `mvn -Pexport-openapi verify` in CI and publish `target/generated-openapi/api.json` as a workflow/artifact so frontend pipelines can fetch the canonical contract.

This makes `target/generated-openapi/api.json` the reliable contract whenever you implement React issues that call the API.

## Key Standards
src/main/kotlin/
├── feature/        # Features of the api
    ├── project/      # All project-related code: controller, facade, repository, model, dto, etc.
    ├── timeentry/    # All time entry-related code: controller, facade, repository, model, dto, etc.
    └── ...           # (Add more features as needed)
├── config/         # @Configuration classes
└── exception/      # custom exceptions + @ControllerAdvice

### Feature-based Code Structure (2025)
```
src/main/kotlin/com/example/basespringbootapikotlin/feature/
    ├── mockauth/  # All user-related code: controller, facade, repository, model, dto, etc.
    ├── project/   # All project-related code: controller, facade, repository, model, dto, etc.
    └── ...        # (Add more features as needed)
```

Each feature folder contains all files for that feature (no subpackages per layer). Example:
```
src/main/kotlin/com/example/basespringbootapikotlin/feature/project/
        ProjectController.kt
        ProjectFacade.kt
        ProjectFacadeImpl.kt
        ProjectRepository.kt
        Project.kt
        ProjectDto.kt
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

---

**See detailed plan for logging and error handling:**
`dev.docs/issue-16-logging.md`

### Example: ProjectController
```kotlin
@RestController
@RequestMapping("/api/projects")
class ProjectController(private val projectFacade: ProjectFacade) {
    @GetMapping
    fun getAllProjects(@AuthenticationPrincipal jwt: Jwt): List<ProjectDto> =
        projectFacade.getAllProjects(jwt.subject)

    @PostMapping
    fun createProject(@RequestBody dto: ProjectDto, @AuthenticationPrincipal jwt: Jwt): ProjectDto =
        projectFacade.createProject(dto, jwt.subject)
}
```

### Example: ProjectFacade & ProjectFacadeImpl
```kotlin
interface ProjectFacade {
    fun getAllProjects(userSub: String): List<ProjectDto>
    fun createProject(dto: ProjectDto, userSub: String): ProjectDto
}

@Service
class ProjectFacadeImpl(private val projectRepository: ProjectRepository) : ProjectFacade {
    override fun getAllProjects(userSub: String): List<ProjectDto> =
        projectRepository.findByUserSub(userSub).map { it.toDto() }

    override fun createProject(dto: ProjectDto, userSub: String): ProjectDto {
        val project = Project(
            id = null,
            navn = dto.navn,
            beskrivelse = dto.beskrivelse,
            userSub = userSub
        )
        return projectRepository.save(project).toDto()
    }
}
```

### Example: Project (Model/Entity)
```kotlin
data class Project(
    val id: Long?,
    val navn: String,
    val beskrivelse: String?,
    val userSub: String
)
```

### Example: ProjectDto
```kotlin
data class ProjectDto(
    val id: Long?,
    val navn: String,
    val beskrivelse: String?
)

fun Project.toDto() = ProjectDto(id, navn, beskrivelse)
```

### Example: TimeEntryController (Second Feature)
```kotlin
@RestController
@RequestMapping("/api/time-entries")
class TimeEntryController(private val timeEntryFacade: TimeEntryFacade) {
    @GetMapping
    fun getAll(@AuthenticationPrincipal jwt: Jwt): List<TimeEntryDto> =
        timeEntryFacade.getAll(jwt.subject)

    @PostMapping
    fun create(@RequestBody dto: TimeEntryDto, @AuthenticationPrincipal jwt: Jwt): TimeEntryDto =
        timeEntryFacade.create(dto, jwt.subject)
}
```

### Example: Global Error Handling
```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiError> =
        ResponseEntity(ApiError("Uventet feil: ${ex.localizedMessage}"), HttpStatus.INTERNAL_SERVER_ERROR)
}

data class ApiError(val message: String)
```

### Example: Logging with @Slf4j
```kotlin
import org.slf4j.LoggerFactory

class ProjectRepository {
    private val log = LoggerFactory.getLogger(ProjectRepository::class.java)

    fun save(project: Project): Project {
        log.info("Saving project for userSub={}", project.userSub)
        // ...
    }
}
```

### Repository Example
```kotlin
class ProjectRepository(private val jdbcTemplate: JdbcTemplate) {
    fun save(project: Project): Project {
        val id = jdbcTemplate.queryForObject(
            "INSERT INTO projects (navn, beskrivelse, user_sub) VALUES (?, ?, ?) RETURNING project_id",
            Long::class.java, project.navn, project.beskrivelse, project.userSub
        )
        return project.copy(id = id ?: 0)
    }

    fun findByUserSub(userSub: String): List<Project> =
        jdbcTemplate.query(
            "SELECT project_id, navn, beskrivelse, user_sub FROM projects WHERE user_sub = ?",
            arrayOf(userSub)
        ) { rs, _ ->
            Project(
                id = rs.getLong("project_id"),
                navn = rs.getString("navn"),
                beskrivelse = rs.getString("beskrivelse"),
                userSub = rs.getString("user_sub")
            )
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