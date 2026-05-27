package com.proyecto.microservicio_agendamiento.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Validaciones de dominio para prevenir solapamientos y conflictos de agendamiento.
 * 
 * Reglas implementadas:
 * 1. Intervalo de 30 minutos mínimo entre citas del mismo médico en el mismo día
 * 2. Un paciente solo puede tener UNA cita en estado Pendiente o Confirmada simultáneamente
 * 3. No se puede agendar una cita en slot ocupado del mismo médico
 */
public class ValidadorSolapamiento {

    private static final int DURACION_SLOT_MINUTOS = 30;

    /**
     * Valida si la nueva cita se solapa con citas existentes del mismo médico.
     * Retorna null si es válida, o un mensaje de error descriptivo.
     */
    public static String validarSolapamientoMedico(int idMedico, LocalDate fecha,
                                                   LocalTime horaInicio, LocalTime horaFin,
                                                   List<Cita> citasExistentes) {
        // Excluir citas canceladas
        List<Cita> citasActivas = citasExistentes.stream()
                .filter(c -> c.getIdMedico() == idMedico 
                         && c.getFecha() != null
                         && c.getFecha().equals(fecha)
                         && c.getIdEstadoCita() != EstadoCitaId.CANCELADA)
                .toList();

        for (Cita cita : citasActivas) {
            // Validar intervalo de 30 minutos
            if (hayTraslape(horaInicio, horaFin, cita.getHoraInicio(), cita.getHoraFin())) {
                return "❌ Solapamiento con cita existente: " + cita.getHoraInicio() 
                       + " - " + cita.getHoraFin() + " (Médico #" + idMedico + ")";
            }
        }
        return null; // Válida
    }

    /**
     * Valida que el paciente no tenga más de una cita en estado Pendiente o Confirmada.
     * Retorna null si es válida, o un mensaje de error descriptivo.
     */
    public static String validarUnaPendienteOConfirmada(int idPaciente, List<Cita> citasExistentes) {
        long citasPendientesOConfirmadas = citasExistentes.stream()
                .filter(c -> c.getIdPaciente() == idPaciente
                         && (c.getIdEstadoCita() == EstadoCitaId.PENDIENTE
                             || c.getIdEstadoCita() == EstadoCitaId.CONFIRMADA))
                .count();

        if (citasPendientesOConfirmadas > 0) {
            return "⚠️ El paciente ya tiene una cita pendiente o confirmada. " +
                   "No puede agendar otra hasta completarla o cancelarla.";
        }
        return null; // Válida
    }

    /**
     * Valida ambas reglas: solapamiento de médico + una cita por paciente.
     * Retorna lista de errores. Si está vacía, la cita es válida.
     */
    public static List<String> validarCompletamente(int idMedico, int idPaciente,
                                                     LocalDate fecha,
                                                     LocalTime horaInicio, LocalTime horaFin,
                                                     List<Cita> citasExistentes) {
        List<String> errores = new java.util.ArrayList<>();

        String errSolapamiento = validarSolapamientoMedico(idMedico, fecha, horaInicio, horaFin, citasExistentes);
        if (errSolapamiento != null) errores.add(errSolapamiento);

        String errPaciente = validarUnaPendienteOConfirmada(idPaciente, citasExistentes);
        if (errPaciente != null) errores.add(errPaciente);

        return errores;
    }

    /**
     * Verifica si dos intervalos de tiempo se traslapan.
     */
    private static boolean hayTraslape(LocalTime inicio1, LocalTime fin1,
                                        LocalTime inicio2, LocalTime fin2) {
        // Dos intervalos se traslapan si:
        // inicio1 < fin2 AND inicio2 < fin1
        return inicio1.isBefore(fin2) && inicio2.isBefore(fin1);
    }

    /**
     * Calcula la hora de fin recomendada (30 min después de inicio).
     */
    public static LocalTime calcularHoraFin(LocalTime horaInicio) {
        return horaInicio.plusMinutes(DURACION_SLOT_MINUTOS);
    }
}
