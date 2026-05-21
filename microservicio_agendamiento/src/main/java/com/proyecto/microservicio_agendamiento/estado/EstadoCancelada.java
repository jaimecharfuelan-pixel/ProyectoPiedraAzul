package com.proyecto.microservicio_agendamiento.estado;

/**
 * ESTADO: CANCELADA
 * 
 * La consulta fue cancelada por el paciente o el sistema.
 * Este es un estado TERMINAL - la consulta no volverá a cambiar.
 * 
 * Transiciones válidas:
 * - Confirmar: INVÁLIDO (la consulta está cancelada)
 * - Cancelar: INVÁLIDO (ya está cancelada)
 * - Completar: INVÁLIDO (la consulta fue cancelada)
 * - No Asistió: INVÁLIDO (la consulta fue cancelada)
 */
public class EstadoCancelada implements EstadoConsultaStrategy {
    
    @Override
    public boolean confirmar() {
        // NO - no se puede confirmar una consulta cancelada
        System.out.println("✗ NO PERMITIDO: No se puede confirmar una consulta CANCELADA");
        System.out.println("  → La consulta fue cancelada y ya no es válida");
        return false;
    }
    
    @Override
    public boolean cancelar() {
        // Ya está cancelada
        System.out.println("✓ Consulta ya está CANCELADA (sin cambios)");
        return true;
    }
    
    @Override
    public boolean completar() {
        // NO - la consulta fue cancelada
        System.out.println("✗ NO PERMITIDO: No se puede completar una consulta CANCELADA");
        System.out.println("  → La consulta fue cancelada");
        return false;
    }
    
    @Override
    public boolean marcarNoAsistio() {
        // NO - la consulta ya está cancelada
        System.out.println("✗ NO PERMITIDO: No se puede marcar como 'No Asistió' una consulta CANCELADA");
        System.out.println("  → La consulta ya fue cancelada");
        return false;
    }
    
    @Override
    public String obtenerNombre() {
        return "Cancelada";
    }
    
    @Override
    public int obtenerID() {
        return 1; // ID en BD para estado Cancelada
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Consulta cancelada (estado inmutable)";
    }
}
