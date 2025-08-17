#!/usr/bin/env bash
set -euo pipefail

OUT_DIR=target/generated-openapi
mkdir -p "$OUT_DIR"

# Find the first jar in target
JAR=$(ls -1 target/*.jar 2>/dev/null | head -n1 || true)
if [ -z "$JAR" ]; then
  echo "No jar found in target. Run 'mvn package' first." >&2
  exit 1
fi

PID_FILE=target/export-openapi.pid
LOG_FILE=target/export-openapi.log

# Start the app in background
nohup java -jar "$JAR" > "$LOG_FILE" 2>&1 &
PID=$!
echo $PID > "$PID_FILE"

echo "Started app (pid=$PID), waiting for /v3/api-docs..."
# Wait up to 60s for the API to respond
for i in $(seq 1 60); do
  if curl -sSf http://localhost:8080/v3/api-docs > /dev/null 2>&1; then
    echo "API is up"
    break
  fi
  sleep 1
done

# Fetch the OpenAPI JSON
curl -sSf http://localhost:8080/v3/api-docs -o "$OUT_DIR/api.json"

# Shutdown the app
if [ -f "$PID_FILE" ]; then
  echo "Stopping app (pid=$(cat $PID_FILE))"
  kill "$(cat $PID_FILE)" || true
  rm -f "$PID_FILE"
fi

echo "OpenAPI JSON saved to $OUT_DIR/api.json"
