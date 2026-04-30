# Endpoints — Guía de pruebas Thunder Client

Asegúrate de tener Docker corriendo antes de probar:
```bash
docker compose up -d
```

> Todo va por el API Gateway en **http://localhost:8080**
> El gateway redirige internamente a cada microservicio.
> Los puertos 8081, 8082, 8083 siguen funcionando directo si los necesitas.

Orden recomendado para probar:
1. `GET /api/medicos/activos` — verifica ms-usuarios
2. `POST /api/auth/login` — obtén el token
3. `GET /api/especialidades` — verifica ms-configuracion
4. `GET /api/jornadas?medicoId=3` — verifica jornadas
5. `GET /api/citas/disponibilidad?idMedico=3&fecha=2026-04-28` — verifica comunicación ms-agendamiento ↔ ms-configuracion
6. `POST /api/citas/web` — agenda cita y verifica RabbitMQ en logs

---

## ms-usuarios — http://localhost:8080

### Auth

**Login**
```
POST http://localhost:8080/api/auth/login
Body JSON:
{
  "usuario": "admin",
  "contrasena": "admin123"
}
```

**Logout**
```
POST http://localhost:8080/api/auth/logout
Headers: Authorization: Bearer TU_TOKEN_AQUI
```

**Validar token**
```
GET http://localhost:8080/api/auth/validar?token=TU_TOKEN_AQUI
```

---

### Usuarios

**Listar todos**
```
GET http://localhost:8080/api/usuarios
```

**Crear usuario**
```
POST http://localhost:8080/api/usuarios
Body JSON:
{
  "usuario": "nuevo.usuario",
  "contrasena": "clave123"
}
```

**Editar usuario**
```
PUT http://localhost:8080/api/usuarios/1
Body JSON:
{
  "usuario": "admin.editado",
  "contrasena": "nuevaclave123"
}
```

**Eliminar usuario**
```
DELETE http://localhost:8080/api/usuarios/1
```

---

### Personas

**Listar todas**
```
GET http://localhost:8080/api/personas
```

**Buscar por cédula**
```
GET http://localhost:8080/api/personas/documento/10000000001
```

**Editar persona**
```
PUT http://localhost:8080/api/personas/1
Body JSON:
{
  "nombre": "Laura",
  "apellido": "Mendez",
  "cedulaCiudadania": "10000000001",
  "celular": "3001000001",
  "idGenero": 2,
  "correo": "laura.mendez@piedraazul.com",
  "idEstado": 2
}
```

**Inactivar persona**
```
DELETE http://localhost:8080/api/personas/1
```

---

### Médicos

**Listar médicos activos**
```
GET http://localhost:8080/api/medicos/activos
```

**Asignar especialidad**
```
PUT http://localhost:8080/api/medicos/3/especialidad?idEspecialidad=2
```

---

### Pacientes

**Listar todos**
```
GET http://localhost:8080/api/pacientes
```

**Buscar por ID**
```
GET http://localhost:8080/api/pacientes/10
```

**Buscar por cédula**
```
GET http://localhost:8080/api/pacientes/documento/30000000001
```

**Registrar paciente nuevo (crea usuario + paciente + rol en una sola llamada)**
```
POST http://localhost:8080/api/pacientes
Body JSON:
{
  "paciente": {
    "nombre": "Carlos",
    "apellido": "Ruiz",
    "cedulaCiudadania": "99999999999",
    "celular": "3109999999",
    "idGenero": 1,
    "idEstado": 2
  },
  "usuario": {
    "usuario": "carlos.ruiz",
    "contrasena": "clave123"
  }
}
```

---

## ms-agendamiento — http://localhost:8080

### Citas

**Listar todas las citas**
```
GET http://localhost:8080/api/citas/todas
```

**Listar citas de un médico en una fecha**
```
GET http://localhost:8080/api/citas?idMedico=3&fecha=2026-04-21
```

**Listar citas de una fecha (todos los médicos)**
```
GET http://localhost:8080/api/citas?fecha=2026-04-21
```

**Consultar horarios disponibles de un médico**
```
GET http://localhost:8080/api/citas/disponibilidad?idMedico=3&fecha=2026-04-28
```

**Agendar cita desde la web (paciente)**
```
POST http://localhost:8080/api/citas/web
Body JSON:
{
  "idPaciente": "10",
  "idMedico": "3",
  "fecha": "2026-04-28",
  "hora": "07:00"
}
```

**Crear cita manual (agendador)**
```
POST http://localhost:8080/api/citas
Body JSON:
{
  "idPaciente": 10,
  "idMedico": 3,
  "fecha": "2026-04-28",
  "horaInicio": "09:00",
  "horaFin": "09:30",
  "idEstadoCita": 2
}
```

**Historial de citas pasadas de un paciente**
```
GET http://localhost:8080/api/citas/paciente/10/historial
```

**Citas futuras de un paciente**
```
GET http://localhost:8080/api/citas/paciente/10/futuras
```

**Editar cita**
```
PUT http://localhost:8080/api/citas/1
Body JSON:
{
  "idPaciente": 10,
  "idMedico": 3,
  "fecha": "2026-04-29",
  "horaInicio": "08:00",
  "horaFin": "08:30",
  "idEstadoCita": 3
}
```

**Cancelar cita**
```
DELETE http://localhost:8080/api/citas/1
```

---

## ms-configuracion — http://localhost:8080

### Jornadas

**Listar todas las jornadas**
```
GET http://localhost:8080/api/jornadas
```

**Jornadas de un médico**
```
GET http://localhost:8080/api/jornadas?medicoId=3
```

**Jornada de un médico en un día específico**
```
GET http://localhost:8080/api/jornadas?medicoId=3&dia=Lunes
```

**Jornadas de un médico (path variable)**
```
GET http://localhost:8080/api/jornadas/medico/3
```

**Crear jornada**
```
POST http://localhost:8080/api/jornadas
Body JSON:
{
  "idUsuario": 3,
  "diaSemana": "Lunes",
  "horaInicio": "07:00",
  "horaFin": "12:00",
  "idEstado": 1,
  "duracionEstimadaAtencion": 30
}
```

**Editar jornada**
```
PUT http://localhost:8080/api/jornadas/1
Body JSON:
{
  "idUsuario": 3,
  "diaSemana": "Lunes",
  "horaInicio": "08:00",
  "horaFin": "13:00",
  "idEstado": 1,
  "duracionEstimadaAtencion": 30
}
```

**Eliminar jornada**
```
DELETE http://localhost:8080/api/jornadas/1
```

---

### Especialidades

**Listar todas**
```
GET http://localhost:8080/api/especialidades
```

**Buscar por ID**
```
GET http://localhost:8080/api/especialidades/1
```

---

### Configuración

**Listar médicos activos (consulta ms-usuarios internamente)**
```
GET http://localhost:8080/api/configuracion/medicos
```

---

## Usuarios de prueba disponibles

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | Administrador |
| agendador1 | agenda123 | Agendador |
| medico.garcia | medico123 | Medico |
| paciente.perez | pac123 | Paciente |

## IDs útiles para las pruebas

| Qué | IDs disponibles |
|---|---|
| Pacientes (id_persona) | 10 al 29 |
| Médicos (id_persona) | 3 al 9 |
| Citas | 1 al 30 |
| Jornadas | 1 al 28 |
| Especialidades | 1 al 10 |
