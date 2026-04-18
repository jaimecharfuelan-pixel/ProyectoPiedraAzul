
# Guía de Migración: Monolito → Microservicios
## Sistema Piedrazul — Ingeniería de Software II (Segundo Corte)

---

# PARTE 1 — Diagramas C4

El modelo C4 describe la arquitectura en 4 niveles de zoom: Contexto → Contenedores → Componentes → Código.
Primero se muestra el sistema **tal como está hoy** (monolito), luego **como quedará** con microservicios.

---

## C4 Nivel 1 — Diagrama de Contexto (Sistema Actual: Monolito)

Muestra quién usa el sistema y con qué sistemas externos interactúa.

```
┌─────────────────────────────────────────────────────────────────────┐
│                        SISTEMA PIEDRAZUL                            │
│                    [Sistema de Citas Médicas]                       │
└─────────────────────────────────────────────────────────────────────┘

Usuarios que interactúan:

  [Paciente]          Agenda citas, ve disponibilidad, registra su cuenta
       │
       ▼
  ┌──────────────────────────────────────────────────────────────────┐
  │                                                                  │
  │              PIEDRAZUL (Aplicación JavaFX)                       │
  │         Gestión de citas médicas — Monolito Java 17              │
  │                                                                  │
  └──────────────────────────────────────────────────────────────────┘
       ▲                    │                    ▲
       │                    ▼                    │
  [Agendador]        [PostgreSQL DB]        [Administrador]
  Registra           Base de datos          Configura agendas,
  pacientes,         única compartida       gestiona usuarios
  crea citas         por todo el sistema    y médicos
  manualmente

  [Médico/Terapista]
  Consulta sus citas del día
```

**Problema visible:** Un solo proceso JVM, una sola base de datos. Todos los actores tocan el mismo sistema.

---

## C4 Nivel 2 — Diagrama de Contenedores (Sistema Actual: Monolito)

Muestra los "contenedores" (procesos/bases de datos) que componen el sistema hoy.

```
┌──────────────────────────────────────────────────────────────────────────┐
│  SISTEMA PIEDRAZUL — MONOLITO                                            │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │              Aplicación JavaFX (JVM única)                         │  │
│  │                                                                    │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐ │  │
│  │  │ presentacion │  │    logica    │  │      persistencia        │ │  │
│  │  │              │  │              │  │                          │ │  │
│  │  │ Controladores│→ │  Servicios   │→ │  Repositorios (JDBC)     │ │  │
│  │  │ Vistas FXML  │  │  Modelos     │  │  ConexionBD (Singleton)  │ │  │
│  │  │              │  │  Interfaces  │  │                          │ │  │
│  │  └──────────────┘  └──────────────┘  └────────────┬─────────────┘ │  │
│  │                                                    │               │  │
│  └────────────────────────────────────────────────────│───────────────┘  │
│                                                       │                  │
│  ┌────────────────────────────────────────────────────▼───────────────┐  │
│  │              PostgreSQL — BaseDeDatosPiedraAzul                    │  │
│  │                                                                    │  │
│  │  tablas: persona, usuario, cita, jornada_laboral,                  │  │
│  │          medico_terapista, rol, sesion_token, dominio_especialidad │  │
│  └────────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

**Problema visible:** Todas las tablas en una sola BD. `ServicioAgendamiento` accede directamente a `JornadaLaboral` y a `Cita` en el mismo proceso. No hay separación de responsabilidades a nivel de despliegue.

---

## C4 Nivel 3 — Diagrama de Componentes (Sistema Actual: Monolito)

Zoom dentro de la capa lógica para ver qué servicios existen y cómo se relacionan.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Capa Lógica — proyecto-piedra-azul                                         │
│                                                                             │
│  ┌─────────────────────┐    ┌──────────────────────┐                        │
│  │   ServicioAuth      │───▶│   ServicioUsuarios   │                        │
│  │  login() + JWT      │    │  autenticar()        │                        │
│  │  SesionToken        │    │  registrarUsuario()  │                        │
│  └─────────────────────┘    │  listarUsuarios()    │                        │
│                             └──────────┬───────────┘                        │
│                                        │ usa                                │
│                             ┌──────────▼───────────┐                        │
│                             │   ServicioPersona    │                        │
│                             │  crearPersona()      │                        │
│                             │  listarMedicos()     │◀──────────────────┐    │
│                             │  asignarEspecialidad │                   │    │
│                             └──────────────────────┘                   │    │
│                                                                         │    │
│  ┌──────────────────────────────────────────────────┐                   │    │
│  │   ServicioAgendamiento                           │                   │    │
│  │  consultarDisponibilidad(idMedico, fecha)        │───────────────────┘    │
│  │  agendarCitaWeb(idPaciente, idMedico, fecha, hora│  necesita médicos      │
│  │  crearCitaManual(cita)                           │                        │
│  │  listarCitas(idMedico, fecha)                    │                        │
│  │  cancelarCita(idCita)                            │                        │
│  └──────────────────┬───────────────────────────────┘                        │
│                     │ usa                                                    │
│  ┌──────────────────▼───────────────────────────────┐                        │
│  │   ServicioConfiguracion                          │                        │
│  │  configurarDisponibilidadMedico(jornada)         │                        │
│  │  obtenerTodasLasJornadas()                       │                        │
│  │  editarTurno() / eliminarTurno()                 │                        │
│  └──────────────────────────────────────────────────┘                        │
│                                                                             │
│  ┌──────────────────────────────────────────────────┐                        │
│  │   ServicioPaciente                               │                        │
│  │  registrarPaciente(paciente, usuario)            │                        │
│  │  obtenerHistorialCitas(idPaciente)               │                        │
│  │  obtenerCitasFuturas(idPaciente)                 │                        │
│  └──────────────────────────────────────────────────┘                        │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Problema visible:** `ServicioAgendamiento` depende de `IRepositorioJornadaLaboral` Y de `IRepositorioMedicoTerapista` al mismo tiempo. Todo está acoplado en memoria.

---
---

# PARTE 2 — C4 de la Arquitectura Objetivo (Microservicios)

---

## C4 Nivel 1 — Diagrama de Contexto (Arquitectura Objetivo)

```
  [Paciente]          [Agendador]          [Administrador]       [Médico]
      │                    │                     │                   │
      └────────────────────┴─────────────────────┴───────────────────┘
                                      │
                                      ▼  HTTPS
                         ┌────────────────────────┐
                         │      API GATEWAY        │
                         │  (Spring Cloud Gateway) │
                         │  Punto de entrada único │
                         │  Valida JWT             │
                         └────────────────────────┘
                                      │
              ┌───────────────────────┼───────────────────────┐
              ▼                       ▼                       ▼
   ┌─────────────────┐    ┌─────────────────────┐   ┌──────────────────────┐
   │   ms-usuarios   │    │   ms-agendamiento   │   │   ms-configuracion   │
   │  Identidad y    │    │  Citas y            │   │  Jornadas y          │
   │  Autenticación  │    │  Disponibilidad     │   │  Disponibilidad      │
   └─────────────────┘    └─────────────────────┘   └──────────────────────┘
              │                       │                       │
              └───────────────────────┴───────────────────────┘
                                      │
                         ┌────────────▼────────────┐
                         │        RabbitMQ          │
                         │  Broker de mensajería    │
                         │  Comunicación asíncrona  │
                         └─────────────────────────┘
```

---

## C4 Nivel 2 — Diagrama de Contenedores (Arquitectura Objetivo)

Cada caja es un proceso/contenedor Docker independiente.

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│  SISTEMA PIEDRAZUL — MICROSERVICIOS                                                  │
│                                                                                      │
│  ┌──────────────────┐                                                                │
│  │   api-gateway    │  :8080                                                         │
│  │  Spring Cloud GW │◀── Todo el tráfico externo entra aquí                         │
│  │  Filtro JWT      │                                                                │
│  └──┬───────┬───┬───┘                                                                │
│     │       │   │  enruta por path                                                  │
│     │       │   │                                                                    │
│  /api/usuarios  /api/citas    /api/configuracion                                     │
│     │       │   │                                                                    │
│     ▼       │   ▼                                                                    │
│  ┌──────────────────┐    ┌──────────────────────┐    ┌──────────────────────────┐   │
│  │  ms-usuarios     │    │   ms-agendamiento    │    │    ms-configuracion      │   │
│  │  :8081           │    │   :8082              │    │    :8083                 │   │
│  │                  │    │                      │    │                          │   │
│  │  Spring Boot     │    │  Spring Boot         │    │  Spring Boot             │   │
│  │  REST API        │    │  REST API            │    │  REST API                │   │
│  │  + Swagger       │    │  + Swagger           │    │  + Swagger               │   │
│  └────────┬─────────┘    └──────────┬───────────┘    └────────────┬─────────────┘   │
│           │                         │                             │                 │
│           ▼                         ▼                             ▼                 │
│  ┌────────────────┐    ┌────────────────────────┐    ┌────────────────────────────┐ │
│  │  db_usuarios   │    │    db_agendamiento     │    │    db_configuracion        │ │
│  │  PostgreSQL    │    │    PostgreSQL           │    │    PostgreSQL              │ │
│  │  :5433         │    │    :5434               │    │    :5435                   │ │
│  └────────────────┘    └────────────────────────┘    └────────────────────────────┘ │
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────┐   │
│  │                          RabbitMQ  :5672 / UI :15672                         │   │
│  │   Exchange: citas.exchange (topic)    Exchange: usuarios.exchange (direct)   │   │
│  └──────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## C4 Nivel 3 — Diagrama de Componentes por Microservicio (Arquitectura Objetivo)

### ms-usuarios (puerto 8081)

```
┌──────────────────────────────────────────────────────────────────────┐
│  ms-usuarios                                                         │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Capa REST (Controllers)                                        │ │
│  │  UsuarioController   PersonaController   AuthController         │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Capa de Servicios                                              │ │
│  │  ServicioAuth        ServicioUsuarios      ServicioPersona      │ │
│  │  (login + JWT)       (CRUD usuarios)       (CRUD personas,      │ │
│  │                                             médicos, pacientes) │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Capa de Repositorios (JPA/JDBC)                                │ │
│  │  RepositorioUsuario   RepositorioPersona   RepositorioPaciente  │ │
│  │  RepositorioMedico    RepositorioAgendador RepositorioRol       │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Mensajería (RabbitMQ Publisher)                                │ │
│  │  Publica: usuarios.exchange → "paciente.registrado"             │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                      │
│  Base de datos: db_usuarios                                          │
│  Tablas: persona, usuario, rol, sesion_token, dominio_especialidad   │
└──────────────────────────────────────────────────────────────────────┘
```

---

### ms-agendamiento (puerto 8082)

```
┌──────────────────────────────────────────────────────────────────────┐
│  ms-agendamiento                                                     │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Capa REST (Controllers)                                        │ │
│  │  CitaController                                                 │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Capa de Servicios                                              │ │
│  │  ServicioAgendamiento                                           │ │
│  │  consultarDisponibilidad() → llama a ms-configuracion via HTTP  │ │
│  │  agendarCitaWeb()          → guarda Cita + publica evento       │ │
│  │  crearCitaManual()                                              │ │
│  │  listarCitas()                                                  │ │
│  │  cancelarCita()                                                 │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Capa de Repositorios                                           │ │
│  │  RepositorioCitas                                               │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Clientes externos                                              │ │
│  │  JornadaHttpAdapter → GET http://ms-configuracion/api/jornadas  │ │
│  │  (implementa IRepositorioJornadaLaboral via RestTemplate)       │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Mensajería (RabbitMQ)                                          │ │
│  │  Publisher: citas.exchange → "cita.creada" / "cita.cancelada"   │ │
│  │  Consumer:  cola.paciente.precarga ← "paciente.registrado"      │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                      │
│  Base de datos: db_agendamiento                                      │
│  Tablas: cita, estado_cita                                           │
│  NOTA: Solo guarda idPaciente e idMedico (no duplica datos de persona│
└──────────────────────────────────────────────────────────────────────┘
```

---

### ms-configuracion (puerto 8083)

```
┌──────────────────────────────────────────────────────────────────────┐
│  ms-configuracion                                                    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Capa REST (Controllers)                                        │ │
│  │  JornadaController    EspecialidadController                    │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Capa de Servicios                                              │ │
│  │  ServicioConfiguracion                                          │ │
│  │  configurarDisponibilidadMedico(jornada)                        │ │
│  │  obtenerTodasLasJornadas()                                      │ │
│  │  editarTurno() / eliminarTurno()                                │ │
│  └──────────────────────────────┬──────────────────────────────────┘ │
│                                 │                                    │
│  ┌──────────────────────────────▼──────────────────────────────────┐ │
│  │  Capa de Repositorios                                           │ │
│  │  RepositorioJornadaLaboral    RepositorioDominioEspecialidad     │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                      │
│  Base de datos: db_configuracion                                     │
│  Tablas: jornada_laboral, dominio_especialidad                       │
└──────────────────────────────────────────────────────────────────────┘
```

---

## C4 Nivel 3 — Flujo de Mensajería RabbitMQ

```
ms-agendamiento                  RabbitMQ                    ms-usuarios
      │                              │                             │
      │  POST /api/citas             │                             │
      │  (nueva cita creada)         │                             │
      │                              │                             │
      │──publish("cita.creada")─────▶│                             │
      │  Exchange: citas.exchange    │                             │
      │  RoutingKey: cita.creada     │──deliver──────────────────▶│
      │                              │  Queue: cola.notif.paciente │
      │                              │                             │ onEvento()
      │                              │                             │ notifica al paciente
      │                              │                             │
      │──publish("cita.cancelada")──▶│                             │
      │  RoutingKey: cita.cancelada  │──deliver──────────────────▶│
      │                              │  Queue: cola.auditoria      │ registra log

ms-usuarios                      RabbitMQ                ms-agendamiento
      │                              │                             │
      │  POST /api/usuarios/registrar│                             │
      │  (nuevo paciente)            │                             │
      │                              │                             │
      │──publish("paciente.registrado")▶│                          │
      │  Exchange: usuarios.exchange │──deliver──────────────────▶│
      │                              │  Queue: cola.paciente.precarga
      │                              │                             │ pre-carga idPaciente
      │                              │                             │ en caché local
```

---

---

# PARTE 3 — Análisis de Clases: ¿Qué se mueve, qué se duplica y qué se simplifica?

Esta es la parte más importante antes de escribir código. Cada clase del monolito tiene un destino claro.

---

## Tabla de Destino por Clase

| Clase del Monolito       | Destino Principal  | ¿Se duplica? | Forma en otros servicios          |
|--------------------------|--------------------|--------------|-----------------------------------|
| `Persona` (abstracta)    | ms-usuarios        | Parcial      | Ver tabla de DTOs abajo           |
| `Paciente`               | ms-usuarios        | Parcial      | `PacienteResumenDTO` en ms-agendamiento |
| `MedicoTerapista`        | ms-usuarios        | Parcial      | `MedicoResumenDTO` en ms-agendamiento y ms-configuracion |
| `Agendador`              | ms-usuarios        | No           | Solo existe en ms-usuarios        |
| `Usuario`                | ms-usuarios        | No           | Solo existe en ms-usuarios        |
| `Rol`                    | ms-usuarios        | No           | Solo existe en ms-usuarios        |
| `SesionToken`            | ms-usuarios        | No           | Solo existe en ms-usuarios        |
| `Cita`                   | ms-agendamiento    | No           | Solo existe en ms-agendamiento    |
| `JornadaLaboral`         | ms-configuracion   | Parcial      | `JornadaResumenDTO` en ms-agendamiento |
| `DominioEspecialidad`    | ms-configuracion   | No           | Solo existe en ms-configuracion   |

---

## ¿Por qué se duplican algunas clases?

La regla de oro en microservicios es: **cada servicio es dueño de sus datos**. Pero `ms-agendamiento` necesita saber el nombre del médico para mostrar una cita, y necesita las jornadas para calcular disponibilidad.

La solución no es compartir la clase completa, sino crear **DTOs ligeros** (Data Transfer Objects) que solo tienen los campos que ese servicio necesita.

---

## DTOs Ligeros por Microservicio

### En ms-agendamiento

```java
// MedicoResumenDTO.java
// Solo lo que necesita agendamiento para operar
public class MedicoResumenDTO {
    private int idMedico;       // para filtrar citas
    private String nombre;      // para mostrar en UI
    private String apellido;    // para mostrar en UI
    private String especialidad;// para mostrar en UI
    // NO tiene: cedula, celular, correo, fechaNacimiento, idGenero, idEstado
    // NO tiene: contrasena, idUsuario, idRol
}

// PacienteResumenDTO.java
public class PacienteResumenDTO {
    private int idPaciente;     // para asociar la cita
    private String nombre;      // para mostrar en UI
    private String apellido;    // para mostrar en UI
    // NO tiene nada más — agendamiento no necesita más datos del paciente
}

// JornadaResumenDTO.java
// Recibido desde ms-configuracion via HTTP
public class JornadaResumenDTO {
    private int idMedico;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    // NO tiene: idJornada, idEstado (no relevante para calcular disponibilidad)
}
```

### En ms-configuracion

```java
// MedicoResumenDTO.java
// Solo necesita saber qué médicos existen para asignarles jornadas
public class MedicoResumenDTO {
    private int idMedico;
    private String nombre;
    private String apellido;
    // Obtenido via HTTP desde ms-usuarios cuando el admin configura una jornada
}
```

---

## ¿Cómo obtiene ms-agendamiento los datos del médico?

Hay dos estrategias. Se recomienda la combinación de ambas:

**Estrategia A — Consulta HTTP síncrona (para datos en tiempo real)**
```
ms-agendamiento → GET http://ms-usuarios/api/personas/medicos
                ← [{ idMedico: 3, nombre: "Carlos", apellido: "Ruiz", especialidad: "Fisioterapia" }]
```
Se usa cuando el agendador abre la pantalla y necesita el listado actualizado de médicos.

**Estrategia B — Evento asíncrono (para pre-carga en caché)**
```
ms-usuarios publica "medico.registrado" → RabbitMQ
ms-agendamiento consume el evento y guarda MedicoResumenDTO en su propia tabla local
```
Se usa para que ms-agendamiento no dependa de ms-usuarios en cada operación.

Para el entregable del segundo corte, **la Estrategia A es suficiente y más simple de implementar**.

---

## Clases que NO se duplican bajo ninguna circunstancia

| Clase          | Razón                                                                 |
|----------------|-----------------------------------------------------------------------|
| `Usuario`      | La autenticación y contraseñas solo viven en ms-usuarios             |
| `SesionToken`  | El JWT lo valida el API Gateway, no los microservicios internos      |
| `Rol`          | La autorización por rol se resuelve en el Gateway con el JWT         |
| `Agendador`    | Es un tipo de persona, solo ms-usuarios lo gestiona                  |

---

# PARTE 4 — Plan de Descomposición Detallado

## Bounded Context 1: ms-usuarios

**Responsabilidad única:** Saber quién es cada persona en el sistema y si puede entrar.

Clases que migran completas:
- `Persona` (abstracta) → se convierte en entidad JPA base
- `Paciente`, `MedicoTerapista`, `Agendador` → entidades JPA con herencia
- `Usuario`, `Rol`, `SesionToken` → entidades JPA propias

Servicios que migran:
- `ServicioAuth` → `AuthService` (Spring Boot)
- `ServicioUsuarios` → `UsuarioService`
- `ServicioPersona` → `PersonaService`
- `ServicioPaciente` (solo la parte de registro) → `PacienteService`

Repositorios que migran:
- Todos los `IRepositorio*` de persona, usuario, rol, token → interfaces JPA Repository

API que expone:
```
POST   /api/auth/login                    → devuelve JWT
POST   /api/personas/pacientes            → registrar paciente
POST   /api/personas/medicos              → registrar médico (admin)
GET    /api/personas/medicos              → listar médicos activos
GET    /api/personas/{id}                 → buscar por id
GET    /api/personas/documento/{cedula}   → buscar por cédula
PUT    /api/personas/{id}                 → editar persona
DELETE /api/personas/{id}                 → inactivar persona
```

---

## Bounded Context 2: ms-agendamiento

**Responsabilidad única:** Saber qué citas existen y cuándo hay espacio para una nueva.

Clases que migran completas:
- `Cita` → entidad JPA propia

Clases que se crean nuevas (DTOs ligeros):
- `MedicoResumenDTO` (recibido de ms-usuarios)
- `PacienteResumenDTO` (recibido de ms-usuarios)
- `JornadaResumenDTO` (recibido de ms-configuracion)

Servicios que migran:
- `ServicioAgendamiento` → `AgendamientoService` (Spring Boot)
- `ServicioPaciente` (solo historial/citas futuras) → métodos en `CitaService`

Repositorios que migran:
- `IRepositorioCitas` → `CitaRepository` (JPA)

Clientes HTTP que se crean:
- `UsuarioClient` → llama a ms-usuarios para obtener médicos/pacientes
- `ConfiguracionClient` → llama a ms-configuracion para obtener jornadas

API que expone:
```
GET    /api/citas?medicoId={id}&fecha={fecha}          → listar citas
GET    /api/citas/paciente/{id}                        → historial paciente
GET    /api/citas/disponibilidad?medicoId={id}&fecha={fecha}
POST   /api/citas                                      → crear cita (publica evento)
PUT    /api/citas/{id}                                 → editar cita
DELETE /api/citas/{id}                                 → cancelar cita (publica evento)
```

---

## Bounded Context 3: ms-configuracion

**Responsabilidad única:** Saber cuándo trabaja cada médico.

Clases que migran completas:
- `JornadaLaboral` → entidad JPA propia
- `DominioEspecialidad` → entidad JPA propia

Clases que se crean nuevas (DTOs ligeros):
- `MedicoResumenDTO` (recibido de ms-usuarios para mostrar nombre en UI)

Servicios que migran:
- `ServicioConfiguracion` → `ConfiguracionService` (Spring Boot)

Repositorios que migran:
- `IRepositorioJornadaLaboral` → `JornadaLaboralRepository` (JPA)
- `IRepositorioDominioEspecialidad` → `EspecialidadRepository` (JPA)

API que expone:
```
GET    /api/configuracion/jornadas?medicoId={id}       → jornadas de un médico
POST   /api/configuracion/jornadas                     → crear jornada
PUT    /api/configuracion/jornadas/{id}                → editar jornada
DELETE /api/configuracion/jornadas/{id}                → eliminar jornada
GET    /api/configuracion/especialidades               → listar especialidades
```

---

# PARTE 5 — Configuración RabbitMQ

## Topología completa

```
Exchange: citas.exchange (tipo: topic)
├── RoutingKey: cita.creada      → Queue: cola.notificacion.paciente  → ms-usuarios
├── RoutingKey: cita.cancelada   → Queue: cola.auditoria.citas        → ms-usuarios (log)
└── RoutingKey: cita.#           → Queue: cola.auditoria.general      → futuro servicio de logs

Exchange: usuarios.exchange (tipo: direct)
└── RoutingKey: paciente.registrado → Queue: cola.paciente.precarga   → ms-agendamiento
```

## Mensaje evento `cita.creada`

```json
{
  "eventoId": "a1b2c3d4-uuid",
  "tipo": "CITA_CREADA",
  "timestamp": "2025-04-17T10:30:00",
  "payload": {
    "idCita": 42,
    "idPaciente": 7,
    "idMedico": 3,
    "fecha": "2025-04-20",
    "horaInicio": "09:00",
    "horaFin": "09:30",
    "estado": "PENDIENTE"
  }
}
```

## Configuración Spring Boot (RabbitMQConfig.java)

```java
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange citasExchange() {
        return new TopicExchange("citas.exchange");
    }

    @Bean
    public Queue colaNotificacion() {
        return new Queue("cola.notificacion.paciente", true);
    }

    @Bean
    public Binding bindingNotificacion(Queue colaNotificacion, TopicExchange citasExchange) {
        return BindingBuilder.bind(colaNotificacion)
                             .to(citasExchange)
                             .with("cita.creada");
    }
}
```

## Publicador (ms-agendamiento)

```java
@Component
public class CitaEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publicarCitaCreada(Cita cita) {
        CitaCreadaEvent evento = new CitaCreadaEvent(cita);
        rabbitTemplate.convertAndSend("citas.exchange", "cita.creada", evento);
    }

    public void publicarCitaCancelada(int idCita) {
        rabbitTemplate.convertAndSend("citas.exchange", "cita.cancelada",
            Map.of("idCita", idCita, "timestamp", LocalDateTime.now()));
    }
}
```

## Consumidor (ms-usuarios)

```java
@Component
public class NotificacionCitaListener {

    @RabbitListener(queues = "cola.notificacion.paciente")
    public void recibirCitaCreada(CitaCreadaEvent evento) {
        System.out.println("Notificando paciente " + evento.getIdPaciente()
            + " sobre cita el " + evento.getFecha());
        // Aquí iría lógica de email/SMS
    }
}
```

Dependencia Maven para todos los microservicios que usen RabbitMQ:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

---

# PARTE 6 — Patrones GoF (6 implementaciones)

## Creacionales

### 1. Builder — Construcción de Cita (ms-agendamiento)

```java
public class Cita {
    private int idPaciente, idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio, horaFin;
    private int idEstado;

    private Cita() {}

    public static class Builder {
        private Cita cita = new Cita();
        public Builder paciente(int id)        { cita.idPaciente = id;   return this; }
        public Builder medico(int id)          { cita.idMedico = id;     return this; }
        public Builder fecha(LocalDate f)      { cita.fecha = f;         return this; }
        public Builder horaInicio(LocalTime h) { cita.horaInicio = h;    return this; }
        public Builder horaFin(LocalTime h)    { cita.horaFin = h;       return this; }
        public Builder estado(int e)           { cita.idEstado = e;      return this; }
        public Cita build() {
            Objects.requireNonNull(cita.fecha, "Fecha obligatoria");
            return cita;
        }
    }
}
// Uso:
Cita c = new Cita.Builder().paciente(7).medico(3).fecha(fecha).horaInicio(hora).estado(2).build();
```

### 2. Factory Method — Creación de Persona por Rol (ms-usuarios)

```java
public abstract class PersonaFactory {
    public abstract Persona crearPersona(PersonaDTO dto);

    public static PersonaFactory obtenerFactory(String rol) {
        return switch (rol.toUpperCase()) {
            case "PACIENTE"  -> new PacienteFactory();
            case "MEDICO"    -> new MedicoFactory();
            case "AGENDADOR" -> new AgendadorFactory();
            default -> throw new IllegalArgumentException("Rol desconocido: " + rol);
        };
    }
}
```

## Estructurales

### 3. Adapter — Cliente HTTP como Repositorio (ms-agendamiento)

```java
// Adapta la llamada HTTP a ms-configuracion para que parezca un repositorio local
@Component
public class JornadaHttpAdapter implements IRepositorioJornadaLaboral {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms.configuracion.url}")
    private String urlConfiguracion;

    @Override
    public List<JornadaLaboral> listar() {
        JornadaResumenDTO[] resp = restTemplate.getForObject(
            urlConfiguracion + "/api/configuracion/jornadas",
            JornadaResumenDTO[].class
        );
        return Arrays.stream(resp).map(this::toJornada).collect(Collectors.toList());
    }
}
```

### 4. Decorator — Logging sobre ServicioUsuarios (ms-usuarios)

```java
public class ServicioUsuariosConLog implements IServicioUsuarios {
    private final IServicioUsuarios wrapped;

    public ServicioUsuariosConLog(IServicioUsuarios servicio) {
        this.wrapped = servicio;
    }

    @Override
    public Usuario autenticar(String user, String pass) {
        System.out.println("[LOG] Login: " + user);
        Usuario u = wrapped.autenticar(user, pass);
        if (u == null) System.out.println("[LOG] Fallo login: " + user);
        return u;
    }
}
```

## De Comportamiento

### 5. Observer — Listeners de RabbitMQ (ms-usuarios)

```java
public interface EventoObserver<T> {
    void onEvento(T evento);
}

@Component
public class NotificacionCitaObserver implements EventoObserver<CitaCreadaEvent> {
    @RabbitListener(queues = "cola.notificacion.paciente")
    @Override
    public void onEvento(CitaCreadaEvent evento) {
        System.out.println("Cita creada para paciente " + evento.getIdPaciente());
    }
}
```

### 6. Strategy — Cálculo de Franjas Horarias (ms-agendamiento)

```java
public interface EstrategiaFranja {
    List<LocalTime> generarFranjas(LocalTime inicio, LocalTime fin);
}

public class FranjaFija30Min implements EstrategiaFranja {
    @Override
    public List<LocalTime> generarFranjas(LocalTime inicio, LocalTime fin) {
        List<LocalTime> franjas = new ArrayList<>();
        LocalTime t = inicio;
        while (t.isBefore(fin)) { franjas.add(t); t = t.plusMinutes(30); }
        return franjas;
    }
}

public class FranjaPersonalizada implements EstrategiaFranja {
    private final int intervalo;
    public FranjaPersonalizada(int intervalo) { this.intervalo = intervalo; }

    @Override
    public List<LocalTime> generarFranjas(LocalTime inicio, LocalTime fin) {
        List<LocalTime> franjas = new ArrayList<>();
        LocalTime t = inicio;
        while (t.isBefore(fin)) { franjas.add(t); t = t.plusMinutes(intervalo); }
        return franjas;
    }
}
// Uso en ServicioAgendamiento:
EstrategiaFranja estrategia = new FranjaPersonalizada(jornada.getIntervaloMinutos());
List<LocalTime> franjas = estrategia.generarFranjas(jornada.getHoraInicio(), jornada.getHoraFin());
```

---

# PARTE 7 — Docker

## Dockerfile optimizado (multi-stage)

```dockerfile
# Etapa 1: Build con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Etapa 2: Runtime mínimo (solo JRE Alpine ~85MB)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## docker-compose.yml completo

```yaml
version: '3.9'

services:

  # ── BASES DE DATOS ──────────────────────────────────────────────────

  db-usuarios:
    image: postgres:15-alpine
    container_name: db_usuarios
    environment:
      POSTGRES_DB: db_usuarios
      POSTGRES_USER: piedrazul
      POSTGRES_PASSWORD: piedrazul123
    ports:
      - "5433:5432"
    volumes:
      - vol_db_usuarios:/var/lib/postgresql/data
    networks:
      - piedrazul-net

  db-agendamiento:
    image: postgres:15-alpine
    container_name: db_agendamiento
    environment:
      POSTGRES_DB: db_agendamiento
      POSTGRES_USER: piedrazul
      POSTGRES_PASSWORD: piedrazul123
    ports:
      - "5434:5432"
    volumes:
      - vol_db_agendamiento:/var/lib/postgresql/data
    networks:
      - piedrazul-net

  db-configuracion:
    image: postgres:15-alpine
    container_name: db_configuracion
    environment:
      POSTGRES_DB: db_configuracion
      POSTGRES_USER: piedrazul
      POSTGRES_PASSWORD: piedrazul123
    ports:
      - "5435:5432"
    volumes:
      - vol_db_configuracion:/var/lib/postgresql/data
    networks:
      - piedrazul-net

  # ── RABBITMQ ────────────────────────────────────────────────────────

  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    container_name: rabbitmq
    ports:
      - "5672:5672"     # AMQP
      - "15672:15672"   # Panel web: http://localhost:15672 (admin/admin123)
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    networks:
      - piedrazul-net

  # ── MICROSERVICIOS ──────────────────────────────────────────────────

  ms-usuarios:
    build: ./ms-usuarios
    container_name: ms_usuarios
    ports:
      - "8081:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db-usuarios:5432/db_usuarios
      SPRING_DATASOURCE_USERNAME: piedrazul
      SPRING_DATASOURCE_PASSWORD: piedrazul123
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_USERNAME: admin
      SPRING_RABBITMQ_PASSWORD: admin123
    depends_on:
      - db-usuarios
      - rabbitmq
    networks:
      - piedrazul-net

  ms-agendamiento:
    build: ./ms-agendamiento
    container_name: ms_agendamiento
    ports:
      - "8082:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db-agendamiento:5432/db_agendamiento
      SPRING_DATASOURCE_USERNAME: piedrazul
      SPRING_DATASOURCE_PASSWORD: piedrazul123
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_USERNAME: admin
      SPRING_RABBITMQ_PASSWORD: admin123
      MS_USUARIOS_URL: http://ms-usuarios:8080
      MS_CONFIGURACION_URL: http://ms-configuracion:8080
    depends_on:
      - db-agendamiento
      - rabbitmq
      - ms-usuarios
      - ms-configuracion
    networks:
      - piedrazul-net

  ms-configuracion:
    build: ./ms-configuracion
    container_name: ms_configuracion
    ports:
      - "8083:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db-configuracion:5432/db_configuracion
      SPRING_DATASOURCE_USERNAME: piedrazul
      SPRING_DATASOURCE_PASSWORD: piedrazul123
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_USERNAME: admin
      SPRING_RABBITMQ_PASSWORD: admin123
      MS_USUARIOS_URL: http://ms-usuarios:8080
    depends_on:
      - db-configuracion
      - rabbitmq
    networks:
      - piedrazul-net

  # ── API GATEWAY ─────────────────────────────────────────────────────

  api-gateway:
    build: ./api-gateway
    container_name: api_gateway
    ports:
      - "8080:8080"
    environment:
      MS_USUARIOS_URL: http://ms-usuarios:8080
      MS_AGENDAMIENTO_URL: http://ms-agendamiento:8080
      MS_CONFIGURACION_URL: http://ms-configuracion:8080
    depends_on:
      - ms-usuarios
      - ms-agendamiento
      - ms-configuracion
    networks:
      - piedrazul-net

volumes:
  vol_db_usuarios:
  vol_db_agendamiento:
  vol_db_configuracion:

networks:
  piedrazul-net:
    driver: bridge
```

---

# PARTE 8 — API Gateway (Spring Cloud Gateway)

## pom.xml del gateway

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

## application.yml del gateway

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: ms-usuarios
          uri: http://ms-usuarios:8080
          predicates:
            - Path=/api/auth/**, /api/personas/**

        - id: ms-agendamiento
          uri: http://ms-agendamiento:8080
          predicates:
            - Path=/api/citas/**

        - id: ms-configuracion
          uri: http://ms-configuracion:8080
          predicates:
            - Path=/api/configuracion/**
```

## Filtro JWT global

```java
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> RUTAS_PUBLICAS = List.of(
        "/api/auth/login",
        "/api/personas/pacientes"  // registro público
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (RUTAS_PUBLICAS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange); // sin validación
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Aquí se valida la firma del JWT
        // Si es válido, se propaga la petición al microservicio destino
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() { return -1; }
}
```

---

# PARTE 9 — Swagger/OpenAPI

Agregar en cada microservicio:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

UI disponible en: `http://localhost:808X/swagger-ui/index.html`

```java
@RestController
@RequestMapping("/api/citas")
@Tag(name = "Agendamiento", description = "Gestión de citas médicas")
public class CitaController {

    @Operation(summary = "Crear una nueva cita")
    @ApiResponse(responseCode = "201", description = "Cita creada exitosamente")
    @PostMapping
    public ResponseEntity<Cita> crearCita(@RequestBody CitaDTO dto) {
        // ...
    }
}
```

---

# PARTE 10 — Orden de Implementación Recomendado

1. Crear tres proyectos Spring Boot vacíos: `ms-usuarios`, `ms-agendamiento`, `ms-configuracion`
2. Migrar entidades y repositorios a cada proyecto con su `application.yml` y BD propia
3. Exponer endpoints REST y documentar con Swagger
4. Agregar RabbitMQ: publicador en `ms-agendamiento`, consumidor en `ms-usuarios`
5. Crear `JornadaHttpAdapter` en `ms-agendamiento` para consultar jornadas via HTTP
6. Crear el `api-gateway` con rutas y filtro JWT
7. Escribir Dockerfiles y probar con `docker-compose up --build`
8. Verificar mensajería desde el panel RabbitMQ: `http://localhost:15672`
