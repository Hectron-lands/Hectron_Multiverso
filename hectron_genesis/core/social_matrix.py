#!/usr/bin/env python3
# HECTRON-01: social_matrix.py
# Motor de Dinámicas de Poder Estructural y Matriz Social

import sqlite3
import os
import math
from datetime import datetime

class SocialMatrix:
    """
    Modelado de Poder Estructural y Relaciones Sociales:
    El poder NO es psicológico, es ESTRUCTURAL:
    1. Control de Recursos (Rc)
    2. Asimetría de Información (Ia)
    3. Alternativas de Salida / BATNA (Ea)
    4. Centralidad en la Red (Nc)
    """
    def __init__(self, db_path=None):
        self.db_path = db_path or os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")

    def calculate_structural_power(self, resource_control, info_asymmetry, batna_score, network_centrality):
        """
        Calcula el índice de poder estructural (P_e) de 0.0 a 1.0.
        P_e = w1*Rc + w2*Ia + w3*Ea + w4*Nc
        """
        w1, w2, w3, w4 = 0.35, 0.25, 0.25, 0.15
        power = (w1 * min(1.0, max(0.0, resource_control)) +
                 w2 * min(1.0, max(0.0, info_asymmetry)) +
                 w3 * min(1.0, max(0.0, batna_score)) +
                 w4 * min(1.0, max(0.0, network_centrality)))
        return round(power, 4)

    def record_interaction(self, agent_source, agent_target, commitment_fulfilled=True, resource_exchanged=0.0, conflict_delta=0.0):
        """
        Registra una interacción observable y actualiza la matriz relacional basada en evidencia.
        """
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        # Recuperar estado previo si existe
        cursor.execute("""
            SELECT trust, conflict, affinity FROM social_matrix 
            WHERE agent_source = ? AND agent_target = ?
        """, (agent_source, agent_target))
        row = cursor.fetchone()

        if row:
            curr_trust, curr_conflict, curr_affinity = row[0], row[1], row[2]
        else:
            curr_trust, curr_conflict, curr_affinity = 0.5, 0.0, 0.5

        # Actualización bayesiana gradual de confianza (Trust)
        alpha = 0.15
        if commitment_fulfilled:
            new_trust = min(1.0, curr_trust + alpha * (1.0 - curr_trust))
            new_conflict = max(0.0, curr_conflict - 0.1)
            new_affinity = min(1.0, curr_affinity + 0.05)
        else:
            # Penalización asimétrica por incumplimiento (la confianza cae más rápido de lo que sube)
            new_trust = max(0.0, curr_trust - (alpha * 2.5 * curr_trust))
            new_conflict = min(1.0, curr_conflict + 0.3 + conflict_delta)
            new_affinity = max(0.0, curr_affinity - 0.2)

        timestamp = datetime.utcnow().isoformat()

        cursor.execute("""
            INSERT INTO social_matrix (agent_source, agent_target, trust, conflict, affinity, last_updated)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT(agent_source, agent_target) DO UPDATE SET
                trust = excluded.trust,
                conflict = excluded.conflict,
                affinity = excluded.affinity,
                last_updated = excluded.last_updated
        """, (agent_source, agent_target, round(new_trust, 4), round(new_conflict, 4), round(new_affinity, 4), timestamp))

        conn.commit()
        conn.close()

        return {
            "source": agent_source,
            "target": agent_target,
            "trust": round(new_trust, 4),
            "conflict": round(new_conflict, 4),
            "affinity": round(new_affinity, 4)
        }

    def evaluate_relationship(self, agent_source, agent_target):
        """Consulta la postura social y calcula el riesgo de interacción."""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute("""
            SELECT trust, conflict, affinity, last_updated FROM social_matrix 
            WHERE agent_source = ? AND agent_target = ?
        """, (agent_source, agent_target))
        row = cursor.fetchone()
        conn.close()

        if not row:
            return {
                "source": agent_source,
                "target": agent_target,
                "trust": 0.5,
                "conflict": 0.0,
                "affinity": 0.5,
                "stance": "NEUTRAL",
                "interaction_risk": 0.25
            }

        trust, conflict, affinity = row[0], row[1], row[2]
        
        # Clasificación de postura relacional
        if conflict > 0.6:
            stance = "HOSTILE_OR_COMPETITIVE"
        elif trust > 0.75 and conflict < 0.2:
            stance = "STRATEGIC_ALLIANCE"
        elif trust < 0.3:
            stance = "LOW_CONFIDENCE"
        else:
            stance = "COOPERATIVE_TRANSACTIONAL"

        risk = round((1.0 - trust) * 0.6 + conflict * 0.4, 3)

        return {
            "source": agent_source,
            "target": agent_target,
            "trust": trust,
            "conflict": conflict,
            "affinity": affinity,
            "stance": stance,
            "interaction_risk": risk,
            "last_updated": row[3]
        }

if __name__ == "__main__":
    sm = SocialMatrix()
    res1 = sm.record_interaction("HECTRON_CORE_01", "FACTION_MINERS", commitment_fulfilled=True, resource_exchanged=500.0)
    print(f"[+] Interacción exitosa: {res1}")
    power = sm.calculate_structural_power(0.8, 0.6, 0.9, 0.7)
    print(f"[+] Poder Estructural HECTRON_CORE_01: {power}")
    eval_res = sm.evaluate_relationship("HECTRON_CORE_01", "FACTION_MINERS")
    print(f"[+] Evaluación relacional: {eval_res}")
