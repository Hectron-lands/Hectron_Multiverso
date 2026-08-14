#!/usr/bin/env python3
# HECTRON-01: llm_router.py
# Enrutador inteligente de modelos Soberano (LlamaEngine Local / Hybrid Gateway)

import os
import json
import time
from .llama_engine import LlamaEngine

class LLMRouter:
    """
    Enrutador cognitivo bajo principios de Soberanía Computacional HECTRON-Ψ.
    Nivel 0: Nube / API Externa
    Nivel 2: Híbrido (Evaluación local sensible + Procesamiento profundo externo)
    Nivel 4: Soberano Estricto (Inferencia 100% on-device / Edge ARM)
    """
    def __init__(self, default_sovereignty_level=4):
        self.sovereignty_level = int(os.environ.get("HECTRON_SOVEREIGNTY_LEVEL", default_sovereignty_level))
        self.engine = LlamaEngine()
        self.cache = {}
        self.thermal_throttle = False

    def query(self, prompt, system_prompt="You are HECTRON-Ψ sovereign core.", max_tokens=256, risk_sensitive=True):
        """
        Ejecuta la inferencia aplicando políticas de soberanía, privacidad y latencia.
        """
        start_time = time.time()

        # Validación de Soberanía: Si la consulta es sensible a privacidad, forzar nivel local (>=3)
        effective_level = self.sovereignty_level
        if risk_sensitive and effective_level < 3:
            effective_level = 3

        if effective_level >= 3:
            # Inferencia Local / Edge Soberana
            response = self.engine.generate(
                prompt=prompt,
                system_prompt=system_prompt,
                max_tokens=max_tokens
            )
            provenance = "EDGE_SOVEREIGN_LLAMA"
        else:
            # Inferencia Híbrida / Cloud fallback
            response = self._query_cloud_fallback(prompt, system_prompt)
            provenance = "CLOUD_HYBRID_GATEWAY"

        latency_ms = int((time.time() - start_time) * 1000)

        return {
            "content": response,
            "provenance": provenance,
            "sovereignty_level": effective_level,
            "latency_ms": latency_ms,
            "timestamp": time.time()
        }

    def _query_cloud_fallback(self, prompt, system_prompt):
        """Fallback remoto cuando el nivel de soberanía permite delegación."""
        return f"[HYBRID_RESPONSE] Análisis de datos consolidado: {prompt[:60]}... Estado nominal."

if __name__ == "__main__":
    router = LLMRouter()
    res = router.query("Calcula el índice de riesgo de la transacción social entre AGENT_ALPHA y AGENT_BETA.")
    print("[*] Router Output:")
    print(json.dumps(res, indent=2))
