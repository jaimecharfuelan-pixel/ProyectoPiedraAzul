package com.proyecto.microservicio_agendamiento.estado;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DEMOSTRACIÓN DEL PATRÓN STATE
 * 
 * Esta clase ejecuta ejemplos prácticos del patrón State aplicado a consultas médicas.
 * 
 * Muestra:
 * 1. Cómo se crea una consulta
 * 2. Las transiciones válidas (flujo normal)
 * 3. Las transiciones inválidas (rechazadas)
 * 4. Diferentes flujos posibles
 * 
 * CÓMO EJECUTAR:
 * Se puede correr desde un @SpringBootTest o desde main()
 */
public class DemostracionPatronState {
    
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║          DEMOSTRACIÓN DEL PATRÓN STATE EN CONSULTAS                ║");
        System.out.println("║                 Sistema de Agendamiento - Piedra Azul              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        
        // Mostrar tabla de transiciones
        TablaTransiciones.mostrarTabla();
        TablaTransiciones.mostrarFlujosPrincipales();
        
        // ESCENARIO 1: Flujo Normal (Éxito)
        escenario1_FlujoDNormal();
        
        // ESCENARIO 2: Flujo con Cancelación Pendiente
        escenario2_CancelacionPendiente();
        
        // ESCENARIO 3: Flujo con Inasistencia
        escenario3_Inasistencia();
        
        // ESCENARIO 4: Transiciones Inválidas
        escenario4_TransicionesInvalidas();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    FIN DE LA DEMOSTRACIÓN                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
    }
    
    // ═════════════════════════════════════════════════════════════════════════════════
    // ESCENARIO 1: Flujo Normal - Éxito
    // ═════════════════════════════════════════════════════════════════════════════════
    
    private static void escenario1_FlujoDNormal() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 1: FLUJO NORMAL (ÉXITO)                                     │");
        System.out.println("│ Pendiente → Confirmada → Completada                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        // Crear una nueva consulta
        Consulta consulta1 = new Consulta(
            10,                          // id_paciente
            3,                           // id_medico
            LocalDate.of(2026, 4, 28),   // fecha
            LocalTime.of(14, 0),         // hora_inicio
            LocalTime.of(14, 30)         // hora_fin
        );
        
        System.out.println("✓ Consulta creada:");
        consulta1.mostrarDetalle();
        
        // El paciente confirma la cita
        System.out.println("\n► Acción: Paciente CONFIRMA la cita");
        System.out.println("  Resultado: " + (consulta1.confirmar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta1.mostrarDetalle();
        
        // El paciente completa la cita (fue realizada)
        System.out.println("\n► Acción: Médico COMPLETA la cita (fue realizada)");
        System.out.println("  Resultado: " + (consulta1.completar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta1.mostrarDetalle();
        
        // Intentar cambiar de estado (debe fallar)
        System.out.println("\n► Intento: Cancelar una cita ya COMPLETADA");
        System.out.println("  Resultado: " + (consulta1.cancelar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
    }
    
    // ═════════════════════════════════════════════════════════════════════════════════
    // ESCENARIO 2: Cancelación después de confirmar
    // ═════════════════════════════════════════════════════════════════════════════════
    
    private static void escenario2_CancelacionPendiente() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 2: CANCELACIÓN DESPUÉS DE CONFIRMAR                        │");
        System.out.println("│ Pendiente → Confirmada → Cancelada                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        // Crear nueva consulta
        Consulta consulta2 = new Consulta(
            11,                          // id_paciente
            4,                           // id_medico
            LocalDate.of(2026, 4, 29),   // fecha
            LocalTime.of(15, 0),         // hora_inicio
            LocalTime.of(15, 45)         // hora_fin
        );
        
        System.out.println("✓ Consulta creada:");
        consulta2.mostrarDetalle();
        
        // El paciente confirma
        System.out.println("\n► Acción: Paciente CONFIRMA la cita");
        System.out.println("  Resultado: " + (consulta2.confirmar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta2.mostrarDetalle();
        
        // Pero luego cancela
        System.out.println("\n► Acción: Paciente CANCELA la cita confirmada");
        System.out.println("  Resultado: " + (consulta2.cancelar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta2.mostrarDetalle();
        
        // Intentar hacer algo más (debe fallar)
        System.out.println("\n► Intento: Confirmar nuevamente una cita CANCELADA");
        System.out.println("  Resultado: " + (consulta2.confirmar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
    }
    
    // ═════════════════════════════════════════════════════════════════════════════════
    // ESCENARIO 3: Paciente no asiste
    // ═════════════════════════════════════════════════════════════════════════════════
    
    private static void escenario3_Inasistencia() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 3: INASISTENCIA DEL PACIENTE                               │");
        System.out.println("│ Pendiente → Confirmada → No Asistió                                 │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        // Crear nueva consulta
        Consulta consulta3 = new Consulta(
            12,                          // id_paciente
            5,                           // id_medico
            LocalDate.of(2026, 4, 30),   // fecha
            LocalTime.of(16, 0),         // hora_inicio
            LocalTime.of(16, 30)         // hora_fin
        );
        
        System.out.println("✓ Consulta creada:");
        consulta3.mostrarDetalle();
        
        // El paciente confirma
        System.out.println("\n► Acción: Paciente CONFIRMA la cita");
        System.out.println("  Resultado: " + (consulta3.confirmar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta3.mostrarDetalle();
        
        // Pero no asiste (el médico lo marca)
        System.out.println("\n► Acción: Médico registra que el paciente NO ASISTIÓ");
        System.out.println("  Resultado: " + (consulta3.marcarNoAsistio() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta3.mostrarDetalle();
        
        // Intentar marcar como completada (debe fallar)
        System.out.println("\n► Intento: Marcar como COMPLETADA una cita donde el paciente NO ASISTIÓ");
        System.out.println("  Resultado: " + (consulta3.completar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
    }
    
    // ═════════════════════════════════════════════════════════════════════════════════
    // ESCENARIO 4: Transiciones Inválidas
    // ═════════════════════════════════════════════════════════════════════════════════
    
    private static void escenario4_TransicionesInvalidas() {
        System.out.println("\n┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ESCENARIO 4: INTENTOS DE TRANSICIONES INVÁLIDAS                      │");
        System.out.println("└─────────────────────────────────────────────────────────────────────────┘\n");
        
        // Crear nueva consulta
        Consulta consulta4 = new Consulta(
            13,                          // id_paciente
            6,                           // id_medico
            LocalDate.of(2026, 5, 1),    // fecha
            LocalTime.of(10, 0),         // hora_inicio
            LocalTime.of(10, 30)         // hora_fin
        );
        
        System.out.println("✓ Consulta creada (Estado: PENDIENTE):");
        consulta4.mostrarDetalle();
        
        // Intento 1: Completar sin confirmar
        System.out.println("\n► Intento 1: Completar directamente una cita PENDIENTE");
        System.out.println("  Resultado: " + (consulta4.completar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        System.out.println("  Estado actual: " + consulta4.getEstadoNombre());
        
        // Intento 2: Marcar como no asistió sin confirmar
        System.out.println("\n► Intento 2: Marcar como NO ASISTIÓ una cita PENDIENTE");
        System.out.println("  Resultado: " + (consulta4.marcarNoAsistio() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        System.out.println("  Estado actual: " + consulta4.getEstadoNombre());
        
        // Ahora confirmar
        System.out.println("\n► Acción: Confirmar la cita PENDIENTE");
        System.out.println("  Resultado: " + (consulta4.confirmar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta4.mostrarDetalle();
        
        // Completar
        System.out.println("\n► Acción: Completar la cita CONFIRMADA");
        System.out.println("  Resultado: " + (consulta4.completar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        consulta4.mostrarDetalle();
        
        // Intentos en estado COMPLETADA
        System.out.println("\n► Intento 3: Cancelar una cita COMPLETADA");
        System.out.println("  Resultado: " + (consulta4.cancelar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
        
        System.out.println("\n► Intento 4: Confirmar nuevamente una cita COMPLETADA");
        System.out.println("  Resultado: " + (consulta4.confirmar() ? "✓ EXITOSO" : "✗ RECHAZADO"));
    }
}
