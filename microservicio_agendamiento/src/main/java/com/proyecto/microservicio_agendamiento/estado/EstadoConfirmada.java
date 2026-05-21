package com.proyecto.microservicio_agendamiento.estado;

/**
 * ESTADO: CONFIRMADA
 * 
 * El paciente ha confirmado su asistencia a la consulta.
 * La consulta está lista para realizarse en la fecha/hora programada.
 * 
 * Transiciones válidas:
 * - Confirmar: Confirmada → Confirmada (sin cambio, idempotente)
 * - Cancelar: Confirmada → Cancelada ✓
 * - Completar: Confirmada → Completada ✓
 * - No Asistió: Confirmada → No Asistió ✓
 */
public class EstadoConfirmada implements EstadoConsultaStrategy {
    
    @Override
    public boolean confirmar() {
        // Ya está confirmada, pero no hay error - es idempotente
        System.out.println("✓ Consulta ya está CONFIRMADA (sin cambios)");
        return true;
    }
    
    @Override
    public boolean cancelar() {
        // Sí, se puede cancelar una consulta confirmada
        System.out.println("✓ Consulta CONFIRMADA → CANCELADA");
        return true;
    }
    
    @Override
    public boolean completar() {
        // Sí, se puede completar una consulta confirmada
        System.out.println("✓ Consulta CONFIRMADA → COMPLETADA (consulta realizada)");
        return true;
    }
    
    @Override
    public boolean marcarNoAsistio() {
        // Sí, el paciente pudo no asistir a pesar de confirmar
        System.out.println("✓ Consulta CONFIRMADA → NO ASISTIÓ");
        return true;
    }
    
    @Override
    public String obtenerNombre() {
        return "Confirmada";
    }
    
    @Override
    public int obtenerID() {
        return 3; // ID en BD para estado Confirmada
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Consulta confirmada por el paciente, lista para realizarse";
    }
}
