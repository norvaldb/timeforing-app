# Timeforing App

A comprehensive time tracking application built with Kotlin Spring Boot API backend and React TypeScript frontend in a monorepo structure.

## ✅ **Status: FULLY IMPLEMENTED**
✅ **Frontend**: Complete React TypeScript app with Norwegian localization  
✅ **Backend**: Complete Kotlin Spring Boot API with Oracle database  
✅ **Database**: Oracle XE with automated containerized setup  
✅ **Testing**: 60+ passing frontend tests, comprehensive backend testing  
✅ **Docker**: Multi-stage builds with automated deployment scripts  

## 🚀 Quick Start

### Automated Setup (Recommended)
```bash
# Start everything with automated setup
./scripts/start-api.sh

# View API documentation
open http://localhost:8080/swagger-ui.html

# Start frontend (separate terminal)
cd timeforing-app-gui && npm run dev
open http://localhost:5173
```

### Manual Setup
```bash
# 1. Start database and API (containerized)
./scripts/start-api.sh

# 2. Start frontend development server  
cd timeforing-app-gui && npm run dev
```

## 🚀 Project Overview

**Timeforing App** is a time tracking solution designed for individuals and teams to efficiently track work hours, manage projects, and generate insightful reports. The application follows modern development practices with a monorepo structure containing both backend and frontend components.

**User management is now fully stateless and claims-driven:**
- All user identification, profile data, and roles are extracted from JWT claims (no user CRUD endpoints or user table in the backend).

## 🏗️ Repository Structure

```
timeforing-app/
├── src/                          # Backend (Kotlin Spring Boot API)
│   ├── main/kotlin/              # Application source code
│   ├── main/resources/           # Configuration and migrations
│   └── test/kotlin/              # Test suites
├── timeforing-app-gui/           # Frontend (React TypeScript)
├── scripts/                      # Database management scripts
├── docker-compose.yml            # Oracle XE database container
├── DEVELOPER_SETUP.md            # Complete environment setup guide
├── .github/                      # CI/CD workflows and project configs
├── pom.xml                       # Maven build configuration
├── README.md                     # This file
└── ...config files
```

## 🎯 Completed Features

### ✅ Backend API (Fully Implemented)
- **User Management** - Complete CRUD operations with Norwegian validation
- **Norwegian Localization** - All error messages and validation in Norwegian
- **Oracle Database Integration** - Containerized Oracle XE with automated setup
- **JWT Security** - OAuth2 resource server with method-level authorization
- **API Documentation** - Interactive Swagger UI with comprehensive schemas
- **Docker Support** - Multi-stage builds with dependency caching
- **Database Migrations** - Flyway migrations with Oracle sequence support
- **Automated Scripts** - Complete development workflow automation
- **Health Monitoring** - Actuator endpoints for production monitoring
- **Norwegian Mobile Validation** - +47 format with automatic formatting

### ✅ Frontend (Fully Implemented)  
- **User Registration** - Complete registration flow with Norwegian UI
- **Profile Management** - User profile editing with validation
- **Component Library** - Reusable UI components with accessibility
- **Form Validation** - Zod schemas with Norwegian error messages
- **Responsive Design** - Mobile-first with dark mode support
- **Testing Suite** - 60+ passing tests with comprehensive coverage
- **Accessibility** - WCAG 2.1 AA compliance
- **TypeScript** - Full type safety throughout the application

## 🏗️ Architecture

### Backend Architecture Pattern
```
API Controllers → Facades → Repositories
```

- **Controllers**: Handle HTTP requests/responses, validation, and routing
- **Facades**: Business logic orchestration, transaction management, and service coordination  
- **Repositories**: Data access layer with pure SQL operations

### Backend Project Structure
```
src/main/kotlin/com/norvaldb/api/timeforing/
├── controller/     # REST endpoints (@RestController)
├── facade/         # Business logic layer (@Service, @Transactional)
├── service/        # External service integrations
├── repository/     # Data access layer (@Repository)
├── model/          # Entity classes (plain data classes)
├── dto/            # Data Transfer Objects with OpenAPI schemas
├── config/         # Configuration classes (@Configuration)
└── exception/      # Custom exceptions and handlers
```

## 🛠️ Technology Stack

| Component | Technology | Version | Status |
|-----------|------------|---------|---------|
| Language | Kotlin | 1.9.25 | ✅ Implemented |
| Framework | Spring Boot | 3.5.4 | ✅ Implemented |
| Build Tool | Maven | 3.9.x | ✅ Implemented |
| Database | Oracle XE | 21.3 | ✅ Implemented |
| Connection Pool | HikariCP | Built-in | ✅ Implemented |
| Migration | Flyway | 10.21.0 | ✅ Implemented |
| Documentation | SpringDoc OpenAPI | 2.7.0 | ✅ Implemented |
| Security | Spring Security OAuth2 | Built-in | ✅ Implemented |
| Frontend | React + TypeScript | 18 | ✅ Implemented |
| Styling | Tailwind CSS | 3.x | ✅ Implemented |
| Testing | Vitest + Kotest | Latest | ✅ Implemented |
| Containers | Docker + Podman | Latest | ✅ Implemented |

## 🚦 Getting Started

### Prerequisites

- **Docker/Podman** (for containerized deployment)
- **Node.js 18+** (for frontend development)
- **Git**

### 1. Clone the Repository

```bash
git clone https://github.com/norvaldb/timeforing-app.git
cd timeforing-app
```

### 2. Start the Application

#### Option A: Complete Stack (Recommended)
```bash
# Start Oracle database and API (fully automated)
./scripts/start-api.sh

# Start frontend development server (separate terminal)
cd timeforing-app-gui && npm install && npm run dev
```

#### Option B: Development Mode
```bash
# Start database only
./scripts/start-api.sh --db-only

# Start backend locally (requires Java 21)
mvn spring-boot:run

# Start frontend
cd timeforing-app-gui && npm run dev
```

### 3. Access the Application

- **Frontend**: http://localhost:5173
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **API Health**: http://localhost:8080/actuator/health

### 4. Test the Application

```bash
# Test user registration
curl -X POST "http://localhost:8080/api/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "navn": "Test Bruker",
    "mobil": "41234567",
    "epost": "test@example.com"
  }'

# Run frontend tests
cd timeforing-app-gui && npm test

# Run backend tests (requires local development setup)
mvn test
```

## 🔧 Development Tools

### VS Code Configuration
The project includes VS Code workspace configuration with:
- **Task Automation**: Build, test, and run tasks
- **Debug Configuration**: Backend debugging setup

### GitHub Integration
- **CI/CD Pipelines**: Automated testing and deployment
- **Security Scanning**: Dependency vulnerability checks
- **Code Quality**: Automated code analysis
- **Issue Tracking**: Integrated project management

## 📊 API Documentation

Once the backend is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

Runtime-generated OpenAPI JSON (for frontend use)

- The build also supports exporting the runtime OpenAPI JSON to a file for the frontend to consume: `target/generated-openapi/api.json`.
- To generate the file as part of the build, run:

```bash
# Builds the jar, starts the app briefly, fetches /v3/api-docs and writes the file
mvn -DskipTests -Pexport-openapi verify
```

Or run the helper script directly (requires a packaged jar or use after `mvn -DskipTests package`):

```bash
./scripts/export-openapi.sh
```

Frontend developers should use `target/generated-openapi/api.json` as the contract when implementing API calls.
## 🗄️ Database Setup

### Create Database Schema

```bash
# Create database schema and user (connect as system)
sqlplus system/password123@localhost:1521/XEPDB1

# In SQL*Plus:
CREATE USER dev_user IDENTIFIED BY dev_password;
GRANT DBA TO dev_user;
CREATE SCHEMA AUTHORIZATION dev_user;

# Run Flyway migrations
mvn flyway:migrate
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests with coverage
mvn clean test

# Run integration tests
mvn verify

# Generate test reports
mvn allure:report
```

### Test Reports
- **JaCoCo Coverage**: `target/site/jacoco/index.html`
- **Allure Reports**: `target/site/allure-maven-plugin/index.html`
- **Surefire Reports**: `target/surefire-reports/`

## 🚀 Production Deployment

### Building for Production

```bash
# Build optimized JAR
mvn clean package -P production

# Run with production profile
java -jar target/timeforing-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```

### Docker Support (Coming Soon)

```bash
# Build Docker image
docker build -t timeforing-app .

# Run container
docker run -p 8080:8080 timeforing-app
```

## 📁 Key Files and Directories

- **`pom.xml`** - Maven configuration with dependencies and plugins
- **`src/main/kotlin/`** - Application source code
- **`src/main/resources/application.yml`** - Application configuration
- **`src/main/resources/db/migration/`** - Flyway database migrations
- **`src/test/kotlin/`** - Test suites and configurations
- **`.github/workflows/`** - CI/CD pipeline definitions
- **`.vscode/`** - VS Code workspace configuration
- **`timeforing-app-gui/`** - Frontend application (to be developed)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/awesome-feature`)
3. Commit your changes (`git commit -m 'Add awesome feature'`)
4. Push to the branch (`git push origin feature/awesome-feature`)
5. Open a Pull Request

## 📝 Development Guidelines

### Code Style
- Follow Kotlin coding conventions
- Use meaningful names for classes, functions, and variables
- Add KDoc comments for public APIs
- Maintain high test coverage (>80%)

### Commit Messages
- Use conventional commit format
- Include scope and type (feat, fix, docs, etc.)
- Keep messages concise but descriptive

### Pull Requests
- Include description of changes
- Add tests for new functionality
- Ensure CI pipeline passes
- Request review from maintainers

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 👨‍💻 Author

**Norvald Bjarte Algrøy**
- GitHub: [@norvaldb](https://github.com/norvaldb)
- Email: norvald@example.com

## 🔗 Links

- **Repository**: https://github.com/norvaldb/timeforing-app
- **Issues**: https://github.com/norvaldb/timeforing-app/issues
- **CI/CD**: https://github.com/norvaldb/timeforing-app/actions

---

**Happy coding! 🚀**

```bash
# Run OWASP dependency vulnerability scan
mvn org.owasp:dependency-check-maven:check

# Generate report without failing build
mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=11
```

**Generated Reports:**
- **HTML Report**: `target/dependency-check-report.html`
- **JSON Report**: `target/dependency-check-report.json`
- **XML Report**: `target/dependency-check-report.xml`

**Configuration:**
- **Fail Threshold**: CVSS score ≥ 7.0
- **Suppressions**: `owasp-suppressions.xml` (false positive management)
- **Test Dependencies**: Excluded from scanning for performance
- **NVD API**: Configure `NVD_API_KEY` environment variable for faster updates

**Setting up NVD API Key:**
1. Get a free API key from [NVD](https://nvd.nist.gov/developers/request-an-api-key)
2. **Local Development**: Set environment variable `export NVD_API_KEY=your-key-here`
3. **GitHub Actions**: Add `NVD_API_KEY` as a repository secret in Settings → Secrets and variables → Actions

### Example Test Structure

```kotlin
class UserFacadeTest : FunSpec({
    
    val userRepository = mockk<UserRepository>()
    val userFacade = UserFacadeImpl(userRepository)
    
    test("should find user by id") {
        // Given
        val userId = 1L
        val user = User(id = userId, email = "test@example.com")
        every { userRepository.findById(userId) } returns user
        
        // When
        val result = userFacade.findById(userId)
        
        // Then
        result shouldNotBe null
        result?.id shouldBe userId
        result?.email shouldBe "test@example.com"
    }
})
```

## � GitHub Actions CI/CD

### Automated Testing & Reporting

This project includes comprehensive GitHub Actions workflows for automated testing and reporting:

![CI Pipeline](https://github.com/norvaldb/base-springboot-api-kotlin/workflows/CI%20Pipeline%20with%20Testing%20and%20Reporting/badge.svg)
![Test Reports](https://github.com/norvaldb/base-springboot-api-kotlin/workflows/Test%20Reports%20Only/badge.svg)

### Available Workflows

#### 1. CI Pipeline (`ci.yml`)
**Triggers**: Push to main/develop, Pull Requests to main

**Features:**
- ✅ Runs all tests (unit + integration)
- ✅ Generates JaCoCo coverage reports
- ✅ Creates Allure test reports with history
- ✅ Posts coverage comments on PRs
- ✅ Uploads reports to GitHub Pages
- ✅ Codecov integration
- ✅ OWASP dependency scanning
- ✅ Artifact uploads with retention

#### 2. Test Reports Only (`test-reports.yml`)
**Triggers**: Push to main/develop, Pull Requests to main

**Features:**
- ✅ Lightweight testing workflow
- ✅ JaCoCo coverage analysis
- ✅ Allure report generation
- ✅ Test result summaries
- ✅ Downloadable artifacts

### Accessing Reports

**JaCoCo Coverage Reports:**
- **PR Comments**: Automatic coverage reports on pull requests
- **Artifacts**: Download from Actions → Workflow Run → Artifacts
- **Codecov**: Available at https://codecov.io/gh/norvaldb/base-springboot-api-kotlin

**Allure Test Reports:**
- **Artifacts**: Download from Actions → Workflow Run → Artifacts  
- **GitHub Pages**: https://norvaldb.github.io/base-springboot-api-kotlin (if enabled)

### Setting up GitHub Pages (Optional)

To enable automatic deployment of test reports to GitHub Pages:

1. Go to **Repository Settings** → **Pages**
2. **Source**: Deploy from a branch
3. **Branch**: `gh-pages`
4. **Folder**: `/` (root)

The CI pipeline will automatically deploy Allure reports with test history to your GitHub Pages site.

### Coverage Requirements

The workflows enforce coverage thresholds:
- **Overall Coverage**: 80% minimum
- **Changed Files**: 80% minimum

Failed coverage checks will be reported in PR comments and workflow status.

## �🗄️ Database

### Oracle Database Setup

The application uses Oracle Database 23ai with the following configuration:

- **Connection**: `jdbc:oracle:thin:@localhost:1521:XEPDB1`
- **Schema**: Configurable via `DB_SCHEMA` environment variable
- **Connection Pool**: HikariCP with optimized settings

### Flyway Migrations

Database migrations are located in `src/main/resources/db/migration/`:

```
db/migration/
├── V1__Create_users_table.sql
├── V2__Add_user_indexes.sql
└── V3__Create_audit_tables.sql
```

### Example Migration

```sql
-- V1__Create_users_table.sql
CREATE TABLE users (
    user_id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR2(255) NOT NULL UNIQUE,
    status VARCHAR2(50) DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
```

## 🔧 Configuration

### Application Profiles

- **dev** - Development profile with debug logging
- **test** - Testing profile with TestContainers
- **prod** - Production profile with optimized settings

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | dev_user |
| `DB_PASSWORD` | Database password | dev_password |
| `DB_SCHEMA` | Database schema | DEV_SCHEMA |
| `JWT_ISSUER_URI` | OAuth2 JWT issuer | https://your-oauth2-provider.com |
| `CORS_ALLOWED_ORIGINS` | CORS allowed origins | http://localhost:3000 |

## 📋 Development Guidelines

This project follows the comprehensive Copilot instructions located in `.github/copilot-instructions.md`. Key principles:

### SOLID Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extensible through interfaces
- **Liskov Substitution**: Proper inheritance hierarchies
- **Interface Segregation**: Focused, specific interfaces
- **Dependency Inversion**: Depend on abstractions

### Code Standards
- Use data classes for DTOs and entities
- Prefer `val` over `var`
- Leverage Kotlin null safety
- Use constructor injection
- Follow REST conventions
- Comprehensive error handling

### Security
- OAuth2 JWT validation
- Role-based access control
- Method-level security
- CORS configuration
- Security headers

## 🐳 Docker Support

### Development with Podman

```bash
# Start Oracle Database
podman run -d \
  --name oracle-xe \
  -p 1521:1521 \
  -e ORACLE_PWD=password123 \
  container-registry.oracle.com/database/express:latest
```

### Application Container (Future)

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/base-springboot-api-kotlin-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Follow the coding standards in `.github/copilot-instructions.md`
4. Add tests for new functionality
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For questions and support:

- Create an [Issue](https://github.com/norvaldb/base-springboot-api-kotlin/issues)
- Check the [Copilot Instructions](.github/copilot-instructions.md)
- Review the [API Documentation](http://localhost:8080/swagger-ui.html)

---

**Built with ❤️ using Kotlin and Spring Boot**