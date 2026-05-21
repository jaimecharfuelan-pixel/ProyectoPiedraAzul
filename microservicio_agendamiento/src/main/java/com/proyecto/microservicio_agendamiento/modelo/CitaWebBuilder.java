package com.proyecto.microservicio_agendamiento.modelo;

/**
 * Builder concreto para citas agendadas por el paciente vía web (RF3).
 *
 * Reglas específicas:
 * - El estado inicial siempre es Pendiente (id=2).
 * - La hora fin se calcula automáticamente sumando 30 minutos a horaInicio
 *   si no fue especificada explícitamente.
 */
public class CitaWebBuilder extends CitaBuilder {

    private static final int ESTADO_PENDIENTE = 2;
    private static final int DURACION_MINUTOS = 30;

    @Override
    protected void aplicarReglas() {
        // Regla 1: estado siempre Pendiente para citas web
        this.idEstadoCita = ESTADO_PENDIENTE;

        // Regla 2: calcular horaFin si no fue asignada
        if (this.horaFin == null && this.horaInicio != null) {
            this.horaFin = this.horaInicio.plusMinutes(DURACION_MINUTOS);
        }
    }
}
