#!/bin/bash

# =============================================================================
# Database Stop Script for Timeforing App
# =============================================================================
# Simple script using podman-compose to stop Oracle database
#
# Usage: ./scripts/stop-database.sh [--remove]
# =============================================================================

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }

# Check if podman-compose is available
if command -v podman-compose &> /dev/null; then
    COMPOSE_CMD="podman-compose"
    USE_COMPOSE=true
else
    log_warning "podman-compose not found, using direct podman commands"
    USE_COMPOSE=false
fi

if [ "$1" = "--remove" ]; then
    log_info "Stopping and removing database container..."
    if [ "$USE_COMPOSE" = true ]; then
        $COMPOSE_CMD down -v
    else
        podman stop timeforing-oracle 2>/dev/null || true
        podman rm timeforing-oracle 2>/dev/null || true
        podman volume rm oracle-data 2>/dev/null || true
    fi
    log_success "Database container removed!"
else
    log_info "Stopping database container..."
    if [ "$USE_COMPOSE" = true ]; then
        $COMPOSE_CMD stop
    else
        podman stop timeforing-oracle 2>/dev/null || true
    fi
    log_success "Database container stopped!"
fi
