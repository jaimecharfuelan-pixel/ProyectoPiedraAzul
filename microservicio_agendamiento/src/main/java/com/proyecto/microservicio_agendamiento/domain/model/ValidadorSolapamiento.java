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
    private static final int MAX_TURNOS_POR_DIA = 2;

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

    public static String validarHoraDuplicada(int idMedico, LocalDate fecha,
                                              LocalTime horaInicio,
                                              List<Cita> citasExistentes) {
        return citasExistentes.stream()
                .filter(c -> c.getIdMedico() == idMedico
                          && c.getFecha() != null
                          && c.getFecha().equals(fecha)
                          && c.getIdEstadoCita() != EstadoCitaId.CANCELADA
                          && c.getHoraInicio() != null
                          && c.getHoraInicio().equals(horaInicio))
                .findFirst()
                .map(c -> "La cita ya está agendada a esa hora: " + horaInicio)
                .orElse(null);
    }

    public static String validarLimiteTurnosDiarios(int idMedico, LocalDate fecha,
                                                    List<Cita> citasExistentes) {
        long turnosDelDia = citasExistentes.stream()
                .filter(c -> c.getIdMedico() == idMedico
                          && c.getFecha() != null
                          && c.getFecha().equals(fecha)
                          && c.getIdEstadoCita() != EstadoCitaId.CANCELADA)
                .count();
        if (turnosDelDia >= MAX_TURNOS_POR_DIA) {
            return "El médico ya tiene " + MAX_TURNOS_POR_DIA + " turnos agendados para el " + fecha + ".";
        }
        return null;
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

        String errHoraDuplicada = validarHoraDuplicada(idMedico, fecha, horaInicio, citasExistentes);
        if (errHoraDuplicada != null) errores.add(errHoraDuplicada);

        String errMaxTurnos = validarLimiteTurnosDiarios(idMedico, fecha, citasExistentes);
        if (errMaxTurnos != null) errores.add(errMaxTurnos);

        if (errHoraDuplicada == null) {
            String errSolapamiento = validarSolapamientoMedico(idMedico, fecha, horaInicio, horaFin, citasExistentes);
            if (errSolapamiento != null) errores.add(errSolapamiento);
        }

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
