#!/bin/bash

# =============================================================================
# API and Database Stop Script for Timeforing App
# =============================================================================
# Script using podman-compose to stop Oracle database and API services
# Volumes are preserved - use start-api.sh --create to recreate from scratch
#
# Usage: 
#   ./scripts/stop-api.sh        - Stop API only (default)
#   ./scripts/stop-api.sh --all  - Stop both API and database
#   ./scripts/stop-api.sh --db   - Stop database only
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

# Handle command line options
STOP_SERVICE=""

case "${1:-}" in
    --all)
        STOP_SERVICE="all"
        log_info "Stopping all containers..."
        ;;
    --db|--database)
        STOP_SERVICE="oracledb"
        log_info "Stopping database container only..."
        ;;
    "")
        STOP_SERVICE="api"
        log_info "Stopping API container only..."
        ;;
    *)
        log_warning "Unknown option: $1"
        echo "Usage: $0 [--all|--db]"
        exit 1
        ;;
esac

# Stop the specified service(s)
if [ "$STOP_SERVICE" = "all" ]; then
    podman-compose down
    log_success "All containers stopped!"
else
    podman-compose stop "$STOP_SERVICE"
    log_success "Container '$STOP_SERVICE' stopped!"
fi

log_info "Volumes preserved. Use './scripts/start-api.sh --create' to recreate from scratch."
