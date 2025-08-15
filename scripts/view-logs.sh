#!/bin/bash

# =============================================================================
# Log Viewer Script for Timeforing App
# =============================================================================
# Simple script to view logs from containerized services
#
# Usage: 
#   ./scripts/view-logs.sh api      - View API logs (follow)
#   ./scripts/view-logs.sh db       - View database logs (follow)
#   ./scripts/view-logs.sh api --tail - View last 100 lines of API logs
#   ./scripts/view-logs.sh db --tail  - View last 100 lines of database logs
# =============================================================================

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Show usage if no arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 <service> [--tail]"
    echo ""
    echo "Services:"
    echo "  api    - View API logs"
    echo "  db     - View database logs"
    echo ""
    echo "Options:"
    echo "  --tail - Show last 100 lines instead of following"
    echo ""
    echo "Examples:"
    echo "  $0 api           # Follow API logs"
    echo "  $0 db --tail     # Show last 100 lines of database logs"
    exit 1
fi

SERVICE="$1"
MODE="${2:-follow}"

# Map service names to container names
case "$SERVICE" in
    api)
        CONTAINER_NAME="timeforing-api"
        SERVICE_NAME="API"
        ;;
    db|database)
        CONTAINER_NAME="timeforing-oracle"
        SERVICE_NAME="Database"
        ;;
    *)
        log_error "Unknown service: $SERVICE"
        echo "Available services: api, db"
        exit 1
        ;;
esac

# Check if container exists and is running
if ! podman ps --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    log_error "Container '$CONTAINER_NAME' is not running"
    echo "Start the services with: ./scripts/start-database.sh --api"
    exit 1
fi

# Show logs based on mode
case "$MODE" in
    --tail)
        log_info "Showing last 100 lines of $SERVICE_NAME logs..."
        podman logs --tail 100 "$CONTAINER_NAME"
        ;;
    follow|--follow|-f)
        log_info "Following $SERVICE_NAME logs (Ctrl+C to exit)..."
        podman logs -f "$CONTAINER_NAME"
        ;;
    *)
        log_error "Unknown mode: $MODE"
        echo "Available modes: --tail, --follow"
        exit 1
        ;;
esac
