# Developer Environment Setup

This document outlines the complete development environment setup for the Timeforing timetracking application. Follow this guide to get your development environment ready for both backend (Kotlin Spring Boot) and frontend (React TypeScript) development.

## 📋 Prerequisites Overview

The Timeforing application consists of:
- **Backend**: Kotlin Spring Boot API with Oracle XE database
- **Frontend**: React 18 + TypeScript + Vite
- **Container**: Oracle XE 21c running in Podman
- **Build Tools**: Maven for backend, npm/yarn for frontend

## 🔧 Required Tools

### 1. Java Development Kit (JDK)
**Required Version**: Java 21 or higher

#### Installation:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# Verify installation
java --version
javac --version
```

**Expected Output:**
```
java 21.0.7 2024-10-15 LTS
javac 21.0.7
```

### 2. Maven
**Required Version**: Maven 3.9.x or higher

#### Installation:
```bash
# Ubuntu/Debian
sudo apt install maven

# Verify installation
mvn --version
```

**Expected Output:**
```
Apache Maven 3.9.10
Maven home: /usr/share/maven
Java version: 21.0.7
```

### 3. Node.js and npm
**Required Version**: Node.js 18+ and npm 9+

#### Installation:
```bash
# Ubuntu/Debian - Install Node.js 22.x
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify installation
node --version
npm --version
```

**Expected Output:**
```
v22.18.0
10.8.2
```

### 4. Podman (Container Engine)
**Required Version**: Podman 4.0+ with podman-compose

#### Installation:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install podman

# Install podman-compose via pipx
sudo apt install pipx
pipx install podman-compose
pipx ensurepath

# Verify installation
podman --version
podman-compose --version
```

**Expected Output:**
```
podman version 4.9.3
podman-compose version 1.5.0
```

### 5. Git
**Required Version**: Git 2.25+

#### Installation:
```bash
# Ubuntu/Debian
sudo apt install git

# Configure Git (replace with your details)
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# Verify installation
git --version
```

### 6. Oracle Database Client (Optional)
For direct database access outside of containers:

```bash
# Download Oracle Instant Client (optional)
# Or use SQL*Plus via container: podman exec timeforing-oracle sqlplus
```

## 🗂️ Project Structure

```
timeforing-app/
├── src/main/kotlin/          # Backend Kotlin source code
├── src/main/resources/       # Backend configuration files
├── src/test/kotlin/          # Backend tests
├── timeforing-app-gui/       # Frontend React application
├── scripts/                  # Database and utility scripts
├── docker-compose.yml        # Database container configuration
├── pom.xml                   # Maven backend dependencies
└── README.md                 # Project documentation
```

## 🚀 Quick Start Guide

### 1. Clone the Repository
```bash
git clone https://github.com/norvaldb/timeforing-app.git
cd timeforing-app
```

### 2. Database Setup (Automated)
```bash
# Start Oracle XE database with automated user creation
./scripts/start-database.sh

# The script will:
# - Pull Oracle XE 21c image if needed
# - Start container with persistent storage
# - Create timeforing_user with proper privileges
# - Verify connectivity
```

**Expected Output:**
```
[INFO] Starting Oracle database with podman-compose...
[SUCCESS] Oracle database is ready!
[INFO] Checking if database user exists...
[SUCCESS] Database user already exists, skipping creation
[SUCCESS] Database connection verified!
Connection: localhost:1521/XEPDB1
User: timeforing_user
```

### 3. Backend Setup
```bash
# Install dependencies and compile
mvn clean compile

# Run tests
mvn test

# Start the Spring Boot application
mvn spring-boot:run
```

**Verify Backend:**
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### 4. Frontend Setup
```bash
# Navigate to frontend directory
cd timeforing-app-gui

# Install dependencies
npm install

# Start development server
npm run dev

# Run tests
npm test
```

**Verify Frontend:**
Open browser to `http://localhost:5173` to see the React application.

## 🐳 Database Management

### Database Scripts
```bash
# Start database (creates user if needed)
./scripts/start-database.sh

# Start fresh (removes all data)
./scripts/start-database.sh --recreate

# Stop database
./scripts/stop-database.sh

# Stop and remove all data
./scripts/stop-database.sh --remove
```

### Database Connection Details
- **Host**: localhost
- **Port**: 1521
- **Service**: XEPDB1
- **Username**: timeforing_user
- **Password**: TimeTrack123
- **JDBC URL**: `jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=XEPDB1)))`

### Manual Database Access
```bash
# Connect via container
podman exec -it timeforing-oracle sqlplus timeforing_user/TimeTrack123@localhost:1521/XEPDB1

# Check container status
podman-compose ps
```

## 🔍 IDE Setup

### IntelliJ IDEA (Recommended)
1. **Install Kotlin Plugin** (usually pre-installed)
2. **Import Project**: File → Open → Select `pom.xml`
3. **Configure JDK**: File → Project Structure → Project → SDK → Java 21
4. **Database Plugin**: Connect to Oracle using connection details above

### VS Code
Required extensions:
- Extension Pack for Java
- Kotlin Language
- Spring Boot Extension Pack
- Oracle Developer Tools

## 🧪 Testing

### Backend Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Frontend Tests
```bash
cd timeforing-app-gui

# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Run end-to-end tests
npm run test:e2e
```

## 🛠️ Development Workflow

### 1. Daily Development
```bash
# Start database
./scripts/start-database.sh

# Terminal 1: Backend
mvn spring-boot:run

# Terminal 2: Frontend
cd timeforing-app-gui && npm run dev

# Terminal 3: Development tasks
git status
mvn test
npm test
```

### 2. Database Schema Changes
```bash
# Create new Flyway migration
# Files go in src/main/resources/db/migration/
# Format: V{version}__{description}.sql

# Example: V2__Create_projects_table.sql
```

### 3. Code Quality
```bash
# Backend: Maven checks
mvn compile
mvn test

# Frontend: Lint and type checking
cd timeforing-app-gui
npm run lint
npm run type-check
```

## 🔧 Troubleshooting

### Common Issues

#### 1. Oracle Container Issues
```bash
# Check container logs
podman logs timeforing-oracle

# Restart database
./scripts/stop-database.sh
./scripts/start-database.sh
```

#### 2. Java/Maven Issues
```bash
# Check Java version
java --version

# Clear Maven cache
mvn clean

# Reimport in IDE
```

#### 3. Node.js/npm Issues
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
cd timeforing-app-gui
rm -rf node_modules package-lock.json
npm install
```

#### 4. Port Conflicts
- Backend: Default port 8080
- Frontend: Default port 5173  
- Database: Default port 1521

```bash
# Check what's using ports
lsof -i :8080
lsof -i :5173
lsof -i :1521
```

### Performance Tips

1. **Allocate sufficient memory** to Java: `export MAVEN_OPTS="-Xmx2048m"`
2. **Use SSD storage** for better container performance
3. **Close unused applications** when running full stack
4. **Use IDE incremental compilation** for faster development cycles

## 📚 Additional Resources

### Documentation
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [React Documentation](https://react.dev/)
- [Oracle XE Documentation](https://www.oracle.com/database/technologies/appdev/xe.html)
- [Podman Documentation](https://podman.io/getting-started/)

### Project-Specific
- Backend API: `http://localhost:8080/swagger-ui.html` (when implemented)
- Database Web UI: `http://localhost:5500/em` (Oracle Enterprise Manager)
- Frontend Dev Server: `http://localhost:5173`

## ✅ Environment Verification Checklist

Run these commands to verify your environment is ready:

```bash
# ✅ Check versions
java --version          # Should show Java 21+
mvn --version          # Should show Maven 3.9+
node --version         # Should show Node 18+
npm --version          # Should show npm 9+
podman --version       # Should show Podman 4+
podman-compose --version # Should show podman-compose 1.5+

# ✅ Start database
./scripts/start-database.sh

# ✅ Test backend
mvn clean compile
mvn test

# ✅ Test frontend
cd timeforing-app-gui
npm install
npm run build

# ✅ Verify connectivity
curl http://localhost:8080/actuator/health
```

If all commands succeed, your development environment is ready! 🎉

---

**Need Help?** Check the troubleshooting section above or create an issue in the repository.
