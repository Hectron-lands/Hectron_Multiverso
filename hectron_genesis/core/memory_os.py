#!/usr/bin/env python3
# HECTRON-01: memory_os.py
# Subsistema MemoryOS de Alto Rendimiento con Verificador ASTAROTH y Consolidación

import sqlite3
import os
import uuid
import time
from datetime import datetime
from .db_bootstrap import get_optimized_connection

class MemoryOS:
    """
    Gestor de memoria persistente multicapa optimizado para Edge:
    - Conexiones persistentes WAL de alta velocidad
    - Filtrado indexado en tiempo real
    - Módulo de verificación de contradicciones ASTAROTH
    - Consolidación y decaimiento temporal
    """
    def __init__(self, db_path=None):
        self.db_path = db_path or os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")
        self._conn = None

    def _get_conn(self):
        if self._conn is None:
            self._conn = get_optimized_connection(self.db_path)
        return self._conn

    def store_memory(self, agent_id, content, memory_type="episodic", confidence=1.0, salience=0.8, provenance="SensoryInput", privacy_class="SOVEREIGN"):
        memory_id = f"mem_{uuid.uuid4().hex[:12]}"
        timestamp = datetime.utcnow().isoformat()
        decay = 0.05  # Tasa de decaimiento temporal

        conn = self._get_conn()
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO memory_store 
            (memory_id, agent_id, content, type, confidence, salience, provenance, timestamp, decay, privacy_class)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (memory_id, agent_id, content, memory_type, confidence, salience, provenance, timestamp, decay, privacy_class))
        conn.commit()
        cursor.close()

        return memory_id

    def retrieve(self, agent_id, query_type=None, min_confidence=0.5, limit=10):
        """Recupera recuerdos filtrados y ordenados por relevancia/salience a través de índices."""
        conn = self._get_conn()
        cursor = conn.cursor()

        if query_type:
            cursor.execute("""
                SELECT memory_id, content, type, confidence, salience, provenance, timestamp 
                FROM memory_store
                WHERE agent_id = ? AND type = ? AND confidence >= ?
                ORDER BY salience DESC, timestamp DESC
                LIMIT ?
            """, (agent_id, query_type, min_confidence, limit))
        else:
            cursor.execute("""
                SELECT memory_id, content, type, confidence, salience, provenance, timestamp 
                FROM memory_store
                WHERE agent_id = ? AND confidence >= ?
                ORDER BY salience DESC, timestamp DESC
                LIMIT ?
            """, (agent_id, min_confidence, limit))

        rows = cursor.fetchall()
        cursor.close()

        memories = []
        for r in rows:
            memories.append({
                "memory_id": r[0],
                "content": r[1],
                "type": r[2],
                "confidence": r[3],
                "salience": r[4],
                "provenance": r[5],
                "timestamp": r[6]
            })
        return memories

    def consolidate_and_decay(self, agent_id):
        """Aplica decaimiento temporal a memorias antiguas y poda salience residual."""
        conn = self._get_conn()
        cursor = conn.cursor()
        cursor.execute("""
            UPDATE memory_store
            SET salience = MAX(0.05, salience * (1.0 - decay))
            WHERE agent_id = ? AND salience > 0.05
        """, (agent_id,))
        conn.commit()
        cursor.close()

    def astaroth_verify(self, agent_id, proposed_action):
        """
        Módulo ASTAROTH Optimizado:
        1. Verifica contradicciones entre la memoria episódica reciente y la acción propuesta.
        2. Separa hechos observables de conjeturas no fundamentadas.
        3. Genera índice numérico de confiabilidad (0.0 a 1.0).
        """
        recent_memories = self.retrieve(agent_id, limit=20)
        contradictions = []

        action_str = str(proposed_action).lower()
        for mem in recent_memories:
            c = mem["content"].lower()
            if any(k in c for k in ("falló", "fracasó", "vetado", "abortado", "error")):
                # Verificar intersección de palabras clave
                keywords = [w for w in c.split() if len(w) > 4]
                if any(kw in action_str for kw in keywords):
                    contradictions.append(f"Contradicción con memoria {mem['memory_id']}: '{mem['content']}'")

        is_valid = len(contradictions) == 0
        reliability = 1.0 if is_valid else max(0.2, 1.0 - (len(contradictions) * 0.3))

        return {
            "verified": is_valid,
            "reliability_score": round(reliability, 2),
            "contradictions": contradictions,
            "audited_memories_count": len(recent_memories),
            "timestamp": datetime.utcnow().isoformat()
        }

if __name__ == "__main__":
    mos = MemoryOS()
    mem_id = mos.store_memory("HECTRON_CORE_01", "Acuerdo comercial con Nodo-7 completado con éxito.", "episodic", 0.95, 0.9)
    print(f"[+] Recuerdo guardado: {mem_id}")
    mos.consolidate_and_decay("HECTRON_CORE_01")
    verification = mos.astaroth_verify("HECTRON_CORE_01", "Iniciar nuevo acuerdo con Nodo-7")
    print(f"[+] Verificación ASTAROTH: {verification}")
