package com.proyecto.microservicio_agendamiento.estado;

/**
 * Interfaz que define el contrato para todos los estados de una consulta.
 * 
 * Esta es la INTERFAZ DEL PATRÓN STATE.
 * Cada estado concreto implementa el comportamiento específico.
 * 
 * Propósito: Encapsular el comportamiento que varía según el estado,
 * permitiendo que la clase Consulta delegue el comportamiento al estado actual.
 */
public interface EstadoConsultaStrategy {
    
    /**
     * Confirma una consulta en su estado actual.
     * 
     * @return true si la confirmación fue válida, false en caso contrario
     */
    boolean confirmar();
    
    /**
     * Cancela una consulta en su estado actual.
     * 
     * @return true si la cancelación fue válida, false en caso contrario
     */
    boolean cancelar();
    
    /**
     * Completa una consulta (marca como realizada) en su estado actual.
     * 
     * @return true si la finalización fue válida, false en caso contrario
     */
    boolean completar();
    
    /**
     * Marca una consulta como "No Asistió" en su estado actual.
     * 
     * @return true si esta operación fue válida, false en caso contrario
     */
    boolean marcarNoAsistio();
    
    /**
     * Obtiene el nombre del estado actual.
     * 
     * @return nombre del estado (ej: "Pendiente", "Confirmada")
     */
    String obtenerNombre();
    
    /**
     * Obtiene el ID del estado para persistencia en BD.
     * 
     * @return ID del estado (1-5)
     */
    int obtenerID();
    
    /**
     * Obtiene la descripción del estado actual.
     * 
     * @return descripción legible del estado
     */
    String obtenerDescripcion();
}
