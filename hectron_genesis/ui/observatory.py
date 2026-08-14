#!/usr/bin/env python3
# HECTRON-01: observatory.py
# Interfaz de monitoreo para el Event Bus (Terminal-based por ahora)

import sqlite3
import os
import time

DB_PATH = os.path.join(os.getcwd(), "hectron_genesis/data/hectron_core.db")

def clear_screen():
    os.system('clear' if os.name == 'posix' else 'cls')

def monitor_events():
    print("--- HECTRON OBSERVATORY v1.0 ---")
    print(f"Monitoreando: {DB_PATH}")
    print("Presiona Ctrl+C para salir.\n")

    while True:
        try:
            conn = sqlite3.connect(DB_PATH)
            cursor = conn.cursor()
            cursor.execute("SELECT timestamp, event_type, payload FROM event_log ORDER BY timestamp DESC LIMIT 5")
            rows = cursor.fetchall()
            conn.close()

            clear_screen()
            print("--- HECTRON OBSERVATORY: LIVE EVENT BUS ---")
            print(f"{'TIMESTAMP':<25} | {'TYPE':<15} | {'PAYLOAD'}")
            print("-" * 80)
            
            for row in rows:
                print(f"{row[0]:<25} | {row[1]:<15} | {row[2][:40]}...")
            
            time.sleep(2)
        except KeyboardInterrupt:
            print("\nObservatorio cerrado.")
            break
        except Exception as e:
            print(f"Error: {e}")
            time.sleep(5)

if __name__ == "__main__":
    monitor_events()
