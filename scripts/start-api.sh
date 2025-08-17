#!/bin/bash

# =============================================================================
# API and Database Startup Script for Timeforing App
# =============================================================================
# Script using podman-compose to start Oracle database and Spring Boot API
#
# Usage: 
#   ./scripts/start-api.sh                   - Start both database and API (default)
#   ./scripts/start-api.sh --create          - Recreate database volume and user, then start both
#   ./scripts/start-api.sh --db-only         - Start database only (for external API development)
#   ./scripts/start-api.sh --rebuild         - Force rebuild of API image and start both
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

# Handle command line options
START_API=true  # Default to starting both services
SHOULD_CREATE_USER=false
FORCE_REBUILD=false

for arg in "$@"; do
    case $arg in
        --create)
            SHOULD_CREATE_USER=true
            ;;
        --db-only)
            START_API=false
            ;;
        --rebuild)
            FORCE_REBUILD=true
            ;;
        *)
            log_error "Unknown option: $arg"
            echo "Usage: $0 [--create] [--db-only] [--rebuild]"
            exit 1
            ;;
    esac
done

# Handle create option
if [ "$SHOULD_CREATE_USER" = true ]; then
    log_info "Recreating database container and volume..."
    
    # Stop all services
    log_info "Stopping all services..."
    podman-compose down
    
    # Check if stopped containers exist and remove them
    if podman ps -a --format "{{.Names}}" | grep -q "timeforing-oracle"; then
        log_info "Removing existing timeforing-oracle container..."
        podman container rm timeforing-oracle 2>/dev/null || true
    fi
    
    if podman ps -a --format "{{.Names}}" | grep -q "timeforing-api"; then
        log_info "Removing existing timeforing-api container..."
        podman container rm timeforing-api 2>/dev/null || true
    fi
    
    # Check if volumes exist and remove them
    if podman volume ls --format "{{.Name}}" | grep -q "timeforing-app_oracle-data"; then
        log_info "Removing existing Oracle data volume..."
        podman volume rm timeforing-app_oracle-data 2>/dev/null || true
    fi
    
    if podman volume ls --format "{{.Name}}" | grep -q "timeforing-app_api-logs"; then
        log_info "Removing existing API logs volume..."
        podman volume rm timeforing-app_api-logs 2>/dev/null || true
    fi
    
    # Remove any lingering networks
    if podman network ls --format "{{.Name}}" | grep -q "timeforing-app_timeforing-network"; then
        log_info "Removing existing network..."
        podman network rm timeforing-app_timeforing-network 2>/dev/null || true
    fi
    
    log_success "Cleanup completed successfully"
fi

# Start services

# Enhanced logic: If --rebuild is used, only rebuild/restart API if DB is running
if [ "$START_API" = true ]; then
    if [ "$FORCE_REBUILD" = true ]; then
        if podman ps --format '{{.Names}}' | grep -q "timeforing-oracle"; then
            log_info "Database container is already running. Rebuilding/restarting API only..."
            podman-compose up -d --build --force-recreate api
        else
            log_info "Database container is not running. Rebuilding/restarting both database and API..."
            podman-compose up -d --build --force-recreate
        fi
    else
        log_info "Starting Oracle database and API..."
        podman-compose up -d --build
    fi
else
    log_info "Starting Oracle database only..."
    podman-compose up -d oracle
fi

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

log_success "Services startup completed!"
if [ "$SHOULD_CREATE_USER" = true ]; then
    echo ""
    echo "Database connection details:"
    echo "URL: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=XEPDB1)))"
    echo "User: timeforing_user"
    echo "Password: TimeTrack123"
    echo "Schema: TIMEFORING_USER"
fi

if [ "$START_API" = true ]; then
    echo ""
    echo "API Service Details:"
    echo "URL: http://localhost:8080"
    echo "Health Check: http://localhost:8080/actuator/health"
    echo "API Documentation: http://localhost:8080/swagger-ui.html"
    echo ""
    echo "To view API logs: podman logs -f timeforing-api"
    echo "To view database logs: podman logs -f timeforing-oracle"
fi
