#!/bin/bash
set -e
echo "HECTRON MULTIVERSO - Setup"
npm install
for service in api/*/ local-agent; do
  if [ -f "$service/package.json" ]; then
    (cd "$service" && npm install && cd ../..)
  fi
done
echo "Setup complete!"