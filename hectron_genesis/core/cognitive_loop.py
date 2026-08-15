#!/usr/bin/env python3
# HECTRON-01: cognitive_loop.py
# Núcleo del ciclo de control cognitivo cerrado Optimizado (High-Throughput / Sub-millisecond Event Queue)

import time
import uuid
import sqlite3
import os
import json
import threading
import queue
from datetime import datetime
from .llm_router import LLMRouter
from .memory_os import MemoryOS
from .social_matrix import SocialMatrix
from .db_bootstrap import get_optimized_connection

class CognitiveLoop:
    def __init__(self, agent_id="HECTRON_CORE_01"):
        self.agent_id = agent_id
        self.running = True
        self.router = LLMRouter()
        self.memory_os = MemoryOS()
        self.social_matrix = SocialMatrix()
        self.db_path = os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")
        
        # Cola asíncrona de eventos para no bloquear el ciclo de inferencia
        self._event_queue = queue.Queue(maxsize=1000)
        self._db_worker_thread = threading.Thread(target=self._event_flusher_worker, daemon=True)
        self._db_worker_thread.start()

        # Métricas de Telemetría del Sistema
        self.telemetry = {
            "cycle_count": 0,
            "avg_latency_ms": 0.0,
            "astaroth_avg_score": 1.0,
            "last_tick_time": time.time()
        }

    def _event_flusher_worker(self):
        """Worker en segundo plano para volcar eventos por lotes (Batch Insert)."""
        conn = get_optimized_connection(self.db_path)
        while self.running:
            try:
                events_batch = []
                # Obtener primer elemento bloqueante
                item = self._event_queue.get(timeout=1.0)
                events_batch.append(item)
                
                # Obtener elementos acumulados
                while not self._event_queue.empty() and len(events_batch) < 50:
                    events_batch.append(self._event_queue.get_nowait())

                if events_batch:
                    cursor = conn.cursor()
                    cursor.executemany("""
                        INSERT INTO event_log (event_id, timestamp, agent_id, event_type, payload, trace_id, source)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, events_batch)
                    conn.commit()
                    cursor.close()
            except queue.Empty:
                continue
            except Exception as e:
                print(f"[!] Error en batch flusher: {e}")
                time.sleep(0.5)
        conn.close()

    def _log_event(self, event_type, payload, trace_id):
        event_tuple = (
            str(uuid.uuid4()),
            datetime.utcnow().isoformat(),
            self.agent_id,
            event_type,
            str(payload),
            trace_id,
            "CognitiveLoop"
        )
        try:
            self._event_queue.put_nowait(event_tuple)
        except queue.Full:
            pass

    def observe(self):
        """Etapa 1: Captura de entradas del entorno y telemetría de agentes."""
        timestamp = datetime.utcnow().isoformat()
        return {
            "timestamp": timestamp,
            "status": "nominal",
            "ambient_state": "active_economy",
            "network_nodes_observed": 5,
            "external_events": ["market_tick", "social_exchange"]
        }

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
                "requires_auth": True,
                "astaroth_score": verification["reliability_score"]
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
        
        if "STRATEGIC_TRANSACTION" in decision["action"]:
            self.social_matrix.record_interaction(
                agent_source=self.agent_id,
                agent_target="FACTION_ALLIED",
                commitment_fulfilled=True,
                resource_exchanged=50.0
            )

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
        """Ejecución de un ciclo cognitivo completo con medición de rendimiento."""
        start = time.time()
        obs = self.observe()
        mem = self.remember(obs)
        inf = self.infer(obs, mem)
        dec = self.decide(inf, obs, mem)
        trace_id = self.act(dec)
        duration_ms = (time.time() - start) * 1000.0

        # Actualizar telemetría
        self.telemetry["cycle_count"] += 1
        n = self.telemetry["cycle_count"]
        self.telemetry["avg_latency_ms"] = round(((self.telemetry["avg_latency_ms"] * (n - 1)) + duration_ms) / n, 2)
        self.telemetry["astaroth_avg_score"] = dec.get("astaroth_score", 1.0)
        self.telemetry["last_tick_time"] = time.time()

        # Decaimiento periódico de memoria cada 10 ciclos
        if n % 10 == 0:
            self.memory_os.consolidate_and_decay(self.agent_id)

        return trace_id, duration_ms

    def run(self, cycles=3):
        print(f"[*] Iniciando ciclo cognitivo optimizado HECTRON-Ψ para: {self.agent_id}")
        for i in range(cycles):
            trace_id, latency = self.tick()
            print(f"--- Ticker [{i+1}/{cycles}] | Trace: {trace_id} | Latencia: {latency:.2f}ms | ASTAROTH: {self.telemetry['astaroth_avg_score']} ---")
            time.sleep(0.5)

if __name__ == "__main__":
    loop = CognitiveLoop()
    loop.run(cycles=3)
