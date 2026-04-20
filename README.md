# Proyecto Piedra Azul — Microservicios

Sistema de agendamiento de citas médicas construido con arquitectura de microservicios.
Cada microservicio es una aplicación independiente con su propia base de datos.
Todo corre localmente en tu PC usando Docker — no necesitas internet para que funcione.

---

## ¿Qué es cada cosa?

Antes de arrancar, es útil entender qué hace cada pieza:

| Pieza | Qué es | Para qué sirve |
|---|---|---|
| **Docker** | Programa que crea "contenedores" | Corre todos los servicios en tu PC sin instalar nada extra |
| **docker-compose.yml** | Archivo de configuración | Le dice a Docker qué levantar y cómo conectarlo todo |
| **API Gateway** | Puerta de entrada única | Todo el tráfico entra por aquí antes de ir a los microservicios |
| **ms-usuarios** | Microservicio | Maneja login, personas, médicos, pacientes |
| **ms-agendamiento** | Microservicio | Maneja citas médicas |
| **ms-configuracion** | Microservicio | Maneja jornadas laborales y especialidades |
| **RabbitMQ** | Sistema de mensajería | Permite que los microservicios se comuniquen entre sí de forma asíncrona |
| **PostgreSQL** | Base de datos | Cada microservicio tiene la suya propia |

---

## Requisitos


- [Postman] http://localhost:8082/api/citas/todas
- [Docker Desktop](https://www.docker.com) — instálalo y ábrelo antes de cualquier cosa
- [JDK 21](https://adoptium.net/es/temurin/releases?version=21) — para desarrollo local
- Extensión **Database Client** en VS Code — para ver las BDs desde el editor (opcional pero útil)

extendcion Thunder Client

---

## Estructura del proyecto

```
ProyectoPiedraAzulMicroServicios/
│
├── api_gateway/                        → Puerta de entrada, puerto 8080
│
├── microservicio_usuarios/             → Login, personas, médicos, pacientes — puerto 8081
│   ├── src/main/java/...
│   │   ├── controlador/               → Endpoints HTTP (la "puerta" del microservicio)
│   │   ├── servicio/                  → Lógica de negocio (aquí está el código importante)
│   │   ├── repositorio/               → Consultas a la base de datos
│   │   ├── modelo/                    → Clases que representan las tablas
│   │   └── dto/                       → Objetos simplificados para transferir datos
│   └── BD_MSUsuarios.sql              → Script que crea las tablas y carga datos de prueba
│
├── microservicio_agendamiento/         → Citas médicas — puerto 8082
│   ├── src/main/java/...
│   │   ├── controlador/
│   │   ├── servicio/
│   │   ├── repositorio/
│   │   ├── modelo/
│   │   └── dto/
│   └── BD_MSAgendamiento.sql
│
├── microservicio_configuracion/        → Jornadas y especialidades — puerto 8083
│   ├── src/main/java/...
│   │   ├── controlador/
│   │   ├── servicio/
│   │   ├── repositorio/
│   │   ├── modelo/
│   │   └── dto/
│   └── BD_MSConfiguracion.sql
│
├── frontend/                           → Interfaz de usuario (JavaFX)
└── docker-compose.yml                  → Orquesta y conecta todo
```

---


## Levantar el proyecto

### Primera vez (o después de cambiar código)

```bash
docker compose up --build -d
```

- `--build` recompila el código Java de todos los microservicios antes de levantar
- `-d` corre todo en segundo plano (no bloquea la terminal)

Los scripts SQL se ejecutan **automáticamente** al crear los contenedores.
No necesitas correr nada más — Docker carga las tablas y datos de prueba solo.

### Uso normal (sin cambios de código)

```bash
# Encender todo
docker compose up -d

# Apagar todo
docker compose down
```

### Resetear las bases de datos desde cero

```bash
docker compose down -v
docker compose up --build -d
```

> `-v` elimina los volúmenes (los datos guardados), forzando que los scripts SQL se ejecuten de nuevo desde cero.

---

## Recompilar después de cambiar código

Docker **no detecta cambios automáticamente**. Cada vez que modifiques código Java en VS Code y quieras ver los cambios funcionando, debes recompilar.

### Recompilar todos los microservicios

```bash
docker compose up --build -d
```

### Recompilar solo un microservicio específico (más rápido)

```bash
# Solo ms-agendamiento
docker compose up --build -d microservicio-agendamiento

# Solo ms-usuarios
docker compose up --build -d microservicio-usuarios

# Solo ms-configuracion
docker compose up --build -d microservicio-configuracion
```

> Las bases de datos y RabbitMQ no necesitan rebuild porque no tienen código Java.

---

## Cargar scripts SQL manualmente (PowerShell)

Normalmente no necesitas esto — Docker lo hace solo. Pero si por alguna razón necesitas cargar los scripts a mano:

```powershell
# Asegúrate de estar en la carpeta ProyectoPiedraAzulMicroServicios
docker compose up -d

Get-Content .\microservicio_usuarios\BD_MSUsuarios.sql -Raw | docker exec -i db_usuarios psql -U piedrazul -d db_usuarios
Get-Content .\microservicio_agendamiento\BD_MSAgendamiento.sql -Raw | docker exec -i db_agendamiento psql -U piedrazul -d db_agendamiento
Get-Content .\microservicio_configuracion\BD_MSConfiguracion.sql -Raw | docker exec -i db_configuracion psql -U piedrazul -d db_configuracion
```

---

## Puertos

| Servicio               | Puerto en tu PC |
|------------------------|-----------------|
| API Gateway            | 8080            |
| ms-usuarios            | 8081            |
| ms-agendamiento        | 8082            |
| ms-configuracion       | 8083            |
| db_usuarios (Postgres) | 5433            |
| db_agendamiento        | 5434            |
| db_configuracion       | 5435            |
| RabbitMQ (mensajería)  | 5672            |
| RabbitMQ (panel web)   | 15672           |

---

## Credenciales

| Recurso        | Usuario   | Contraseña   |
|----------------|-----------|--------------|
| Bases de datos | piedrazul | piedrazul123 |
| RabbitMQ       | admin     | admin123     |
| Usuario admin  | admin     | admin123     |

Panel web de RabbitMQ (para ver colas y mensajes): http://localhost:15672

---

## Endpoints disponibles

### ms-usuarios — http://localhost:8081

| Método | URL | Qué hace |
|--------|-----|----------|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/logout` | Cerrar sesión |
| GET | `/api/auth/validar?token=xxx` | Validar token activo |
| GET | `/api/medicos/activos` | Listar médicos activos |
| PUT | `/api/medicos/{id}/especialidad?idEspecialidad=1` | Asignar especialidad |
| GET | `/api/pacientes` | Listar pacientes |
| POST | `/api/pacientes` | Registrar paciente + usuario |
| GET | `/api/pacientes/documento/{cedula}` | Buscar paciente por cédula |
| GET | `/api/personas/documento/{cedula}` | Buscar persona por cédula |
| GET | `/api/usuarios` | Listar usuarios |

### ms-agendamiento — http://localhost:8082

| Método | URL | Qué hace |
|--------|-----|----------|
| GET | `/api/citas?idMedico=3&fecha=2026-04-21` | Listar citas de un médico en una fecha |
| GET | `/api/citas/disponibilidad?idMedico=3&fecha=2026-04-21` | Ver franjas horarias libres |
| POST | `/api/citas/web` | Paciente agenda cita desde la web |
| POST | `/api/citas` | Agendador crea cita manual |
| GET | `/api/citas/paciente/{id}/historial` | Historial de citas pasadas |
| GET | `/api/citas/paciente/{id}/futuras` | Citas próximas del paciente |
| PUT | `/api/citas/{id}` | Editar cita |
| DELETE | `/api/citas/{id}` | Cancelar cita |

### ms-configuracion — http://localhost:8083

| Método | URL | Qué hace |
|--------|-----|----------|
| GET | `/api/jornadas` | Listar todas las jornadas |
| GET | `/api/jornadas?medicoId=3` | Jornadas de un médico |
| GET | `/api/jornadas?medicoId=3&dia=Lunes` | Jornada de un médico en un día |
| POST | `/api/jornadas` | Crear jornada laboral |
| PUT | `/api/jornadas/{id}` | Editar jornada |
| DELETE | `/api/jornadas/{id}` | Eliminar jornada |
| GET | `/api/especialidades` | Listar especialidades |
| GET | `/api/configuracion/medicos` | Listar médicos (consulta ms-usuarios) |

---

## Estado del proyecto


