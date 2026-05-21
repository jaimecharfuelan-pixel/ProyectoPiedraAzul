package com.proyecto.microservicio_agendamiento.estado;

/**
 * FACTORY: Crea instancias de estados concretos.
 * 
 * Centraliza la creación de objetos estado, siguiendo el patrón Factory Method.
 * Permite obtener un estado por su ID o crear el estado inicial (PENDIENTE).
 */
public class FabricaEstados {
    
    /**
     * Crea un estado específico basado en su ID.
     * 
     * @param idEstado ID del estado (1-5)
     * @return Instancia del estado correspondiente
     * @throws IllegalArgumentException si el ID no es válido
     */
    public static EstadoConsultaStrategy crearEstado(int idEstado) {
        return switch (idEstado) {
            case 1 -> new EstadoCancelada();
            case 2 -> new EstadoPendiente();
            case 3 -> new EstadoConfirmada();
            case 4 -> new EstadoCompletada();
            case 5 -> new EstadoNoAsistio();
            default -> throw new IllegalArgumentException(
                "ID de estado inválido: " + idEstado + 
                ". Válidos: 1 (Cancelada), 2 (Pendiente), 3 (Confirmada), 4 (Completada), 5 (No Asistió)"
            );
        };
    }
    
    /**
     * Crea el estado inicial de una consulta recién agendada.
     * 
     * @return EstadoPendiente (estado inicial)
     */
    public static EstadoConsultaStrategy crearEstadoInicial() {
        return new EstadoPendiente();
    }
}
