#!/usr/bin/env python3
# HECTRON-01: cognitive_loop.py
# Núcleo del ciclo de control cognitivo cerrado (BrainOS + MemoryOS + ASTAROTH + SocialMatrix + LlamaEngine)

import time
import uuid
import sqlite3
import os
import json
from datetime import datetime
from .llm_router import LLMRouter
from .memory_os import MemoryOS
from .social_matrix import SocialMatrix

class CognitiveLoop:
    def __init__(self, agent_id="HECTRON_CORE_01"):
        self.agent_id = agent_id
        self.running = True
        self.router = LLMRouter()
        self.memory_os = MemoryOS()
        self.social_matrix = SocialMatrix()
        self.db_path = os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")

    def _log_event(self, event_type, payload, trace_id):
        try:
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            cursor.execute("""
                INSERT INTO event_log (event_id, timestamp, agent_id, event_type, payload, trace_id, source)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """, (str(uuid.uuid4()), datetime.utcnow().isoformat(), self.agent_id, event_type, str(payload), trace_id, "CognitiveLoop"))
            conn.commit()
            conn.close()
        except Exception as e:
            print(f"[!] Error logueando evento: {e}")

    def observe(self):
        """Etapa 1: Captura de entradas del entorno y telemetría de agentes."""
        timestamp = datetime.utcnow().isoformat()
        obs = {
            "timestamp": timestamp,
            "status": "nominal",
            "ambient_state": "active_economy",
            "network_nodes_observed": 5,
            "external_events": ["market_tick", "social_exchange"]
        }
        return obs

    def remember(self, observation):
        """Etapa 2: Recuperación selectiva desde MemoryOS (episódica, social, procedimental)."""
        episodic = self.memory_os.retrieve(self.agent_id, query_type="episodic", limit=3)
        social = self.social_matrix.evaluate_relationship(self.agent_id, "FACTION_ALLIED")
        return {
            "recent_episodic": episodic,
            "social_stance": social,
            "loaded_count": len(episodic)
        }

    def infer(self, observation, memory):
        """Etapa 3: Evaluación epistemológica y de riesgos mediante LLMRouter."""
        prompt = (f"Observación: {json.dumps(observation)}. "
                  f"Memoria: {json.dumps(memory)}. "
                  "Calcula el riesgo operacional y define la hipótesis táctica.")
        
        llm_result = self.router.query(prompt, max_tokens=180)
        
        # Cálculo de poder estructural del agente en este ciclo
        structural_power = self.social_matrix.calculate_structural_power(0.85, 0.70, 0.80, 0.65)
        
        return {
            "risk_level": 0.12,
            "structural_power": structural_power,
            "hypothesis": llm_result["content"],
            "provenance": llm_result["provenance"],
            "latency_ms": llm_result["latency_ms"]
        }

    def decide(self, inference, observation, memory):
        """Etapa 4: BrainOS - Política de decisión bajo gobernanza y filtro ASTAROTH."""
        proposed_action = {
            "type": "STABILIZE_AND_TRADE",
            "target": "FACTION_ALLIED",
            "allocated_resources": 50.0,
            "priority": "HIGH"
        }

        # Verificación crítica ASTAROTH
        verification = self.memory_os.astaroth_verify(self.agent_id, proposed_action)

        if not verification["verified"]:
            return {
                "action": "HALT_AND_REASSESS",
                "reason": "ASTAROTH_CONTRADICTION_DETECTED",
                "details": verification["contradictions"],
                "requires_auth": True
            }

        return {
            "action": "EXECUTE_STRATEGIC_TRANSACTION",
            "payload": proposed_action,
            "astaroth_score": verification["reliability_score"],
            "requires_auth": False
        }

    def act(self, decision):
        """Etapa 5: Ejecución controlada y trazabilidad inmutable."""
        trace_id = f"trc_{uuid.uuid4().hex[:8]}"
        print(f"[TRACE {trace_id}] Acción decidida: {decision['action']}")
        
        # Si la acción implica interacción social, registrar en la matriz
        if "STRATEGIC_TRANSACTION" in decision["action"]:
            self.social_matrix.record_interaction(
                agent_source=self.agent_id,
                agent_target="FACTION_ALLIED",
                commitment_fulfilled=True,
                resource_exchanged=50.0
            )

        # Grabar en Memoria Episódica persistente
        self.memory_os.store_memory(
            agent_id=self.agent_id,
            content=f"Ejecutada acción {decision['action']} con score ASTAROTH {decision.get('astaroth_score', 1.0)}",
            memory_type="episodic",
            confidence=0.95,
            provenance=f"ExecutionTrace:{trace_id}"
        )

        self._log_event("COGNITIVE_ACTION", decision, trace_id)
        return trace_id

    def tick(self):
        """Ejecución de un ciclo cognitivo completo."""
        obs = self.observe()
        mem = self.remember(obs)
        inf = self.infer(obs, mem)
        dec = self.decide(inf, obs, mem)
        self.act(dec)

    def run(self, cycles=3):
        print(f"[*] Iniciando ciclo cognitivo persistente HECTRON-Ψ para: {self.agent_id}")
        for i in range(cycles):
            print(f"\n--- Ticker Ciclo [{i+1}/{cycles}] ---")
            self.tick()
            time.sleep(1)

if __name__ == "__main__":
    loop = CognitiveLoop()
    loop.run(cycles=3)
