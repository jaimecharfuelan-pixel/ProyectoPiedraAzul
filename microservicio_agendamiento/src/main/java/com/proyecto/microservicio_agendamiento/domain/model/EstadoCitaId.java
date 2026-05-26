package com.proyecto.microservicio_agendamiento.domain.model;

/**
 * Constantes de IDs para los estados de cita.
 * Reemplaza la clase EstadoCita del paquete modelo original.
 */
public final class EstadoCitaId {

    private EstadoCitaId() {}

    public static final int CANCELADA  = 1;
    public static final int PENDIENTE  = 2;
    public static final int CONFIRMADA = 3;
    public static final int COMPLETADA = 4;
    public static final int NO_ASISTIO = 5;
}
