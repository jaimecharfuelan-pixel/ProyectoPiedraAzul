import java.time.LocalDate;
import java.time.LocalTime;

// Interfaces
interface EstadoConsultaStrategy {
    boolean confirmar();
    boolean cancelar();
    boolean completar();
    boolean marcarNoAsistio();
    String obtenerNombre();
    int obtenerID();
    String obtenerDescripcion();
}

// Estados Concretos
class EstadoPendiente implements EstadoConsultaStrategy {
    public boolean confirmar() { System.out.println("✓ Consulta PENDIENTE → CONFIRMADA"); return true; }
    public boolean cancelar() { System.out.println("✓ Consulta PENDIENTE → CANCELADA"); return true; }
    public boolean completar() { System.out.println("✗ NO PERMITIDO: Debe confirmar primero"); return false; }
    public boolean marcarNoAsistio() { System.out.println("✗ NO PERMITIDO: No se puede marcar No Asistió siendo Pendiente"); return false; }
    public String obtenerNombre() { return "Pendiente"; }
    public int obtenerID() { return 2; }
    public String obtenerDescripcion() { return "Consulta agendada en espera de confirmación"; }
}

class EstadoConfirmada implements EstadoConsultaStrategy {
    public boolean confirmar() { System.out.println("✓ Consulta ya está CONFIRMADA (sin cambios)"); return true; }
    public boolean cancelar() { System.out.println("✓ Consulta CONFIRMADA → CANCELADA"); return true; }
    public boolean completar() { System.out.println("✓ Consulta CONFIRMADA → COMPLETADA"); return true; }
    public boolean marcarNoAsistio() { System.out.println("✓ Consulta CONFIRMADA → NO ASISTIÓ"); return true; }
    public String obtenerNombre() { return "Confirmada"; }
    public int obtenerID() { return 3; }
    public String obtenerDescripcion() { return "Consulta confirmada por el paciente"; }
}

class EstadoCompletada implements EstadoConsultaStrategy {
    public boolean confirmar() { System.out.println("✗ NO PERMITIDO: La consulta ya fue COMPLETADA"); return false; }
    public boolean cancelar() { System.out.println("✗ NO PERMITIDO: No se puede cancelar COMPLETADA"); return false; }
    public boolean completar() { System.out.println("✓ Consulta ya está COMPLETADA"); return true; }
    public boolean marcarNoAsistio() { System.out.println("✗ NO PERMITIDO: El paciente sí asistió"); return false; }
    public String obtenerNombre() { return "Completada"; }
    public int obtenerID() { return 4; }
    public String obtenerDescripcion() { return "Consulta realizada exitosamente (INMUTABLE)"; }
}

class EstadoCancelada implements EstadoConsultaStrategy {
    public boolean confirmar() { System.out.println("✗ NO PERMITIDO: La consulta está CANCELADA"); return false; }
    public boolean cancelar() { System.out.println("✓ Consulta ya está CANCELADA"); return true; }
    public boolean completar() { System.out.println("✗ NO PERMITIDO: No se puede completar CANCELADA"); return false; }
    public boolean marcarNoAsistio() { System.out.println("✗ NO PERMITIDO: Consulta cancelada"); return false; }
    public String obtenerNombre() { return "Cancelada"; }
    public int obtenerID() { return 1; }
    public String obtenerDescripcion() { return "Consulta cancelada (INMUTABLE)"; }
}

class EstadoNoAsistio implements EstadoConsultaStrategy {
    public boolean confirmar() { System.out.println("✗ NO PERMITIDO: Paciente no asistió"); return false; }
    public boolean cancelar() { System.out.println("✗ NO PERMITIDO: No se puede cancelar"); return false; }
    public boolean completar() { System.out.println("✗ NO PERMITIDO: Consulta no se realizó"); return false; }
    public boolean marcarNoAsistio() { System.out.println("✓ Consulta en estado NO ASISTIÓ"); return true; }
    public String obtenerNombre() { return "No Asistió"; }
    public int obtenerID() { return 5; }
    public String obtenerDescripcion() { return "Paciente no asistió a la consulta (INMUTABLE)"; }
}

// Factory
class FabricaEstados {
    static EstadoConsultaStrategy crearEstado(int id) {
        return switch(id) {
            case 1 -> new EstadoCancelada();
            case 2 -> new EstadoPendiente();
            case 3 -> new EstadoConfirmada();
            case 4 -> new EstadoCompletada();
            case 5 -> new EstadoNoAsistio();
            default -> throw new IllegalArgumentException("ID inválido");
        };
    }
    static EstadoConsultaStrategy crearEstadoInicial() { return new EstadoPendiente(); }
}

// Clase Consulta - PATRÓN STATE
class Consulta {
    private int idConsulta;
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoConsultaStrategy estadoActual;
    
    public Consulta(int idPaciente, int idMedico, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estadoActual = FabricaEstados.crearEstadoInicial();
    }
    
    public boolean confirmar() {
        if (estadoActual.confirmar()) {
            if (!estadoActual.obtenerNombre().equals("Confirmada")) {
                estadoActual = FabricaEstados.crearEstado(3);
            }
            return true;
        }
        return false;
    }
    
    public boolean cancelar() {
        if (estadoActual.cancelar()) {
            if (!estadoActual.obtenerNombre().equals("Cancelada")) {
                estadoActual = FabricaEstados.crearEstado(1);
            }
            return true;
        }
        return false;
    }
    
    public boolean completar() {
        if (estadoActual.completar()) {
            if (!estadoActual.obtenerNombre().equals("Completada")) {
                estadoActual = FabricaEstados.crearEstado(4);
            }
            return true;
        }
        return false;
    }
    
    public boolean marcarNoAsistio() {
        if (estadoActual.marcarNoAsistio()) {
            if (!estadoActual.obtenerNombre().equals("No Asistió")) {
                estadoActual = FabricaEstados.crearEstado(5);
            }
            return true;
        }
        return false;
    }
    
    public String getEstadoNombre() { return estadoActual.obtenerNombre(); }
    public int getEstadoID() { return estadoActual.obtenerID(); }
    
    public void mostrarDetalle() {
        System.out.println("\n┌─────────────────────────────────────────────┐");
        System.out.println("│ DETALLE DE CONSULTA                         │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.printf("│ Paciente:       %-30d │%n", idPaciente);
        System.out.printf("│ Médico:         %-30d │%n", idMedico);
        System.out.printf("│ Fecha:          %-30s │%n", fecha);
        System.out.printf("│ Hora:           %-30s │%n", horaInicio);
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.printf("│ Estado:         %-30s │%n", getEstadoNombre());
        System.out.printf("│ ID Estado:      %-30d │%n", getEstadoID());
        System.out.println("└─────────────────────────────────────────────┘\n");
    }
}

// Programa Principal
public class DemoPatronState {
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║          DEMOSTRACIÓN DEL PATRÓN STATE EN CONSULTAS                ║");
        System.out.println("║                 Sistema de Agendamiento - Piedra Azul              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        
        mostrarTabla();
        escenario1();
        escenario2();
        escenario3();
        escenario4();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    FIN DE LA DEMOSTRACIÓN                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
    }
    
    static void mostrarTabla() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("TABLA DE TRANSICIONES - PATRÓN STATE");
        System.out.println("═══════════════════════════════════════════════════════════════════\n");
        System.out.println("┌──────────────────┬────────────┬──────────┬───────────┬─────────────┐");
        System.out.println("│ ESTADO ACTUAL    │ CONFIRMAR  │ CANCELAR │ COMPLETAR │ NO ASISTIO  │");
        System.out.println("├──────────────────┼────────────┼──────────┼───────────┼─────────────┤");
        System.out.println("│ Pendiente        │     ✓      │    ✓     │     ✗     │      ✗      │");
        System.out.println("│ Confirmada       │     ✓      │    ✓     │     ✓     │      ✓      │");
        System.out.println("│ Completada       │     ✗      │    ✗     │     ✓     │      ✗      │");
        System.out.println("│ Cancelada        │     ✗      │    ✓     │     ✗     │      ✗      │");
        System.out.println("│ No Asistió       │     ✗      │    ✗     │     ✗     │      ✓      │");
        System.out.println("└──────────────────┴────────────┴──────────┴───────────┴─────────────┘\n");
    }
    
    static void escenario1() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 1: FLUJO NORMAL (ÉXITO)                                     │");
        System.out.println("│ Pendiente → Confirmada → Completada                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        Consulta c1 = new Consulta(10, 3, LocalDate.of(2026, 4, 28), LocalTime.of(14, 0), LocalTime.of(14, 30));
        System.out.println("✓ Consulta creada (Estado: PENDIENTE)");
        c1.mostrarDetalle();
        
        System.out.println("► Acción: Paciente CONFIRMA la cita");
        c1.confirmar();
        c1.mostrarDetalle();
        
        System.out.println("► Acción: Médico COMPLETA la cita");
        c1.completar();
        c1.mostrarDetalle();
    }
    
    static void escenario2() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 2: CANCELACIÓN DESPUÉS DE CONFIRMAR                        │");
        System.out.println("│ Pendiente → Confirmada → Cancelada                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        Consulta c2 = new Consulta(11, 4, LocalDate.of(2026, 4, 29), LocalTime.of(15, 0), LocalTime.of(15, 45));
        System.out.println("✓ Consulta creada");
        c2.mostrarDetalle();
        
        System.out.println("► Acción: Paciente CONFIRMA");
        c2.confirmar();
        c2.mostrarDetalle();
        
        System.out.println("► Acción: Paciente CANCELA");
        c2.cancelar();
        c2.mostrarDetalle();
    }
    
    static void escenario3() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 3: INASISTENCIA DEL PACIENTE                               │");
        System.out.println("│ Pendiente → Confirmada → No Asistió                                 │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        Consulta c3 = new Consulta(12, 5, LocalDate.of(2026, 4, 30), LocalTime.of(16, 0), LocalTime.of(16, 30));
        System.out.println("✓ Consulta creada");
        c3.mostrarDetalle();
        
        System.out.println("► Acción: Paciente CONFIRMA");
        c3.confirmar();
        c3.mostrarDetalle();
        
        System.out.println("► Acción: Médico registra NO ASISTIÓ");
        c3.marcarNoAsistio();
        c3.mostrarDetalle();
    }
    
    static void escenario4() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 4: INTENTOS DE TRANSICIONES INVÁLIDAS                      │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        Consulta c4 = new Consulta(13, 6, LocalDate.of(2026, 5, 1), LocalTime.of(10, 0), LocalTime.of(10, 30));
        System.out.println("✓ Consulta creada (Estado: PENDIENTE)");
        c4.mostrarDetalle();
        
        System.out.println("\n► Intento 1: Completar directamente (sin confirmar)");
        System.out.println("Resultado: " + (c4.completar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        System.out.println("Estado actual: " + c4.getEstadoNombre());
        
        System.out.println("\n► Intento 2: Marcar como NO ASISTIÓ (siendo PENDIENTE)");
        System.out.println("Resultado: " + (c4.marcarNoAsistio() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        System.out.println("Estado actual: " + c4.getEstadoNombre());
        
        System.out.println("\n► Acción: Confirmar");
        c4.confirmar();
        System.out.println("Estado actual: " + c4.getEstadoNombre());
        
        System.out.println("\n► Acción: Completar");
        c4.completar();
        System.out.println("Estado actual: " + c4.getEstadoNombre());
        
        System.out.println("\n► Intento 3: Cancelar una COMPLETADA");
        System.out.println("Resultado: " + (c4.cancelar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
    }
}
