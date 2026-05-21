# Patrón STATE - Implementación en Consultas Médicas

## Objetivo
Implementar el patrón de diseño **STATE** (Estado) para gestionar el ciclo de vida de una consulta médica de manera segura, permitiendo que cada estado encapsule su propio comportamiento y valide sus transiciones.

---

## ¿Qué es el Patrón STATE?

El patrón STATE es un patrón de comportamiento que permite que un objeto **cambie su comportamiento** según su **estado interno**. 

### Idea Principal
En lugar de usar cascadas de `if/else` para verificar el estado actual, creamos una clase para **cada estado** que:
1. Implementa una **interfaz común**
2. Define **qué operaciones son válidas** en ese estado
3. **Rechaza operaciones inválidas**

### Problema que resuelve

#### ❌ SIN el patrón STATE:
```java
public class Cita {
    private int estado; // 1=Cancelada, 2=Pendiente, 3=Confirmada, 4=Completada, 5=NoAsistio
    
    public boolean confirmar() {
        if (estado == 2) { // Si es PENDIENTE
            estado = 3;   // Cambiar a CONFIRMADA
            return true;
        }
        if (estado == 3) { // Si es CONFIRMADA
            return true;   // Idempotente
        }
        // Si es otro estado... ¿qué hacemos?
        return false;
    }
    
    public boolean completar() {
        // Múltiples if/else según el estado actual
        if (estado == 3) {
            estado = 4;
            return true;
        }
        // ... más ifs
        return false;
    }
    // Duplicación y complejidad creciente...
}
```

#### ✓ CON el patrón STATE:
```java
public class Consulta {
    private EstadoConsultaStrategy estadoActual;
    
    public boolean confirmar() {
        // El estado actual DECIDE si es válido
        if (estadoActual.confirmar()) {
            transicionarA(EstadoConfirmada);
            return true;
        }
        return false;
    }
}
```

---

## Estructura del Patrón en Proyecto Piedra Azul

### 1️⃣ Interfaz: `EstadoConsultaStrategy`

Define el **contrato común** que todos los estados deben implementar.

```java
public interface EstadoConsultaStrategy {
    boolean confirmar();
    boolean cancelar();
    boolean completar();
    boolean marcarNoAsistio();
    String obtenerNombre();
    int obtenerID();
    String obtenerDescripcion();
}
```

### 2️⃣ Estados Concretos (5 clases)

Cada estado implementa el comportamiento específico para ese estado.

#### **EstadoPendiente**
- Consulta acaba de ser agendada
- ✓ Puede: Confirmar, Cancelar
- ✗ No puede: Completar, Marcar como No Asistió

```java
public class EstadoPendiente implements EstadoConsultaStrategy {
    public boolean confirmar() {
        System.out.println("✓ Pendiente → Confirmada");
        return true; // SÍ es válido
    }
    
    public boolean completar() {
        System.out.println("✗ NO: Debe confirmar primero");
        return false; // NO es válido
    }
}
```

#### **EstadoConfirmada**
- Paciente confirmó asistencia
- ✓ Puede: Confirmar (idempotente), Cancelar, Completar, Marcar No Asistió
- ✗ No puede: (todas las operaciones son válidas)

#### **EstadoCompletada** (TERMINAL)
- Consulta fue realizada exitosamente
- ✓ Puede: Completar (idempotente)
- ✗ No puede: Confirmar, Cancelar, Marcar No Asistió

#### **EstadoCancelada** (TERMINAL)
- Consulta fue cancelada
- ✓ Puede: Cancelar (idempotente)
- ✗ No puede: Confirmar, Completar, Marcar No Asistió

#### **EstadoNoAsistio** (TERMINAL)
- Paciente no asistió
- ✓ Puede: Marcar No Asistió (idempotente)
- ✗ No puede: Confirmar, Cancelar, Completar

### 3️⃣ Clase Contexto: `Consulta`

Es la clase que **usa** los estados. Contiene:
- **Datos de la consulta** (paciente, médico, fecha, hora)
- **Un estado actual** (`EstadoConsultaStrategy`)
- **Métodos que delegan al estado**

```java
public class Consulta {
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoConsultaStrategy estadoActual; // ← El estado actual
    
    public boolean confirmar() {
        // Preguntar al estado actual si es válido
        if (estadoActual.confirmar()) {
            // Si es válido, cambiar de estado
            estadoActual = new EstadoConfirmada();
            return true;
        }
        return false;
    }
}
```

### 4️⃣ Tabla de Transiciones

Define **qué transiciones son válidas** desde cada estado:

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

### 5️⃣ Factory: `FabricaEstados`

Centraliza la creación de estados:

```java
public class FabricaEstados {
    public static EstadoConsultaStrategy crearEstado(int idEstado) {
        return switch (idEstado) {
            case 1 -> new EstadoCancelada();
            case 2 -> new EstadoPendiente();
            case 3 -> new EstadoConfirmada();
            case 4 -> new EstadoCompletada();
            case 5 -> new EstadoNoAsistio();
        };
    }
}
```

---

## Flujos Principales

### 🟢 FLUJO NORMAL (Éxito)
```
Pendiente → Confirmar → Confirmada → Completar → Completada
```
- Paciente agenda cita
- Paciente confirma asistencia
- Médico realiza la consulta

### 🟡 FLUJO CON CANCELACIÓN (Pendiente)
```
Pendiente → Cancelar → Cancelada
```
- Paciente agenda cita
- Paciente la cancela ANTES de confirmar

### 🟡 FLUJO CON CANCELACIÓN (Confirmada)
```
Pendiente → Confirmar → Confirmada → Cancelar → Cancelada
```
- Paciente agenda y confirma
- Paciente la cancela DESPUÉS de confirmar

### 🔴 FLUJO CON INASISTENCIA
```
Pendiente → Confirmar → Confirmada → No Asistió → No Asistió
```
- Paciente agenda y confirma
- Paciente no asiste a la cita
- Médico registra la inasistencia

---

## Ventajas del Patrón STATE

### 1. **Encapsulación del Comportamiento**
Cada estado maneja su propio comportamiento. No hay cascadas de `if/else` en la clase principal.

### 2. **Fácil de Mantener**
Para cambiar el comportamiento de un estado, editas su clase, sin tocar otras.

### 3. **Fácil de Extender**
Para agregar un nuevo estado, creas una nueva clase que implemente `EstadoConsultaStrategy`.

### 4. **Validación Distribuida**
Las transiciones se validan en el estado actual, no en la clase principal.

### 5. **Código Más Limpio**
La clase principal (`Consulta`) es muy simple: solo delega al estado actual.

---

## Flujo de Ejecución - Ejemplo Práctico

### Ejemplo: Confirmar una consulta PENDIENTE

```java
Consulta consulta = new Consulta(...);
// Estado actual: PENDIENTE

consulta.confirmar();
// 1. Se llama al método confirmar() en Consulta
// 2. Consulta pregunta: estadoActual.confirmar()
// 3. EstadoPendiente.confirmar() retorna true ✓
// 4. Consulta cambia: estadoActual = new EstadoConfirmada()
// 5. Estado actual: CONFIRMADA
```

### Ejemplo: Intentar confirmar una consulta CANCELADA

```java
consulta.cancelar(); // → Consultara está CANCELADA
consulta.confirmar(); // ¿Válido?

// 1. Se llama al método confirmar() en Consulta
// 2. Consulta pregunta: estadoActual.confirmar()
// 3. EstadoCancelada.confirmar() retorna false ✗
// 4. Consulta NO cambia el estado
// 5. Se muestra mensaje: "✗ NO PERMITIDO: La consulta está CANCELADA"
```

---

## Diferencias entre Estados

| Aspecto | EstadoPendiente | EstadoConfirmada | EstadoCompletada |
|---|---|---|---|
| **Es terminal?** | No | No | Sí ✓ |
| **Puede confirmar** | ✓ | ✓ | ✗ |
| **Puede cancelar** | ✓ | ✓ | ✗ |
| **Puede completar** | ✗ | ✓ | ✓ |
| **Puede marcar No Asistió** | ✗ | ✓ | ✗ |

---

## Integración con la Base de Datos

La clase `Consulta` se mapea a la tabla `cita`:

```sql
CREATE TABLE cita (
    id_cita        SERIAL PRIMARY KEY,
    id_paciente    INT NOT NULL,
    id_medico      INT NOT NULL,
    fecha          DATE NOT NULL,
    hora_inicio    TIME NOT NULL,
    hora_fin       TIME NOT NULL,
    id_estado_cita INT REFERENCES dominio_estado_cita(id_estado_cita)
);

CREATE TABLE dominio_estado_cita (
    id_estado_cita SERIAL PRIMARY KEY,
    nombre         VARCHAR(30) NOT NULL
    -- 1=Cancelada, 2=Pendiente, 3=Confirmada, 4=Completada, 5=No Asistió
);
```

Cuando cargas una `Consulta` desde BD:
```java
Consulta c = new Consulta(
    idConsulta, idPaciente, idMedico, 
    fecha, horaInicio, horaFin, 
    idEstado  // ← Usa FabricaEstados.crearEstado(idEstado)
);
```

---

## Comparación: Sin vs Con Patrón STATE

### ❌ Alternativa (Switch/If)
```java
public boolean cambiarEstado(String accion) {
    switch (estadoActual) {
        case "PENDIENTE":
            if ("confirmar".equals(accion)) {
                estadoActual = "CONFIRMADA";
                return true;
            }
            if ("cancelar".equals(accion)) {
                estadoActual = "CANCELADA";
                return true;
            }
            return false;
        case "CONFIRMADA":
            if ("confirmar".equals(accion)) return true;
            if ("cancelar".equals(accion)) {
                estadoActual = "CANCELADA";
                return true;
            }
            if ("completar".equals(accion)) {
                estadoActual = "COMPLETADA";
                return true;
            }
            // ... más casos
            return false;
        // ... más estados
    }
}
```
**Problemas:** Código largo, difícil de mantener, cambios afectan toda la clase.

### ✓ Con Patrón STATE
```java
public boolean confirmar() {
    if (estadoActual.confirmar()) {
        estadoActual = FabricaEstados.crearEstado(3); // Confirmada
        return true;
    }
    return false;
}
```
**Ventajas:** Limpio, fácil de entender, cada estado es responsable de sí mismo.

---

## Clases del Patrón en el Proyecto

```
microservicio_agendamiento/
└── src/main/java/com/proyecto/microservicio_agendamiento/
    └── estado/
        ├── EstadoConsultaStrategy.java    ← Interfaz (contrato)
        ├── EstadoPendiente.java           ← Estado concreto
        ├── EstadoConfirmada.java          ← Estado concreto
        ├── EstadoCompletada.java          ← Estado concreto
        ├── EstadoCancelada.java           ← Estado concreto
        ├── EstadoNoAsistio.java           ← Estado concreto
        ├── FabricaEstados.java            ← Factory
        ├── TablaTransiciones.java         ← Documentación
        ├── Consulta.java                  ← Contexto (usa los estados)
        └── DemostracionPatronState.java   ← Ejemplos/Tests
```

---

## Cómo Usar en el Servicio

```java
@Service
public class ServicioAgendamiento {
    
    public boolean confirmarCita(int idCita) {
        Cita cita = repositorio.obtener(idCita);
        Consulta consulta = new Consulta(
            cita.getIdCita(),
            cita.getIdPaciente(),
            cita.getIdMedico(),
            cita.getFecha(),
            cita.getHoraInicio(),
            cita.getHoraFin(),
            cita.getIdEstadoCita()
        );
        
        // Usar el patrón STATE
        if (consulta.confirmar()) {
            // Guardar el nuevo estado en BD
            cita.setIdEstadoCita(consulta.getEstadoID());
            repositorio.guardar(cita);
            return true;
        }
        return false;
    }
}
```

---

## Resumen

**El patrón STATE:**
1. Define una interfaz `EstadoConsultaStrategy`
2. Crea una clase para cada estado posible
3. Delega el comportamiento al estado actual
4. Permite transiciones seguras entre estados
5. Mantiene el código limpio y mantenible
6. Facilita agregar nuevos estados sin modificar código existente

**En Proyecto Piedra Azul:**
- Cada consulta siempre comienza en estado **PENDIENTE**
- Cada estado define qué operaciones son válidas
- Las transiciones se validan automáticamente
- El código es resistente a cambios de requisitos
