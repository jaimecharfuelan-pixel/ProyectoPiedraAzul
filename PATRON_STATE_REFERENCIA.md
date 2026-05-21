# 📚 Índice de Referencia Rápida - Patrón STATE

## 📁 Ubicación de Archivos

```
ProyectoPiedraAzul/
├── PATRON_STATE_EXPLICACION.md          ← Explicación detallada
├── PATRON_STATE_RESUMEN.md              ← Resumen ejecutivo
├── PATRON_STATE_DIAGRAMAS.md            ← Visualización con diagramas
├── PATRON_STATE_REFERENCIA.md           ← Este archivo
│
└── microservicio_agendamiento/
    ├── DemoPatronState.java             ← Demo compilable y ejecutable
    │
    └── src/main/java/com/proyecto/microservicio_agendamiento/estado/
        ├── EstadoConsultaStrategy.java   ← Interfaz (45 líneas)
        ├── EstadoPendiente.java          ← Estado #1 (50 líneas)
        ├── EstadoConfirmada.java         ← Estado #2 (48 líneas)
        ├── EstadoCompletada.java         ← Estado #3 (48 líneas)
        ├── EstadoCancelada.java          ← Estado #4 (48 líneas)
        ├── EstadoNoAsistio.java          ← Estado #5 (48 líneas)
        ├── FabricaEstados.java           ← Factory (35 líneas)
        ├── TablaTransiciones.java        ← Documentación (80 líneas)
        ├── Consulta.java                 ← Contexto (250 líneas)
        └── DemostracionPatronState.java  ← Tests (350 líneas)
```

---

## 🚀 Quick Start

### 1. Crear una Consulta
```java
import com.proyecto.microservicio_agendamiento.estado.*;

Consulta consulta = new Consulta(
    10,                              // idPaciente
    3,                               // idMedico
    LocalDate.of(2026, 4, 28),      // fecha
    LocalTime.of(14, 0),            // horaInicio
    LocalTime.of(14, 30)            // horaFin
);

// Estado actual: PENDIENTE (automático)
System.out.println(consulta.getEstadoNombre()); // Output: "Pendiente"
```

### 2. Cambiar Estado (Validado Automáticamente)
```java
// Confirmar
if (consulta.confirmar()) {
    System.out.println("Confirmada: " + consulta.getEstadoNombre());
} else {
    System.out.println("No se pudo confirmar");
}

// Completar
if (consulta.completar()) {
    System.out.println("Completada: " + consulta.getEstadoNombre());
} else {
    System.out.println("No se pudo completar");
}
```

### 3. Obtener Información del Estado
```java
String nombre = consulta.getEstadoNombre();          // "Confirmada"
int id = consulta.getEstadoID();                     // 3
String descripcion = consulta.getEstadoDescripcion(); // "Consulta confirmada..."
```

### 4. Guardar en BD
```java
if (consulta.confirmar()) {
    // Actualizar objeto de BD
    cita.setIdEstadoCita(consulta.getEstadoID());
    
    // Guardar
    repositorioCitas.save(cita);
}
```

---

## 📊 Estados y Transiciones

### Estados Disponibles

| ID | Nombre | Descripción | ¿Terminal? |
|---|---|---|---|
| 1 | **Cancelada** | Consulta cancelada | ✓ Sí |
| 2 | **Pendiente** | Esperando confirmación | No |
| 3 | **Confirmada** | Confirmada por paciente | No |
| 4 | **Completada** | Consulta realizada | ✓ Sí |
| 5 | **No Asistió** | Paciente no asistió | ✓ Sí |

### Operaciones Disponibles

| Método | Parámetros | Retorna | Propósito |
|---|---|---|---|
| `confirmar()` | - | `boolean` | Confirmar la consulta |
| `cancelar()` | - | `boolean` | Cancelar la consulta |
| `completar()` | - | `boolean` | Marcar como realizada |
| `marcarNoAsistio()` | - | `boolean` | Registrar inasistencia |
| `getEstadoNombre()` | - | `String` | Obtener nombre actual |
| `getEstadoID()` | - | `int` | Obtener ID para BD |
| `getEstadoDescripcion()` | - | `String` | Obtener descripción |

---

## 🔄 Tabla de Transiciones Completa

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

✓ = Transición permitida (retorna true, cambia estado)
✗ = Transición rechazada (retorna false, estado sin cambios)
```

---

## 💡 Ejemplos Comunes

### Flujo 1: Confirmación Normal
```java
Consulta c = new Consulta(10, 3, fecha, hora1, hora2);
c.confirmar();   // PENDIENTE → CONFIRMADA
c.completar();   // CONFIRMADA → COMPLETADA
c.cancelar();    // false (COMPLETADA es terminal)
```

### Flujo 2: Cancelación Tardía
```java
Consulta c = new Consulta(11, 4, fecha, hora1, hora2);
c.confirmar();   // PENDIENTE → CONFIRMADA
c.cancelar();    // CONFIRMADA → CANCELADA
c.marcarNoAsistio(); // false (CANCELADA es terminal)
```

### Flujo 3: Inasistencia
```java
Consulta c = new Consulta(12, 5, fecha, hora1, hora2);
c.confirmar();   // PENDIENTE → CONFIRMADA
c.marcarNoAsistio(); // CONFIRMADA → NO_ASISTIO
c.completar();   // false (NO_ASISTIO es terminal)
```

### Flujo 4: Validación Automática
```java
Consulta c = new Consulta(13, 6, fecha, hora1, hora2);
c.completar();   // false (PENDIENTE no permite completar)
c.marcarNoAsistio(); // false (PENDIENTE no permite marcar)
c.confirmar();   // true (PENDIENTE permite confirmar)
c.completar();   // true (ahora CONFIRMADA permite completar)
```

---

## 🧪 Testing

### Ejecutar la Demostración
```bash
cd microservicio_agendamiento
javac DemoPatronState.java
java DemoPatronState
```

**Output esperado:** 4 escenarios demostrando:
- ✅ Flujo normal
- ✅ Cancelaciones
- ✅ Inasistencias
- ✅ Rechazos de transiciones inválidas

---

## 📖 Documentación Detallada

| Documento | Cubre | Audience |
|---|---|---|
| **PATRON_STATE_EXPLICACION.md** | Teoría completa, conceptos | Arquitectos, Seniors |
| **PATRON_STATE_RESUMEN.md** | Resumen ejecutivo | Managers, PMs |
| **PATRON_STATE_DIAGRAMAS.md** | Visualización UML | Diseñadores, Devs visuales |
| **PATRON_STATE_REFERENCIA.md** | Este archivo: referencia rápida | Devs que necesitan código |

---

## 🔧 Integración en Servicios

### En `ServicioAgendamiento.java`

```java
public boolean confirmarCita(int idCita) {
    // 1. Obtener de BD
    Cita citaBD = repositorioCitas.findById(idCita);
    
    // 2. Crear Consulta con el estado actual
    Consulta consulta = new Consulta(
        citaBD.getIdCita(),
        citaBD.getIdPaciente(),
        citaBD.getIdMedico(),
        citaBD.getFecha(),
        citaBD.getHoraInicio(),
        citaBD.getHoraFin(),
        citaBD.getIdEstadoCita()
    );
    
    // 3. Intentar cambiar estado
    if (consulta.confirmar()) {
        // 4. Guardar en BD
        citaBD.setIdEstadoCita(consulta.getEstadoID());
        repositorioCitas.save(citaBD);
        
        // 5. Publicar evento
        publicadorCitas.publicarCitaConfirmada(citaBD);
        return true;
    }
    
    return false;
}
```

---

## 🎯 Casos de Uso por Rol

### 👨‍⚕️ Médico
- Completar consulta: `consulta.completar()`
- Registrar no asistencia: `consulta.marcarNoAsistio()`
- Ver estado: `consulta.getEstadoNombre()`

### 👤 Paciente
- Confirmar consulta: `consulta.confirmar()`
- Cancelar consulta: `consulta.cancelar()`
- Ver estado: `consulta.getEstadoNombre()`

### 👨‍💼 Admin
- Cargar cualquier estado: `new Consulta(..., idEstadoCita)`
- Cambiar manualmente: `consulta.cancelar()` / `confirmar()`
- Ver histórico: Los estados terminales no cambian

---

## 🔍 Debugging

### Verificar el estado actual
```java
System.out.println("Estado: " + consulta.getEstadoNombre());
System.out.println("ID: " + consulta.getEstadoID());
System.out.println("Descripción: " + consulta.getEstadoDescripcion());
```

### Ver tabla de transiciones
```java
TablaTransiciones.mostrarTabla();
TablaTransiciones.mostrarFlujosPrincipales();
```

### Mostrar detalles de consulta
```java
consulta.mostrarDetalle();
```

---

## ⚠️ Errores Comunes

### ❌ Esperar que Pendiente → Completada
```java
Consulta c = new Consulta(...);
boolean resultado = c.completar();
// Retorna FALSE: Debe confirmar primero
```

### ❌ Intentar cambiar un estado terminal
```java
c.completar();   // → COMPLETADA (terminal)
c.cancelar();    // Retorna FALSE
```

### ✓ Forma Correcta
```java
// Validar ANTES de guardar
if (consulta.confirmar()) {
    // Cambio exitoso
    persistir(consulta);
} else {
    // Cambio rechazado
    mostrarError("No se puede confirmar");
}
```

---

## 🏗️ Arquitectura General

```
Controlador HTTP
    ↓
Servicio (ServicioAgendamiento)
    ↓
[Patrón STATE]
    ├─ Consulta (contexto)
    ├─ EstadoConsultaStrategy (interfaz)
    ├─ Estados concretos (5 clases)
    └─ FabricaEstados (factory)
    ↓
Repositorio (RepositorioCitas)
    ↓
Base de Datos
```

---

## 📋 Checklist para Implementadores

- [ ] Leer `PATRON_STATE_EXPLICACION.md`
- [ ] Revisar `PATRON_STATE_DIAGRAMAS.md`
- [ ] Ejecutar `DemoPatronState.java`
- [ ] Estudiar clase `Consulta.java`
- [ ] Entender cómo cada estado valida operaciones
- [ ] Integrar en `ServicioAgendamiento`
- [ ] Crear tests unitarios para cada estado
- [ ] Verificar con datos reales en BD

---

## 🚀 Próximos Pasos

1. **Integración:** Reemplazar lógica de `EstadoCita` en servicio
2. **Testing:** Crear tests JUnit para cada estado
3. **Auditoría:** Agregar quién cambió el estado y cuándo
4. **Notificaciones:** Enviar eventos cuando el estado cambia
5. **Extensión:** Soportar diferentes reglas por tipo de usuario

---

## 📞 Referencia de Métodos

```java
// Crear
new Consulta(idPaciente, idMedico, fecha, horaInicio, horaFin)

// Cambiar estado
consulta.confirmar()        // → true/false
consulta.cancelar()         // → true/false
consulta.completar()        // → true/false
consulta.marcarNoAsistio()  // → true/false

// Obtener información
consulta.getEstadoNombre()       // → "Pendiente"
consulta.getEstadoID()           // → 2
consulta.getEstadoDescripcion()  // → "..."

// Datos
consulta.getIdConsulta()
consulta.getIdPaciente()
consulta.getIdMedico()
consulta.getFecha()
consulta.getHoraInicio()
consulta.getHoraFin()

// Setters
consulta.setIdPaciente()
consulta.setIdMedico()
// ... etc

// Display
consulta.mostrarDetalle()    // Imprime en consola
consulta.toString()          // String resumen
```

---

## 🎓 Conceptos Clave

**State Pattern:** Encapsula comportamiento que varía según el estado
**Strategy Pattern:** Interfaz común para algoritmos intercambiables
**Factory Method:** Centraliza creación de objetos
**Delegación:** El contexto delega al estado actual
**Encapsulación:** Cada estado maneja su propia lógica

---

## ✅ Conclusión

El patrón STATE está listo para:
- ✓ Integración en código de producción
- ✓ Testing y validación
- ✓ Extensión con nuevos estados
- ✓ Documentación para el equipo

**Contactar arquitecto del proyecto para la integración final.**
