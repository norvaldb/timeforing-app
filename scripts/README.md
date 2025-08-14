# Database Management Scripts

This directory contains scripts to manage the Oracle XE database container for the Timeforing application.

## Prerequisites

- Podman installed and running
- Oracle XE image: `container-registry.oracle.com/database/express:latest`

## Scripts

### start-database.sh

Starts the Oracle XE database container with **automated user setup**.

```bash
# Start database (uses existing container if available)
./scripts/start-database.sh

# Recreate database container from scratch
./scripts/start-database.sh --recreate
```

**Features:**
- Automatically detects if podman-compose is available
- Falls back to direct podman commands if needed
- Handles existing containers gracefully
- **Automatically creates `timeforing_user` with proper privileges**
- **Works from scratch - no manual database setup required**
- Verifies database connectivity
- Uses the same image and configuration as manually created containers

**Automated Setup:**
The script automatically:
1. Waits for Oracle database to be fully ready
2. Creates the `timeforing_user` if it doesn't exist
3. Grants necessary privileges (CONNECT, RESOURCE, CREATE TABLE, etc.)
4. Verifies the setup by testing connection

### stop-database.sh

Stops the Oracle XE database container.

```bash
# Stop database container
./scripts/stop-database.sh

# Stop and remove container + volume
./scripts/stop-database.sh --remove
```

## Container Configuration

- **Container Name**: `timeforing-oracle`
- **Image**: `container-registry.oracle.com/database/express:latest`
- **Ports**: 
  - 1521 (Oracle TNS listener)
  - 5500 (Oracle Enterprise Manager)
- **Database**: XEPDB1 (Pluggable Database)
- **User**: `timeforing_user` / `TimeTrack123`
- **Volume**: `oracle-data` (persistent storage)

## Connection Details

Once the database is running, you can connect using:

- **JDBC URL**: `jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=XEPDB1)))`
- **Username**: `timeforing_user`
- **Password**: `TimeTrack123`
- **Service**: `XEPDB1`

## Troubleshooting

If the scripts fail:

1. **Check Podman status**: `podman ps`
2. **Check container logs**: `podman logs timeforing-oracle`
3. **Manual connection test**: 
   ```bash
   podman exec timeforing-oracle sqlplus timeforing_user/TimeTrack123@localhost:1521/XEPDB1
   ```

## Docker Compose Alternative

A `docker-compose.yml` file is provided for environments with podman-compose support:

```bash
# If podman-compose is available
podman-compose up -d
podman-compose down
```
