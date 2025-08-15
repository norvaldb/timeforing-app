#!/bin/bash

# =============================================================================
# Database Startup Script for Timeforing App
# =============================================================================
# Simple script using podman-compose to start Oracle database
#
# Usage: 
#   ./scripts/start-database.sh        - Start database (normal)
#   ./scripts/start-database.sh --create - Recreate database volume and user
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

# Handle create option
if [ "$1" = "--create" ]; then
    log_info "Recreating database container and volume..."
    
    # Check if container is running and stop it
    if podman ps --format "{{.Names}}" | grep -q "timeforing-oracle"; then
        log_info "Stopping running timeforing-oracle container..."
        podman-compose down
    fi
    
    # Check if stopped container exists and remove it
    if podman ps -a --format "{{.Names}}" | grep -q "timeforing-oracle"; then
        log_info "Removing existing timeforing-oracle container..."
        podman container rm timeforing-oracle 2>/dev/null || true
    fi
    
    # Check if volume exists and remove it
    if podman volume ls --format "{{.Name}}" | grep -q "timeforing-app_oracle-data"; then
        log_info "Removing existing Oracle data volume..."
        podman volume rm timeforing-app_oracle-data 2>/dev/null || true
    fi
    
    # Remove any lingering networks
    if podman network ls --format "{{.Name}}" | grep -q "timeforing-app_default"; then
        log_info "Removing existing network..."
        podman network rm timeforing-app_default 2>/dev/null || true
    fi
    
    log_success "Cleanup completed successfully"
    SHOULD_CREATE_USER=true
else
    SHOULD_CREATE_USER=false
fi

# Start database
log_info "Starting Oracle database..."
podman-compose up -d

# Wait for Oracle to be fully ready
wait_for_oracle() {
    log_info "Waiting for Oracle database to be fully ready..."
    local max_attempts=60  # Increased from 30 to 60 (5 minutes total)
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        # Test connection to the pluggable database specifically
        if podman exec timeforing-oracle bash -c "echo 'SELECT 1 FROM dual;' | sqlplus -s 'sys/TimeForing123!@XEPDB1' as sysdba" &>/dev/null; then
            log_success "Oracle database is ready!"
            # Give it a few more seconds to ensure it's fully stable
            sleep 10
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

# Setup database user
setup_database_user() {
    log_info "Setting up database user and schema..."
    
    # Wait a bit more to ensure Oracle is completely ready for user creation
    sleep 5
    
    # Create user with retry logic
    local max_attempts=3
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        log_info "Attempting to create user (attempt $attempt/$max_attempts)..."
        
        if podman exec timeforing-oracle bash -c 'echo "CREATE USER timeforing_user IDENTIFIED BY TimeTrack123;
GRANT CONNECT, RESOURCE, CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO timeforing_user;
GRANT UNLIMITED TABLESPACE TO timeforing_user;
ALTER USER timeforing_user DEFAULT TABLESPACE USERS;
EXIT;" | sqlplus -s sys/TimeForing123!@XEPDB1 as sysdba' > /tmp/setup_output.log 2>&1; then
            log_success "Database user and schema created"
            return 0
        else
            log_warning "Attempt $attempt failed, waiting 10 seconds before retry..."
            cat /tmp/setup_output.log
            if [ $attempt -lt $max_attempts ]; then
                sleep 10
            fi
            ((attempt++))
        fi
    done
    
    log_error "Failed to create database user after $max_attempts attempts"
    exit 1
}

# Wait for Oracle to be ready first
wait_for_oracle

# Create user only if --create option was used
if [ "$SHOULD_CREATE_USER" = true ]; then
    setup_database_user
fi

# Verify connection (only if user should exist)
if [ "$SHOULD_CREATE_USER" = true ]; then
    log_info "Verifying database connection..."
    if podman exec timeforing-oracle bash -c 'echo "SELECT USER FROM dual;" | sqlplus -s timeforing_user/TimeTrack123@localhost:1521/XEPDB1' &>/dev/null; then
        log_success "Database connection verified!"
    else
        log_warning "User connection failed, may need more time to be activated"
    fi
fi

log_success "Database startup completed!"
if [ "$SHOULD_CREATE_USER" = true ]; then
    echo ""
    echo "Database connection details:"
    echo "URL: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=XEPDB1)))"
    echo "User: timeforing_user"
    echo "Password: TimeTrack123"
    echo "Schema: TIMEFORING_USER"
fi
