package com.proyecto.microservicio_agendamiento.estado;

/**
 * ESTADO: NO ASISTIÓ
 * 
 * El paciente confirmó la cita pero no asistió en la fecha/hora programada.
 * Este es un estado TERMINAL - registra un hecho consumado.
 * 
 * Transiciones válidas:
 * - Confirmar: INVÁLIDO (el paciente no asistió)
 * - Cancelar: INVÁLIDO (no se puede cancelar lo ya sucedido)
 * - Completar: INVÁLIDO (la consulta no se realizó)
 * - No Asistió: INVÁLIDO (ya está en este estado)
 */
public class EstadoNoAsistio implements EstadoConsultaStrategy {
    
    @Override
    public boolean confirmar() {
        // NO - el paciente no asistió, no puede confirmarse
        System.out.println("✗ NO PERMITIDO: No se puede confirmar una consulta donde el paciente NO ASISTIÓ");
        System.out.println("  → El paciente no se presentó a la cita");
        return false;
    }
    
    @Override
    public boolean cancelar() {
        // NO - no se puede cancelar un hecho consumado
        System.out.println("✗ NO PERMITIDO: No se puede cancelar una consulta donde el paciente NO ASISTIÓ");
        System.out.println("  → El evento ya ocurrió (inasistencia)");
        return false;
    }
    
    @Override
    public boolean completar() {
        // NO - la consulta no se realizó
        System.out.println("✗ NO PERMITIDO: No se puede completar una consulta donde el paciente NO ASISTIÓ");
        System.out.println("  → La consulta no se llevó a cabo");
        return false;
    }
    
    @Override
    public boolean marcarNoAsistio() {
        // Ya está en este estado
        System.out.println("✓ Consulta ya está en estado NO ASISTIÓ (sin cambios)");
        return true;
    }
    
    @Override
    public String obtenerNombre() {
        return "No Asistió";
    }
    
    @Override
    public int obtenerID() {
        return 5; // ID en BD para estado No Asistió
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Paciente no asistió a la consulta programada (estado inmutable)";
    }
}
