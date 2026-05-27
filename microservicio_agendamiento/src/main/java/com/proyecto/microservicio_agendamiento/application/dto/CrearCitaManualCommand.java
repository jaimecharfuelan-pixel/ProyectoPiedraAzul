package com.proyecto.microservicio_agendamiento.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Comando para que el agendador cree una cita manualmente.
 * Estado inicial resultante: Confirmada (ya coordinada con el médico).
 */
public class CrearCitaManualCommand {

    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public CrearCitaManualCommand() {}

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
