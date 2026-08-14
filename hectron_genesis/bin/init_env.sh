#!/usr/bin/env bash
# HECTRON-01: init_env.sh
# Verificación de entorno y dependencias para Termux/Edge

set -euo pipefail

# Adaptado para el entorno de trabajo actual
WORKSPACE="$(pwd)/hectron_genesis"
LOG_DIR="$WORKSPACE/logs"
DATA_DIR="$WORKSPACE/data"

echo "[*] Inicializando entorno HECTRON-01..."

# 1. Verificación de directorios base
mkdir -p "$WORKSPACE/bin" "$WORKSPACE/core" "$DATA_DIR" "$WORKSPACE/models" "$WORKSPACE/ui" "$LOG_DIR"

# 2. Verificación de dependencias mínimas
command -v python3 >/dev/null 2>&1 || { echo "[!] Error: python3 no está instalado."; exit 1; }
command -v sqlite3 >/dev/null 2>&1 || { echo "[!] Error: sqlite3 no está instalado."; exit 1; }

# 3. Inicialización de variables de entorno locales
export HECTRON_ROOT="$WORKSPACE"
export HECTRON_STATE="NORMAL"

echo "[+] Directorios y dependencias verificados correctamente."
echo "[+] Ruta base: $HECTRON_ROOT"
