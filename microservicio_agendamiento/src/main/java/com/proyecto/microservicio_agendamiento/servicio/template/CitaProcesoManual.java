package com.proyecto.microservicio_agendamiento.servicio.template;

import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.modelo.EstadoCita;

/**
 * SUBCLASE 2 — Flujo Manual (agendador registra la cita directamente)
 *
 * Analogía con ej1: como MasterCard — implementa las reglas específicas
 * del flujo manual: la cita queda directamente CONFIRMADA porque el
 * agendador ya coordinó con el médico y el paciente.
 */
public class CitaProcesoManual extends CitaProcesoTemplate {

    /**
     * Validación específica del flujo manual:
     * Se verifica que la cita tenga un estado de cita definido previamente
     * o que los IDs sean válidos. En flujo manual el agendador es responsable
     * de la coherencia de los datos.
     */
    @Override
    protected boolean validarDatosEspecificos(Cita cita) {
        if (cita.getHoraFin() != null &&
            cita.getHoraFin().isBefore(cita.getHoraInicio())) {
            System.out.println("  → [Manual] ❌ Hora de fin anterior a hora de inicio");
            return false;
        }
        System.out.println("  → [Manual] ✓ Datos del agendador verificados para paciente #"
                + cita.getIdPaciente() + " con médico #" + cita.getIdMedico());
        return true;
    }

    /**
     * Las citas creadas manualmente por el agendador inician en CONFIRMADA.
     * Ya fueron coordinadas directamente con el médico.
     */
    @Override
    protected void asignarEstadoInicial(Cita cita) {
        cita.setIdEstadoCita(EstadoCita.CONFIRMADA);
        System.out.println("  → [Manual] ✓ Estado asignado: CONFIRMADA (agendada por agendador)");
    }

    /**
     * Hook: después de guardar una cita manual se registra en el log
     * del agendador (extensión futura).
     */
    @Override
    protected void despuesDeGuardar(Cita cita) {
        System.out.println("  → [Manual] ✓ Cita #" + cita.getIdCita()
                + " registrada manualmente. Confirmación enviada al médico.");
    }
}
