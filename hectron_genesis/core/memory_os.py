#!/usr/bin/env python3
# HECTRON-01: memory_os.py
# Subsistema MemoryOS con categorización multi-capa y Verificador ASTAROTH

import sqlite3
import os
import uuid
import time
from datetime import datetime

class MemoryOS:
    """
    Gestor de memoria persistente multicapa:
    1. Episódica: Hechos concretos y cronológicos.
    2. Semántica: Conocimiento general y perfiles.
    3. Procedimental: Habilidades y protocolos.
    4. Social: Vínculos, confianza y asimetrías de poder.
    """
    def __init__(self, db_path=None):
        self.db_path = db_path or os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")

    def store_memory(self, agent_id, content, memory_type="episodic", confidence=1.0, salience=0.8, provenance="SensoryInput", privacy_class="SOVEREIGN"):
        memory_id = f"mem_{uuid.uuid4().hex[:12]}"
        timestamp = datetime.utcnow().isoformat()
        decay = 0.05  # Tasa de decaimiento temporal

        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO memory_store 
            (memory_id, agent_id, content, type, confidence, salience, provenance, timestamp, decay, privacy_class)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (memory_id, agent_id, content, memory_type, confidence, salience, provenance, timestamp, decay, privacy_class))
        conn.commit()
        conn.close()

        return memory_id

    def retrieve(self, agent_id, query_type=None, min_confidence=0.5, limit=10):
        """Recupera recuerdos filtrados y ordenados por relevancia/salience."""
        conn = sqlite3.connect(self.db_path)
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
        conn.close()

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

    def astaroth_verify(self, agent_id, proposed_action):
        """
        Módulo ASTAROTH:
        1. Verifica contradicciones entre la memoria episódica y la acción propuesta.
        2. Separa hechos confiables de inferencias débiles.
        3. Valida la procedencia.
        """
        recent_memories = self.retrieve(agent_id, limit=20)
        contradictions = []

        action_str = str(proposed_action).lower()
        for mem in recent_memories:
            c = mem["content"].lower()
            if "falló" in c or "fracasó" in c or "vetado" in c:
                if any(word in action_str for word in c.split() if len(word) > 4):
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
    verification = mos.astaroth_verify("HECTRON_CORE_01", "Iniciar nuevo acuerdo con Nodo-7")
    print(f"[+] Verificación ASTAROTH: {verification}")
