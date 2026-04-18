# Proyecto Piedra Azul — Microservicios

Sistema de citas médicas migrado de monolito a arquitectura de microservicios.

---

## Requisitos

- [Docker Desktop](https://www.docker.com) — para levantar todos los contenedores
- [JDK 21](https://adoptium.net/es/temurin/releases?version=21) — para desarrollo local
- Extensión **Database Client** en VS Code — para ver las BDs desde el editor

---

## Estructura

```
ProyectoPiedraAzulMicroServicios/
├── api_gateway/              → Puerta de entrada, puerto 8080
├── microservicio_usuarios/   → Personas, auth, roles, puerto 8081
├── microservicio_agendamiento/  → Citas, puerto 8082
├── microservicio_configuracion/ → Jornadas, especialidades, puerto 8083
├── frontend/                 → Interfaz de usuario
└── docker-compose.yml        → Orquesta todo
```

---

## Levantar el proyecto

### Primera vez

```bash
# 1. Construir y levantar todos los contenedores
docker compose up --build -d

# 2. Cargar los datos de prueba en cada BD
docker exec -i db_usuarios      psql -U piedrazul -d db_usuarios      < microservicio_usuarios/init-db.sql
docker exec -i db_agendamiento  psql -U piedrazul -d db_agendamiento  < microservicio_agendamiento/init-db.sql
docker exec -i db_configuracion psql -U piedrazul -d db_configuracion < microservicio_configuracion/init-db.sql
```

### Uso normal

```bash
# Encender
docker compose up -d

# Apagar
docker compose down
```

---

## Puertos

| Servicio              | Puerto local |
|-----------------------|-------------|
| API Gateway           | 8080        |
| ms-usuarios           | 8081        |
| ms-agendamiento       | 8082        |
| ms-configuracion      | 8083        |
| db_usuarios (Postgres)| 5433        |
| db_agendamiento       | 5434        |
| db_configuracion      | 5435        |
| RabbitMQ (AMQP)       | 5672        |
| RabbitMQ (Panel web)  | 15672       |

---

## Credenciales

| Recurso       | Usuario    | Contraseña    |
|---------------|------------|---------------|
| Bases de datos| piedrazul  | piedrazul123  |
| RabbitMQ      | admin      | admin123      |
| Usuario admin | admin      | admin123      |

Panel RabbitMQ: http://localhost:15672

---

## Scripts SQL

Cada microservicio tiene su propio `init-db.sql` con tablas y datos de prueba:

- `microservicio_usuarios/init-db.sql` — usuarios, personas, médicos, pacientes
- `microservicio_agendamiento/init-db.sql` — citas
- `microservicio_configuracion/init-db.sql` — jornadas laborales, especialidades
