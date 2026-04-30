package com.proyecto.microservicio_agendamiento.modelo;

/**
 * Constantes para los estados de cita definidos en dominio_estado_cita.
 * 1=Cancelada, 2=Pendiente, 3=Confirmada, 4=Completada, 5=No Asistió
 */
public final class EstadoCita {

    private EstadoCita() {}

    public static final int CANCELADA   = 1;
    public static final int PENDIENTE   = 2;
    public static final int CONFIRMADA  = 3;
    public static final int COMPLETADA  = 4;
    public static final int NO_ASISTIO  = 5;
}
