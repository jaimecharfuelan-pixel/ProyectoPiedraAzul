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
├── api_gateway/                        → Puerta de entrada, puerto 8080
├── microservicio_usuarios/             → Personas, auth, roles, puerto 8081
│   └── BD_MSUsuarios.sql              → Script tablas + datos de prueba
├── microservicio_agendamiento/         → Citas, puerto 8082
│   └── BD_MSAgendamiento.sql          → Script tablas + datos de prueba
├── microservicio_configuracion/        → Jornadas, especialidades, puerto 8083
│   └── BD_MSConfiguracion.sql         → Script tablas + datos de prueba
├── frontend/                           → Interfaz de usuario
└── docker-compose.yml                  → Orquesta todo
```

---

## Levantar el proyecto

### Primera vez

```bash
docker compose up --build -d
```

Los scripts SQL se ejecutan **automáticamente** al crear los contenedores.
No necesitas correr nada más — Docker carga las tablas y datos de prueba solo.

### Uso normal

```bash
# Encender
docker compose up -d

# Apagar
docker compose down
```

### Si necesitas resetear las bases de datos desde cero

```bash
docker compose down -v
docker compose up --build -d
```

> `-v` elimina los volúmenes, forzando que los scripts SQL se ejecuten de nuevo.

---

## Cargar scripts SQL manualmente (PowerShell)

Si por alguna razón necesitas cargar los scripts a mano:

```powershell
# Asegúrate de estar en la carpeta ProyectoPiedraAzulMicroServicios
docker compose up -d

Get-Content .\microservicio_usuarios\BD_MSUsuarios.sql -Raw | docker exec -i db_usuarios psql -U piedrazul -d db_usuarios
Get-Content .\microservicio_agendamiento\BD_MSAgendamiento.sql -Raw | docker exec -i db_agendamiento psql -U piedrazul -d db_agendamiento
Get-Content .\microservicio_configuracion\BD_MSConfiguracion.sql -Raw | docker exec -i db_configuracion psql -U piedrazul -d db_configuracion
```

---

## Scripts SQL

- `microservicio_usuarios/BD_MSUsuarios.sql` — usuarios, personas, médicos, pacientes
- `microservicio_agendamiento/BD_MSAgendamiento.sql` — citas
- `microservicio_configuracion/BD_MSConfiguracion.sql` — jornadas laborales, especialidades

---

## Puertos

| Servicio               | Puerto local |
|------------------------|-------------|
| API Gateway            | 8080        |
| ms-usuarios            | 8081        |
| ms-agendamiento        | 8082        |
| ms-configuracion       | 8083        |
| db_usuarios (Postgres) | 5433        |
| db_agendamiento        | 5434        |
| db_configuracion       | 5435        |
| RabbitMQ (AMQP)        | 5672        |
| RabbitMQ (Panel web)   | 15672       |

---

## Credenciales

| Recurso        | Usuario   | Contraseña   |
|----------------|-----------|--------------|
| Bases de datos | piedrazul | piedrazul123 |
| RabbitMQ       | admin     | admin123     |
| Usuario admin  | admin     | admin123     |

Panel RabbitMQ: http://localhost:15672

---

## Comandos útiles

```bash
# Reconstruir un microservicio específico
docker compose build microservicio-configuracion

# Reiniciar un microservicio específico
docker compose up -d microservicio-configuracion
```
