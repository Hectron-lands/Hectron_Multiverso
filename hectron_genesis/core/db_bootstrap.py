#!/usr/bin/env python3
# HECTRON-01: db_bootstrap.py
# Inicialización de esquemas SQLite optimizados para MemoryOS y Event Bus (WAL + Índices)

import sqlite3
import os

DB_PATH = os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")

SCHEMA_SQL = """
-- Configuración de Alto Rendimiento para SQLite en Edge/ARM
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;
PRAGMA temp_store = MEMORY;
PRAGMA cache_size = -64000;
PRAGMA mmap_size = 268435456;

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

-- Índices de Alto Rendimiento para Consultas Rápidas (Sub-milimétricas)
CREATE INDEX IF NOT EXISTS idx_event_timestamp ON event_log(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_event_agent ON event_log(agent_id, event_type);
CREATE INDEX IF NOT EXISTS idx_memory_agent_type ON memory_store(agent_id, type, confidence, salience);
CREATE INDEX IF NOT EXISTS idx_memory_salience ON memory_store(salience DESC, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_social_source ON social_matrix(agent_source, trust);
"""

def get_optimized_connection(db_path=DB_PATH):
    """Crea una conexión SQLite con parámetros de rendimiento ultra-rápidos."""
    os.makedirs(os.path.dirname(db_path), exist_ok=True)
    conn = sqlite3.connect(db_path, timeout=10.0, check_same_thread=False)
    cursor = conn.cursor()
    cursor.execute("PRAGMA journal_mode = WAL;")
    cursor.execute("PRAGMA synchronous = NORMAL;")
    cursor.execute("PRAGMA temp_store = MEMORY;")
    cursor.execute("PRAGMA cache_size = -64000;")
    cursor.execute("PRAGMA mmap_size = 268435456;")
    cursor.close()
    return conn

def init_database():
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)
    conn = get_optimized_connection(DB_PATH)
    cursor = conn.cursor()
    cursor.executescript(SCHEMA_SQL)
    conn.commit()
    conn.close()
    print(f"[+] Base de datos SQLite optimizada (WAL + Índices) inicializada en: {DB_PATH}")

if __name__ == "__main__":
    init_database()
