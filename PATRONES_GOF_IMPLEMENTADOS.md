# Patrones GOF implementados en el proyecto

Este documento resume los **6 patrones GOF** que se añadieron al sistema, en qué archivos quedaron y cómo entender su uso en el código.

## Objetivo de estos cambios

Se aplicaron patrones que aportan estructura real a la arquitectura actual (frontend JavaFX + microservicios), evitando meter patrones solo por cumplir.

---

## 1) Factory Method

### Que es
Permite centralizar la creacion de objetos de un mismo tipo usando metodos de fabrica, en lugar de instanciarlos de forma repetida en muchos lugares.

### Donde se agrego
- `frontend/src/main/java/com/proyecto/presentacion/ClienteHttp.java`

### Como se implemento
Dentro de `ClienteHttp` se agrego la clase interna `HttpRequestFactory` con metodos:
- `crearGet(...)`
- `crearPost(...)`
- `crearPut(...)`
- `crearDelete(...)`

Tambien se creo `baseBuilder(...)` para aplicar headers comunes (`Content-Type`) y, si existe, el token (`Authorization`).

### Que mejoro
- Se elimino duplicacion al construir `HttpRequest`.
- Si mañana cambian headers o politicas de request, se cambia en un solo punto.

---

## 2) Facade

### Que es
Una fachada expone una API simple para ocultar complejidad interna.

### Donde se agrego
- Nuevo archivo: `frontend/src/main/java/com/proyecto/presentacion/facade/BackendFacade.java`
- Integrado en:
  - `frontend/src/main/java/com/proyecto/presentacion/controladores/ControladorLogin.java`
  - `frontend/src/main/java/com/proyecto/presentacion/controladores/ControladorAgendarCita.java`

### Como se implemento
`BackendFacade` encapsula operaciones del backend:
- `login(...)`
- `listarMedicosActivos()`
- `buscarPacientePorDocumento(...)`
- `registrarPaciente(...)`
- `agendarCitaWeb(...)`

Los controladores dejaron de construir manualmente las llamadas HTTP para delegarlas a la fachada.

### Que mejoro
- Controladores JavaFX mas limpios.
- Menor acoplamiento UI <-> detalles HTTP.
- Mas facil testear y refactorizar.

---

## 3) Strategy

### Que es
Define una familia de algoritmos intercambiables bajo una interfaz comun.

### Donde se agrego
- `microservicio_usuarios/src/main/java/com/proyecto/microservicio_usuarios/servicio/strategy/TokenGenerationStrategy.java`
- `microservicio_usuarios/src/main/java/com/proyecto/microservicio_usuarios/servicio/strategy/Base64TokenGenerationStrategy.java`
- Uso en:
  - `microservicio_usuarios/src/main/java/com/proyecto/microservicio_usuarios/servicio/ServicioAuth.java`

### Como se implemento
Se extrajo la logica de generacion de token desde `ServicioAuth` hacia una estrategia:
- Interfaz `TokenGenerationStrategy` con `generarToken(Usuario usuario)`.
- Implementacion actual `Base64TokenGenerationStrategy`.
- `ServicioAuth` ahora depende de la interfaz y no del algoritmo concreto.

### Que mejoro
- Facil cambiar a JWT u otra estrategia sin romper `ServicioAuth`.
- Menor acoplamiento y mejor separacion de responsabilidades.

---

## 4) Adapter

### Que es
Convierte una interfaz existente en otra que el sistema necesita, desacoplando infraestructura.

### Donde se agrego
- `microservicio_configuracion/src/main/java/com/proyecto/microservicio_configuracion/servicio/adapter/MedicoClientPort.java`
- `microservicio_configuracion/src/main/java/com/proyecto/microservicio_configuracion/servicio/adapter/RestTemplateMedicoClientAdapter.java`
- Integrado en:
  - `microservicio_configuracion/src/main/java/com/proyecto/microservicio_configuracion/servicio/ServicioMedicoCliente.java`

### Como se implemento
Se creo un puerto (`MedicoClientPort`) y un adaptador concreto con `RestTemplate`.
`ServicioMedicoCliente` ahora consume el puerto, no `RestTemplate` directo.

### Que mejoro
- Si luego migras de `RestTemplate` a `WebClient`, cambias el adaptador y no la logica de negocio.
- Mejor testeabilidad por depender de una abstraccion.

---

## 5) Builder

### Que es
Permite construir objetos complejos paso a paso con una API fluida.

### Donde se agrego
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/modelo/Cita.java`
- Uso en:
  - `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/servicio/ServicioAgendamiento.java`

### Como se implemento
En `Cita` se agrego:
- `Cita.builder()`
- clase interna `Builder` con metodos (`idPaciente`, `idMedico`, `fecha`, `horaInicio`, `horaFin`, `idEstadoCita`, `build`).

En `agendarCitaWeb(...)` se reemplazo construccion por setters por la construccion fluida con builder.

### Que mejoro
- Construccion de `Cita` mas legible.
- Menor riesgo de olvidar campos al crear objetos.

---

## 6) Template Method

### Que es
Define el esqueleto de un algoritmo en una clase base y permite variaciones en pasos concretos (hooks).

### Donde se agrego
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/servicio/template/CitaProcesoTemplate.java`
- Integrado en:
  - `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/servicio/ServicioAgendamiento.java`

### Como se implemento
Se creo el metodo plantilla `guardarYPublicar(...)` que:
1. Valida que la cita exista.
2. Guarda en repositorio.
3. Publica evento de cita creada.
4. Ejecuta `despuesDeGuardar(...)` (hook).

`ServicioAgendamiento` usa dos flujos concretos (`flujoWeb` y `flujoManual`) reutilizando el mismo algoritmo base.

### Que mejoro
- Se evita repetir el flujo guardar/publicar en varios metodos.
- Queda punto de extension claro para reglas futuras por tipo de flujo.

---

## 7) State

### Que es
Permite que un objeto cambie su comportamiento cuando su estado interno varia. El patrón encapsula el comportamiento que depende del estado en clases separadas.

### Donde se agrego
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/EstadoConsultaStrategy.java` (interfaz)
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/EstadoPendiente.java`
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/EstadoConfirmada.java`
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/EstadoCompletada.java`
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/EstadoCancelada.java`
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/EstadoNoAsistio.java`
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/FabricaEstados.java`
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/TablaTransiciones.java`
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/Consulta.java` (contexto)
- `microservicio_agendamiento/src/main/java/com/proyecto/microservicio_agendamiento/estado/DemostracionPatronState.java` (tests)

### Como se implemento
Se creo:
1. **Interfaz `EstadoConsultaStrategy`** que define operaciones comunes a todos los estados:
   - `confirmar()`, `cancelar()`, `completar()`, `marcarNoAsistio()`
   - `obtenerNombre()`, `obtenerID()`, `obtenerDescripcion()`

2. **5 Estados Concretos** que implementan la interfaz:
   - `EstadoPendiente`: Consulta recién agendada
   - `EstadoConfirmada`: Paciente confirmó
   - `EstadoCompletada`: Consulta realizada (TERMINAL)
   - `EstadoCancelada`: Consulta cancelada (TERMINAL)
   - `EstadoNoAsistio`: Paciente no asistió (TERMINAL)

3. **Clase Contexto `Consulta`** que:
   - Contiene los datos de la consulta (paciente, médico, fecha, hora)
   - Mantiene un `estadoActual` del tipo `EstadoConsultaStrategy`
   - Delega operaciones al estado actual: `confirmar()`, `cancelar()`, etc.

4. **Factory `FabricaEstados`** para crear estados por ID o estado inicial

5. **Documentación `TablaTransiciones`** que muestra visualmente qué transiciones son válidas

### Tabla de Transiciones Implementada
```
┌──────────────────┬────────────┬──────────┬───────────┬─────────────┐
│ ESTADO ACTUAL    │ CONFIRMAR  │ CANCELAR │ COMPLETAR │ NO ASISTIO  │
├──────────────────┼────────────┼──────────┼───────────┼─────────────┤
│ Pendiente        │     ✓      │    ✓     │     ✗     │      ✗      │
│ Confirmada       │     ✓      │    ✓     │     ✓     │      ✓      │
│ Completada       │     ✗      │    ✗     │     ✓     │      ✗      │
│ Cancelada        │     ✗      │    ✓     │     ✗     │      ✗      │
│ No Asistió       │     ✗      │    ✗     │     ✗     │      ✓      │
└──────────────────┴────────────┴──────────┴───────────┴─────────────┘
```

### Que mejoro
- **Validación automática:** Cada estado valida si una operación es permitida
- **Sin cascadas de if/else:** La lógica está encapsulada en cada clase de estado
- **Fácil extensión:** Agregar un nuevo estado = crear una nueva clase, sin modificar existentes
- **Mejor testabilidad:** Cada estado puede testearse independientemente
- **Documentación clara:** Las transiciones válidas están explícitas en cada estado

### Ejemplos de Uso
```java
// Crear consulta (comienza en PENDIENTE)
Consulta consulta = new Consulta(10, 3, fecha, horaInicio, horaFin);

// Cambiar estado (el estado actual decide si es válido)
if (consulta.confirmar()) {
    // Transición PENDIENTE → CONFIRMADA exitosa
    guardarEnBD(consulta);
} else {
    // Transición fue rechazada
}

// Obtener información
String estado = consulta.getEstadoNombre();  // "Confirmada"
int idEstado = consulta.getEstadoID();       // 3
```

---

## Documentacion Incluida para State Pattern

- `PATRON_STATE_EXPLICACION.md` - Explicación detallada del patrón
- `PATRON_STATE_RESUMEN.md` - Resumen ejecutivo
- `PATRON_STATE_DIAGRAMAS.md` - Diagramas UML y visualización
- `PATRON_STATE_REFERENCIA.md` - Guía de referencia rápida para desarrolladores

---

## Resumen rapido (patron -> modulo)

- Factory Method -> `frontend` (`ClienteHttp`)
- Facade -> `frontend` (`BackendFacade` + controladores)
- Strategy -> `microservicio_usuarios` (`ServicioAuth` + estrategias de token)
- Adapter -> `microservicio_configuracion` (cliente de medicos)
- Builder -> `microservicio_agendamiento` (`Cita`)
- Template Method -> `microservicio_agendamiento` (proceso de guardado/publicacion)
- **State -> `microservicio_agendamiento` (ciclo de vida de Consulta)** ← NUEVO

**TOTAL: 7 PATRONES GOF IMPLEMENTADOS**

---

## Nota de compilacion

Durante validacion:
- `frontend` compila con JDK 17.
- `microservicio_usuarios`, `microservicio_configuracion` y `microservicio_agendamiento` requieren Java 21 (sus `pom.xml` usan release 21).
- Las clases del patrón State fueron compiladas y testeadas exitosamente con Java 23.

Si quieres, en un siguiente paso puedo dejar tambien un `CHECKLIST_PATRONES.md` corto para presentacion (tipo evidencia academica: patron, archivo, captura sugerida y prueba recomendada).
