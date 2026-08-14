#!/usr/bin/env python3
# HECTRON-01: db_bootstrap.py
# Inicialización de esquemas SQLite para MemoryOS y Event Bus

import sqlite3
import os

# Ajustado para el workspace local
DB_PATH = os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")

SCHEMA_SQL = """
-- Registro inmutable de eventos (Event Bus / Trace)
CREATE TABLE IF NOT EXISTS event_log (
    event_id TEXT PRIMARY KEY,
    timestamp TEXT NOT NULL,
    agent_id TEXT NOT NULL,
    event_type TEXT NOT NULL,
    payload TEXT NOT NULL,
    trace_id TEXT NOT NULL,
    source TEXT NOT NULL
);

-- Memoria Semántica y Episódica Consolidada
CREATE TABLE IF NOT EXISTS memory_store (
    memory_id TEXT PRIMARY KEY,
    agent_id TEXT NOT NULL,
    content TEXT NOT NULL,
    type TEXT NOT NULL, -- episodic, semantic, procedural, social
    confidence REAL NOT NULL,
    salience REAL NOT NULL,
    provenance TEXT NOT NULL,
    timestamp TEXT NOT NULL,
    decay REAL NOT NULL,
    privacy_class TEXT NOT NULL
);

-- Matriz Relacional y de Confianza Social
CREATE TABLE IF NOT EXISTS social_matrix (
    agent_source TEXT NOT NULL,
    agent_target TEXT NOT NULL,
    trust REAL NOT NULL,
    conflict REAL NOT NULL,
    affinity REAL NOT NULL,
    last_updated TEXT NOT NULL,
    PRIMARY KEY (agent_source, agent_target)
);

-- Auditoría de Gobernanza y Permisos
CREATE TABLE IF NOT EXISTS governance_audit (
    audit_id TEXT PRIMARY KEY,
    timestamp TEXT NOT NULL,
    action_requested TEXT NOT NULL,
    authorized INTEGER NOT NULL, -- 0 o 1
    policy_version TEXT NOT NULL,
    reason TEXT NOT NULL
);
"""

def init_database():
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.executescript(SCHEMA_SQL)
    conn.commit()
    conn.close()
    print(f"[+] Base de datos SQLite inicializada en: {DB_PATH}")

if __name__ == "__main__":
    init_database()
