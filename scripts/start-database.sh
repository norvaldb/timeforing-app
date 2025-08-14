#!/bin/bash

# =============================================================================
# Database Startup Script for Timeforing App
# =============================================================================
# Simple script using podman-compose to start Oracle database
#
# Usage: ./scripts/start-database.sh [--recreate]
# =============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Check if podman-compose is available, otherwise use basic podman commands
if command -v podman-compose &> /dev/null; then
    COMPOSE_CMD="podman-compose"
    USE_COMPOSE=true
else
    log_warning "podman-compose not found, using direct podman commands"
    USE_COMPOSE=false
fi

# Handle recreate option
if [ "$1" = "--recreate" ]; then
    log_info "Recreating database container..."
    if [ "$USE_COMPOSE" = true ]; then
        $COMPOSE_CMD down -v
    else
        podman stop timeforing-oracle 2>/dev/null || true
        podman rm timeforing-oracle 2>/dev/null || true
        podman volume rm oracle-data 2>/dev/null || true
    fi
fi

# Create volume if needed
if [ "$USE_COMPOSE" = false ]; then
    podman volume create oracle-data 2>/dev/null || true
fi

# Start database
if [ "$USE_COMPOSE" = true ]; then
    log_info "Starting Oracle database with $COMPOSE_CMD..."
    $COMPOSE_CMD up -d
else
    log_info "Starting Oracle database with podman..."
    
    # Check if container already exists and is running
    if podman ps --format "{{.Names}}" | grep -q "timeforing-oracle"; then
        log_success "Container is already running!"
    elif podman ps -a --format "{{.Names}}" | grep -q "timeforing-oracle"; then
        log_info "Starting existing container..."
        podman start timeforing-oracle
    else
        log_info "Creating new container..."
        podman run -d \
            --name timeforing-oracle \
            -p 1521:1521 \
            -p 5500:5500 \
            -e ORACLE_PWD=TimeForing123! \
            -v oracle-data:/opt/oracle/oradata \
            container-registry.oracle.com/database/express:latest
    fi
fi

# Wait for database
log_info "Waiting for container to be ready..."
sleep 30  # Give Oracle time to initialize

# Check if database user exists
check_user_exists() {
    log_info "Checking if database user exists..."
    
    # Try to connect directly as the user - if successful, user exists
    if podman exec timeforing-oracle bash -c "echo 'SELECT 1 FROM dual;' | sqlplus -s timeforing_user/TimeTrack123@localhost:1521/XEPDB1" &>/dev/null; then
        log_success "Database user already exists, skipping creation"
        return 0
    else
        log_info "Database user does not exist or cannot connect, will create/setup"
        return 1
    fi
}

# Setup database user if needed
setup_database_user() {
    log_info "Setting up database user..."
    
    # Create setup script
    podman exec timeforing-oracle bash -c "cat > /tmp/setup_user.sql << 'EOF'
-- Connect to the pluggable database
ALTER SESSION SET CONTAINER = XEPDB1;

-- Create user if not exists (ignore error if user already exists)
BEGIN
    EXECUTE IMMEDIATE 'CREATE USER timeforing_user IDENTIFIED BY TimeTrack123 DEFAULT TABLESPACE USERS TEMPORARY TABLESPACE TEMP';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1920 THEN -- User already exists
            RAISE;
        END IF;
END;
/

-- Grant necessary privileges
GRANT CONNECT, RESOURCE, CREATE SESSION TO timeforing_user;
GRANT CREATE TABLE, CREATE VIEW, CREATE PROCEDURE TO timeforing_user;
GRANT UNLIMITED TABLESPACE TO timeforing_user;

-- Verify user exists
SELECT 'User created successfully' as status FROM dual WHERE EXISTS (
    SELECT 1 FROM dba_users WHERE username = 'TIMEFORING_USER'
);

EXIT;
EOF"
    
    # Execute the setup script
    if podman exec timeforing-oracle bash -c "sqlplus -s 'sys/TimeForing123!@localhost:1521/XEPDB1' as sysdba @/tmp/setup_user.sql" > /tmp/setup_output.log 2>&1; then
        log_success "Database user setup completed"
    else
        log_warning "User setup had issues, but may already exist"
    fi
    
    # Clean up
    podman exec timeforing-oracle rm -f /tmp/setup_user.sql
}

# Wait for Oracle to be fully ready
wait_for_oracle() {
    log_info "Waiting for Oracle database to be fully ready..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if podman exec timeforing-oracle bash -c "echo 'SELECT 1 FROM dual;' | sqlplus -s 'sys/TimeForing123!@localhost:1521/XEPDB1' as sysdba" &>/dev/null; then
            log_success "Oracle database is ready!"
            return 0
        fi
        
        echo -n "."
        sleep 5
        ((attempt++))
    done
    
    echo ""
    log_warning "Oracle may still be initializing..."
    return 1
}

# Wait for Oracle to be ready first
wait_for_oracle

# Check if user exists, and create only if needed
if ! check_user_exists; then
    setup_database_user
fi

# Verify connection
log_info "Verifying database connection..."
if podman exec timeforing-oracle bash -c "echo 'SELECT USER FROM dual;' | sqlplus -s timeforing_user/TimeTrack123@localhost:1521/XEPDB1" &>/dev/null; then
    log_success "Database connection verified!"
    echo "Connection: localhost:1521/XEPDB1"
    echo "User: timeforing_user"
    echo "Password: TimeTrack123"
else
    log_warning "Direct connection test failed"
    log_info "User may need more time to be activated, try again in a few minutes"
fi

log_success "Database startup completed!"
