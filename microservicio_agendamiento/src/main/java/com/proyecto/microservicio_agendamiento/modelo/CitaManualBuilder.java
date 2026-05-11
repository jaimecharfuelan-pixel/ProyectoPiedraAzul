package com.proyecto.microservicio_agendamiento.modelo;

/**
 * Builder concreto para citas creadas manualmente por el agendador (RF2).
 *
 * Reglas específicas:
 * - El estado inicial es Confirmada (id=3), porque el agendador
 *   ya habló con el paciente y la cita queda confirmada de inmediato.
 * - horaFin debe ser asignada explícitamente por el agendador.
 *   Si no se asigna, se lanza excepción para forzar el dato.
 */
public class CitaManualBuilder extends CitaBuilder {

    private static final int ESTADO_CONFIRMADA = 3;

    @Override
    protected void aplicarReglas() {
        // Regla 1: estado siempre Confirmada para citas manuales
        this.idEstadoCita = ESTADO_CONFIRMADA;

        // Regla 2: horaFin obligatoria en citas manuales
        if (this.horaFin == null) {
            throw new IllegalStateException(
                "CitaManualBuilder: horaFin es obligatoria para citas manuales.");
        }
    }
}
