#!/bin/bash

# =============================================================================
# Database Stop Script for Timeforing App
# =============================================================================
# Simple script using podman-compose to stop Oracle database
# Volume is preserved - use start-database.sh --create to recreate from scratch
#
# Usage: ./scripts/stop-database.sh
# =============================================================================

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }

log_info "Stopping database container..."
podman-compose down

log_success "Database container stopped!"
log_info "Volume preserved. Use './scripts/start-database.sh --create' to recreate from scratch."
