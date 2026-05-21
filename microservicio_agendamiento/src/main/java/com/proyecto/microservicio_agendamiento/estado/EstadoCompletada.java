package com.proyecto.microservicio_agendamiento.estado;

/**
 * ESTADO: COMPLETADA
 * 
 * La consulta fue realizada exitosamente en la fecha/hora programada.
 * Este es un estado TERMINAL - no se puede cambiar de aquí.
 * 
 * Transiciones válidas:
 * - Confirmar: INVÁLIDO (la consulta ya fue realizada)
 * - Cancelar: INVÁLIDO (la consulta ya fue realizada)
 * - Completar: INVÁLIDO (ya está completada)
 * - No Asistió: INVÁLIDO (el paciente sí asistió)
 */
public class EstadoCompletada implements EstadoConsultaStrategy {
    
    @Override
    public boolean confirmar() {
        // NO - la consulta ya fue realizada
        System.out.println("✗ NO PERMITIDO: La consulta ya fue COMPLETADA");
        System.out.println("  → Una consulta completada es INMUTABLE");
        return false;
    }
    
    @Override
    public boolean cancelar() {
        // NO - no se puede cancelar una consulta ya realizada
        System.out.println("✗ NO PERMITIDO: No se puede cancelar una consulta COMPLETADA");
        System.out.println("  → La consulta ya fue realizada");
        return false;
    }
    
    @Override
    public boolean completar() {
        // Ya está completada
        System.out.println("✓ Consulta ya está COMPLETADA (sin cambios)");
        return true;
    }
    
    @Override
    public boolean marcarNoAsistio() {
        // NO - el paciente ya asistió (consulta realizada)
        System.out.println("✗ NO PERMITIDO: No se puede marcar como 'No Asistió' una consulta COMPLETADA");
        System.out.println("  → La consulta ya fue realizada");
        return false;
    }
    
    @Override
    public String obtenerNombre() {
        return "Completada";
    }
    
    @Override
    public int obtenerID() {
        return 4; // ID en BD para estado Completada
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Consulta realizada exitosamente (estado inmutable)";
    }
}
