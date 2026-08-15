# HECTRON MULTIVERSO

Sistema de Universos Virtuales Autonomos para Streaming
Desarrollado por Abadalabs, Inc. (Delaware C-Corp)

## Vision
HECTRON MULTIVERSO es un sistema de streaming autonomo con IA que permite crear universos virtuales persistentes donde los espectadores pueden co-crear contenido en tiempo real.

## Estructura del Proyecto
- 7 Microservicios (API Gateway, Auth, Universe, Autonomy, Metrics, Payments, Gamification)
- Agente Local para control de OBS
- Overlay 3D para streaming
- Kubernetes para despliegue
- Terraform para infraestructura

## Inicio Rapido

### Requisitos
- Node.js 20+
- Docker 24+
- Terraform 1.5+
- gcloud CLI

### Instalacion
```bash
git clone https://github.com/hector1-cloud/Hectron_Multiverso.git
cd Hectron_Multiverso
npm install
npm run docker:up
```

## Arquitectura
- API Gateway (Express + Kong)
- Microservicios en Node.js
- BigQuery para datos
- Redis para cache
- RabbitMQ para eventos
- Kubernetes en GKE

## Comandos
- /explorar [planeta] - Explorar planeta
- /construir [tipo] [planeta] - Construir edificio
- /minar [planeta] - Extraer recursos
- /atacar [objetivo] - Atacar
- /comerciar [item] [cantidad] [precio] - Comerciar
- /votar [opcion] - Votar
- /inventario - Mostrar inventario
- /estados - Mostrar estadisticas
- /ayuda - Ayuda

## Licencia
MIT (c) Abadalabs, Inc.