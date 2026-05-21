package com.proyecto.microservicio_agendamiento.servicio.template;

import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.modelo.EstadoCita;

/**
 * SUBCLASE 1 — Flujo Web (paciente agenda desde la aplicación)
 *
 * Analogía con ej1: como VisaCard — implementa las reglas específicas
 * del flujo web: la cita queda en estado PENDIENTE esperando confirmación,
 * y se valida que la hora de fin sea posterior a la de inicio.
 */
public class CitaProcesoWeb extends CitaProcesoTemplate {

    /**
     * Validación específica del flujo web:
     * La hora de fin debe ser posterior a la hora de inicio.
     * (La disponibilidad ya fue verificada antes de llegar aquí.)
     */
    @Override
    protected boolean validarDatosEspecificos(Cita cita) {
        if (cita.getHoraFin().isBefore(cita.getHoraInicio()) ||
            cita.getHoraFin().equals(cita.getHoraInicio())) {
            System.out.println("  → [Web] ❌ La hora de fin debe ser posterior a la hora de inicio");
            return false;
        }
        System.out.println("  → [Web] ✓ Rango horario válido: "
                + cita.getHoraInicio() + " - " + cita.getHoraFin());
        return true;
    }

    /**
     * Las citas agendadas por web inician en estado PENDIENTE.
     * El médico o agendador debe confirmarlas posteriormente.
     */
    @Override
    protected void asignarEstadoInicial(Cita cita) {
        cita.setIdEstadoCita(EstadoCita.PENDIENTE);
        System.out.println("  → [Web] ✓ Estado asignado: PENDIENTE (requiere confirmación)");
    }

    /**
     * Hook: después de guardar una cita web se podría enviar
     * una notificación al paciente (extensión futura).
     */
    @Override
    protected void despuesDeGuardar(Cita cita) {
        System.out.println("  → [Web] ✓ Cita #" + cita.getIdCita()
                + " guardada. Notificación pendiente de envío al paciente.");
    }
}
