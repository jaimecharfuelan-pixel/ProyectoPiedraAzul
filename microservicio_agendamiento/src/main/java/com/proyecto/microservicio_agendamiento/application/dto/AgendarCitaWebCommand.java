package com.proyecto.microservicio_agendamiento.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Comando para que un paciente agende su cita desde la web.
 * Estado inicial resultante: Pendiente.
 */
public class AgendarCitaWebCommand {

    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime hora;

    public AgendarCitaWebCommand() {}

    public AgendarCitaWebCommand(int idPaciente, int idMedico, LocalDate fecha, LocalTime hora) {
        this.idPaciente = idPaciente;
        this.idMedico   = idMedico;
        this.fecha      = fecha;
        this.hora       = hora;
    }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
}
