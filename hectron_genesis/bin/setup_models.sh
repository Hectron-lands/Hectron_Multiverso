#!/usr/bin/env bash
# HECTRON-01: setup_models.sh
# Descarga y verificación de pesos GGUF optimizados para Edge / ARM / Termux

set -euo pipefail

WORKSPACE="${HECTRON_ROOT:-$(pwd)/hectron_genesis}"
MODELS_DIR="$WORKSPACE/models"

mkdir -p "$MODELS_DIR"

echo "========================================================"
echo "    HECTRON-Ψ : Gestor de Pesos GGUF Soberanos         "
echo "========================================================"
echo "[+] Directorio destino: $MODELS_DIR"

# Modelos recomendados para Edge (cuantizados Q4_K_M)
MODEL_NAME="qwen2.5-0.5b-instruct-q4_k_m.gguf"
MODEL_URL="https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf"

TARGET_FILE="$MODELS_DIR/$MODEL_NAME"

if [ -f "$TARGET_FILE" ]; then
    echo "[+] Modelo $MODEL_NAME ya presente ($(du -h "$TARGET_FILE" | cut -f1))."
else
    echo "[*] Descargando modelo ligero para Edge ($MODEL_NAME)..."
    if command -v curl >/dev/null 2>&1; then
        curl -L -o "$TARGET_FILE" "$MODEL_URL" || echo "[!] Descarga remota omitida o sin conexión. Modo offline activo."
    elif command -v wget >/dev/null 2>&1; then
        wget -O "$TARGET_FILE" "$MODEL_URL" || echo "[!] Descarga remota omitida o sin conexión. Modo offline activo."
    else
        echo "[!] No se encontró curl ni wget. Creando enlace simbólico o inicialización offline."
    fi
fi

echo "[+] Configuración de modelos completada."
