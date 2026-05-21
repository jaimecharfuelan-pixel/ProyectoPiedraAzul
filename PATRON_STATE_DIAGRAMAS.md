# Patrón STATE - Diagramas UML y Visualización

## Diagrama de Clases del Patrón STATE

```
┌────────────────────────────────────────────────────────────────────┐
│                    EstadoConsultaStrategy (Interface)               │
│                                                                     │
│  + confirmar(): boolean                                             │
│  + cancelar(): boolean                                              │
│  + completar(): boolean                                             │
│  + marcarNoAsistio(): boolean                                       │
│  + obtenerNombre(): String                                          │
│  + obtenerID(): int                                                 │
│  + obtenerDescripcion(): String                                     │
└────────────────────────────────────────────────────────────────────┘
                           ▲
                           │ implements
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────────────────┐ ┌──────────────┐ ┌──────────────────┐
   │ EstadoPendiente│ │EstadoConfir- │ │ EstadoCompletada │
   │                │ │  mada        │ │                  │
   │+ confirmar()✓  │ │              │ │+ confirmar()✗    │
   │+ cancelar()✓   │ │+ confirmar()✓│ │+ cancelar()✗     │
   │+ completar()✗  │ │+ cancelar()✓ │ │+ completar()✓    │
   │+ noAsistio()✗  │ │+ completar()✓│ │+ noAsistio()✗    │
   └────────────────┘ │+ noAsistio()✓│ └──────────────────┘
                      └──────────────┘
        
        ┌─────────────────┐       ┌──────────────────┐
        │EstadoCancelada  │       │ EstadoNoAsistio  │
        │                 │       │                  │
        │+ confirmar()✗   │       │+ confirmar()✗    │
        │+ cancelar()✓    │       │+ cancelar()✗     │
        │+ completar()✗   │       │+ completar()✗    │
        │+ noAsistio()✗   │       │+ noAsistio()✓    │
        └─────────────────┘       └──────────────────┘

                                ▲
                                │ usa
                    ┌───────────────────────┐
                    │      Consulta         │
                    ├───────────────────────┤
                    │- idConsulta: int      │
                    │- idPaciente: int      │
                    │- idMedico: int        │
                    │- fecha: LocalDate     │
                    │- horaInicio: LocalTime│
                    │- horaFin: LocalTime   │
                    │- estadoActual:        │
                    │  EstadoConsultaStr.   │
                    ├───────────────────────┤
                    │+ confirmar(): boolean │
                    │+ cancelar(): boolean  │
                    │+ completar(): boolean │
                    │+ getEstadoNombre()    │
                    │+ getEstadoID()        │
                    └───────────────────────┘
                                ▲
                                │ crea
                    ┌───────────────────────┐
                    │   FabricaEstados      │
                    ├───────────────────────┤
                    │+ crearEstado(id: int) │
                    │+ crearEstadoInicial() │
                    └───────────────────────┘
```

---

## Diagrama de Transiciones de Estado

```
                    ┌──────────────────────────────────────────┐
                    │ NUEVA CONSULTA (constructor)             │
                    └──────────────────────────────────────────┘
                                      │
                                      ▼
                        ╔═══════════════════════╗
                        ║     PENDIENTE         ║
                        ║  (Esperando conf.)    ║
                        ╚═══════════════════════╝
                             │           │
            ┌────────────────┘           └──────────────┐
            │                                           │
     confirmar()                                   cancelar()
            │                                           │
            ▼                                           ▼
     ╔═══════════════════════╗            ╔═══════════════════╗
     ║   CONFIRMADA          ║            ║   CANCELADA       ║
     ║  (Listo para realizar)║            ║   (Cancelada)     ║
     ╚═══════════════════════╝            ╚═══════════════════╝
        │      │        │                       (TERMINAL)
        │      │        │
   conf │      │        │     marcar
   irm. │ canc │complet │     NoAsistió
   ar() │ alar │ar()    │
        │()    │        │
        ▼      ▼        ▼
       ✓      ✓        ✓
                │
        ┌───────┼──────────┐
        │       │          │
        ▼       ▼          ▼
   ╔════════╗ ╔════════╗ ╔════════════╗
   ║CONFIRM│ ║CANCEL  │ ║NO ASISTIÓ  ║
   ║ADA    │ ║ADA     │ ║            ║
   ╚════════╝ ╚════════╝ ╚════════════╝
    (TERMINAL) (TERMINAL) (TERMINAL)
```

---

## Diagrama de Flujo: Crear y Cambiar Estado

```
START
  │
  ├─> new Consulta(...)
  │      │
  │      └─> FabricaEstados.crearEstadoInicial()
  │          └─> new EstadoPendiente()
  │
  ├─> consulta.confirmar()
  │      │
  │      └─> estadoActual.confirmar()  [delegación]
  │           │
  │           ├─> EstadoPendiente.confirmar()
  │           │   ├─> return true ✓
  │           │   └─> Consulta cambia estado a CONFIRMADA
  │           │
  │           ├─> EstadoConfirmada.confirmar()
  │           │   └─> return true (idempotente)
  │           │
  │           └─> EstadoCompletada.confirmar()
  │               └─> return false ✗
  │
  ├─> consulta.cancelar()
  │      └─> ... (similar)
  │
  └─> FIN
```

---

## Diagrama de Secuencia: Confirmar una Consulta

```
Cliente          Consulta         EstadoPendiente    FabricaEstados
   │                │                   │                  │
   │                │                   │                  │
   ├─ confirmar() ─>│                   │                  │
   │                │                   │                  │
   │                ├─ confirmar() ────>│                  │
   │                │                   │                  │
   │                │<─ true ───────────┤                  │
   │                │                   │                  │
   │                ├─ crearEstado(3) ──────────────────>│
   │                │                   │                  │
   │                │                   │          crear EstadoConfirmada
   │                │                   │                  │
   │                │<─ EstadoConfirmada ────────────────┤
   │                │                   │                  │
   │                (actualizar estado)
   │                │
   │<─ true ────────┤
   │                │
   
RESULTADO: estado cambió de PENDIENTE a CONFIRMADA
```

---

## Tabla de Métodos del Patrón STATE

### EstadoConsultaStrategy (Interfaz)

```java
┌─────────────────────────────────────────────────────────────────┐
│ Método                    │ Propósito                             │
├─────────────────────────────────────────────────────────────────┤
│ confirmar()               │ Confirmar la consulta                 │
│ cancelar()                │ Cancelar la consulta                  │
│ completar()               │ Completar/realizar la consulta        │
│ marcarNoAsistio()         │ Registrar inasistencia                │
│ obtenerNombre()           │ Obtener nombre del estado actual       │
│ obtenerID()               │ Obtener ID para persistencia en BD    │
│ obtenerDescripcion()      │ Obtener descripción del estado        │
└─────────────────────────────────────────────────────────────────┘
```

---

## Matriz de Validación (Comportamiento por Estado)

### ¿Qué retorna cada operación en cada estado?

```
             confirmar()  cancelar()  completar()  marcarNoAsistio()
Pendiente       ✓ true      ✓ true    ✗ false        ✗ false
Confirmada      ✓ true      ✓ true    ✓ true         ✓ true
Completada      ✗ false     ✗ false   ✓ true         ✗ false
Cancelada       ✗ false     ✓ true    ✗ false        ✗ false
NoAsistio       ✗ false     ✗ false   ✗ false        ✓ true

Leyenda:
  ✓ = Retorna true (operación permitida)
  ✗ = Retorna false (operación rechazada)
```

---

## Ejemplo Visual: Estado Pendiente

```
┌──────────────────────────────────────────────────────────────┐
│ EstadoPendiente                                              │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  public boolean confirmar() {                                │
│    System.out.println("✓ PENDIENTE → CONFIRMADA");           │
│    return true;  // ← PERMITIDO                              │
│  }                                                           │
│                                                              │
│  public boolean completar() {                                │
│    System.out.println("✗ NO PERMITIDO: Debe confirmar");     │
│    return false;  // ← NO PERMITIDO                          │
│  }                                                           │
│                                                              │
│  public String obtenerNombre() {                             │
│    return "Pendiente";                                       │
│  }                                                           │
│                                                              │
│  public int obtenerID() {                                    │
│    return 2;  // ← Para persistencia en BD                   │
│  }                                                           │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## Patrón STATE vs Alternativa (if/else)

### ❌ CON if/else (ANTES)

```java
public class CitaAntiguaForm {
    private int estado; // 1=Cancel, 2=Pendiente, 3=Confirmada...
    
    public boolean confirmar() {
        if (estado == 2) {
            estado = 3;
            return true;
        }
        if (estado == 3) {
            return true; // idempotente
        }
        // ¿Qué pasa si es otro estado? Silenciosamente false
        return false;
    }
    
    public boolean completar() {
        if (estado == 3) {
            estado = 4;
            return true;
        }
        // Más ifs...
        return false;
    }
    // Código crece sin control...
}
```

**Problemas:**
- Cascadas de if/else
- Lógica dispersa
- Difícil de testear
- Cambios afectan toda la clase

### ✓ CON STATE PATTERN (AHORA)

```java
public class Consulta {
    private EstadoConsultaStrategy estadoActual;
    
    public boolean confirmar() {
        // Pregunta al estado actual
        if (estadoActual.confirmar()) {
            // Cambiar de estado si fue válido
            estadoActual = new EstadoConfirmada();
            return true;
        }
        return false;
    }
}

// Cada estado maneja su propia lógica
class EstadoPendiente implements EstadoConsultaStrategy {
    public boolean confirmar() {
        System.out.println("✓ PENDIENTE → CONFIRMADA");
        return true;
    }
}

class EstadoCompletada implements EstadoConsultaStrategy {
    public boolean confirmar() {
        System.out.println("✗ NO PERMITIDO");
        return false;
    }
}
```

**Ventajas:**
- Lógica encapsulada por estado
- Cada clase es responsable de sí misma
- Fácil agregar nuevos estados
- Código limpio y mantenible

---

## Ciclo de Vida Completo de una Consulta

```
1. CREACIÓN
   └─> new Consulta(paciente, medico, fecha, hora)
       └─> estado = PENDIENTE (automático)

2. CONFIRMACIÓN (por el paciente)
   └─> consulta.confirmar()
       └─> estadoActual.confirmar() ✓
           └─> estado = CONFIRMADA

3. REALIZACIÓN (por el médico)
   ├─> Opción A: completar()
   │   └─> estado = COMPLETADA (TERMINAL)
   │
   ├─> Opción B: marcarNoAsistio()
   │   └─> estado = NO_ASISTIO (TERMINAL)
   │
   └─> Opción C: cancelar()
       └─> estado = CANCELADA (TERMINAL)

4. FIN
   └─> Consulta en estado terminal (no cambia más)
```

---

## Comparación: Métodos de Operación

| Contexto | Sin STATE | Con STATE |
|---|---|---|
| **Llamar validación** | `if (estado == PENDIENTE)` | `estadoActual.confirmar()` |
| **Cambiar estado** | `estado = CONFIRMADA` | Automático en el contexto |
| **Validar transición** | Lógica en clase principal | Lógica en clase de estado |
| **Agregar nuevo estado** | Modificar clase principal | Crear nueva clase |
| **Testing** | Difícil aislar lógica | Testear estado aisladamente |
| **Mantenimiento** | Cambios afectan toda la clase | Cambios aislados por estado |

---

## Resumen Visual de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│ PATRÓN STATE - COMPONENTES                                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ 1. INTERFAZ: EstadoConsultaStrategy                      │  │
│  │    └─ Define contrato común                              │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ 2. ESTADOS CONCRETOS (5 clases)                          │  │
│  │    ├─ EstadoPendiente                                    │  │
│  │    ├─ EstadoConfirmada                                   │  │
│  │    ├─ EstadoCompletada                                   │  │
│  │    ├─ EstadoCancelada                                    │  │
│  │    └─ EstadoNoAsistio                                    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ 3. CONTEXTO: Consulta                                    │  │
│  │    └─ Usa el estado actual                               │  │
│  │    └─ Delega operaciones al estado                       │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ 4. FACTORY: FabricaEstados                               │  │
│  │    └─ Crea estados según ID                              │  │
│  │    └─ Crea estado inicial (PENDIENTE)                    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Uso Típico en el Servicio

```java
// 1. Cargar consulta existente
Cita citaBD = repositorio.obtenerPorId(idCita);
Consulta consulta = new Consulta(
    citaBD.getIdCita(),
    citaBD.getIdPaciente(),
    citaBD.getIdMedico(),
    citaBD.getFecha(),
    citaBD.getHoraInicio(),
    citaBD.getHoraFin(),
    citaBD.getIdEstadoCita()
);

// 2. Intentar cambiar estado
if (consulta.confirmar()) {
    // Transición válida
    System.out.println("Estado: " + consulta.getEstadoNombre());
    
    // 3. Guardar cambios
    citaBD.setIdEstadoCita(consulta.getEstadoID());
    repositorio.guardar(citaBD);
    
    return true;
} else {
    // Transición rechazada
    System.out.println("No se puede confirmar en estado: " 
        + consulta.getEstadoNombre());
    return false;
}
```

---

## Conclusión

El patrón STATE proporciona:
- ✅ **Claridad:** Cada estado es una clase clara
- ✅ **Seguridad:** Validación automática de transiciones
- ✅ **Extensibilidad:** Agregar estados sin modificar existentes
- ✅ **Mantenibilidad:** Cambios aislados por estado
- ✅ **Testabilidad:** Cada estado puede testearse independientemente
