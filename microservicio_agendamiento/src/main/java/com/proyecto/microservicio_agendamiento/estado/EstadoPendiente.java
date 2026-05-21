package com.proyecto.microservicio_agendamiento.estado;

/**
 * ESTADO: PENDIENTE
 * 
 * Una consulta acaba de ser agendada y está esperando confirmación.
 * 
 * Transiciones válidas:
 * - Confirmar: Pendiente → Confirmada ✓
 * - Cancelar: Pendiente → Cancelada ✓
 * - Completar: Pendiente → Completada (INVÁLIDO - debe confirmarse primero)
 * - No Asistió: Pendiente → No Asistió (INVÁLIDO)
 */
public class EstadoPendiente implements EstadoConsultaStrategy {
    
    @Override
    public boolean confirmar() {
        // Sí, es válido confirmar una consulta pendiente
        System.out.println("✓ Consulta PENDIENTE → CONFIRMADA");
        return true;
    }
    
    @Override
    public boolean cancelar() {
        // Sí, es válido cancelar una consulta pendiente
        System.out.println("✓ Consulta PENDIENTE → CANCELADA");
        return true;
    }
    
    @Override
    public boolean completar() {
        // NO es válido completar sin confirmar primero
        System.out.println("✗ NO PERMITIDO: No se puede completar una consulta PENDIENTE");
        System.out.println("  → La consulta debe ser CONFIRMADA primero");
        return false;
    }
    
    @Override
    public boolean marcarNoAsistio() {
        // NO es válido marcar como "no asistió" siendo pendiente
        System.out.println("✗ NO PERMITIDO: No se puede marcar como 'No Asistió' una consulta PENDIENTE");
        System.out.println("  → La consulta debe estar CONFIRMADA");
        return false;
    }
    
    @Override
    public String obtenerNombre() {
        return "Pendiente";
    }
    
    @Override
    public int obtenerID() {
        return 2; // ID en BD para estado Pendiente
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Consulta agendada en espera de confirmación del paciente";
    }
}
