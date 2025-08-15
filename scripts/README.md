# API Management Scripts

This directory contains scripts to manage the Oracle XE database and Spring Boot API containers for the Timeforing application.

## Prerequisites

- Podman and podman-compose installed and running
- Oracle XE image: `container-registry.oracle.com/database/express:latest`

## Scripts

### start-api.sh

Starts both the Oracle XE database and Spring Boot API containers with **automated setup**.

```bash
# Start both database and API (default)
./scripts/start-api.sh

# Recreate database container and user from scratch
./scripts/start-api.sh --create

# Start database only (for external API development)
./scripts/start-api.sh --db-only
```

**Features:**
- Uses podman-compose for orchestration
- Handles container dependencies gracefully
- **Automatically creates `timeforing_user` with proper privileges**
- **Builds and deploys API container automatically**
- **Works from scratch - no manual setup required**
- Verifies database connectivity
- Configures container networking

**Automated Setup:**
The script automatically:
1. Builds Spring Boot API Docker image
2. Waits for Oracle database to be fully ready
3. Creates the `timeforing_user` if `--create` is used
4. Grants necessary privileges (CONNECT, RESOURCE, CREATE TABLE, etc.)
5. Starts API container with proper database connection
6. Verifies the setup by testing connection

### stop-api.sh

Stops the Oracle XE database and API containers.

```bash
# Stop all services
./scripts/stop-api.sh

# Stop database only
./scripts/stop-api.sh --db

# Stop API only
./scripts/stop-api.sh --api
```

### view-logs.sh

View logs from running containers.

```bash
# View API logs
./scripts/view-logs.sh

# View database logs
./scripts/view-logs.sh --db

# Follow logs in real-time
./scripts/view-logs.sh --follow
```

## Container Configuration

- **Database Container**: `timeforing-oracle`
- **API Container**: `timeforing-api`
- **Image**: `container-registry.oracle.com/database/express:latest`
- **Ports**: 
  - 1521 (Oracle TNS listener)
  - 5500 (Oracle Enterprise Manager)
  - 8080 (Spring Boot API)
- **Database**: XEPDB1 (Pluggable Database)
- **User**: `timeforing_user` / `TimeTrack123`
- **Volumes**: `oracle-data` (persistent storage), `api-logs` (API logs)

## Connection Details

Once the services are running, you can access:

**Database:**
- **JDBC URL**: `jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=XEPDB1)))`
- **Username**: `timeforing_user`
- **Password**: `TimeTrack123`
- **Service**: `XEPDB1`

**API:**
- **URL**: `http://localhost:8080`
- **Health Check**: `http://localhost:8080/actuator/health`
- **API Documentation**: `http://localhost:8080/swagger-ui.html`

## Troubleshooting

If the scripts fail:

1. **Check container status**: `podman ps`
2. **Check container logs**: 
   ```bash
   podman logs timeforing-oracle
   podman logs timeforing-api
   ```
3. **Manual database connection test**: 
   ```bash
   podman exec timeforing-oracle sqlplus timeforing_user/TimeTrack123@localhost:1521/XEPDB1
   ```
4. **Manual API test**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

## Podman Compose Configuration

A `docker-compose.yml` file is provided for environments with podman-compose support:

```bash
# If podman-compose is available
podman-compose up -d
podman-compose down
```
