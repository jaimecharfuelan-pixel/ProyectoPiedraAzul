package com.proyecto.microservicio_agendamiento.domain.model;

/**
 * Puerto del patrón State para el ciclo de vida de una cita.
 * Cada estado concreto define qué transiciones son válidas.
 */
public interface EstadoConsulta {
    boolean confirmar();
    boolean cancelar();
    boolean completar();
    boolean marcarNoAsistio();
    String obtenerNombre();
    int obtenerID();
}
