package com.proyecto.microservicio_usuarios.mensajeria;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO que llega desde ms-agendamiento via RabbitMQ.
 * Mismos campos que el CitaEventoDTO de ms-agendamiento — deben coincidir.
 */
public class CitaEventoDTO {

    private int idCita;
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;

    public CitaEventoDTO() {}

    public int getIdCita()            { return idCita; }
    public void setIdCita(int v)      { this.idCita = v; }

    public int getIdPaciente()        { return idPaciente; }
    public void setIdPaciente(int v)  { this.idPaciente = v; }

    public int getIdMedico()          { return idMedico; }
    public void setIdMedico(int v)    { this.idMedico = v; }

    public LocalDate getFecha()             { return fecha; }
    public void setFecha(LocalDate v)       { this.fecha = v; }

    public LocalTime getHoraInicio()        { return horaInicio; }
    public void setHoraInicio(LocalTime v)  { this.horaInicio = v; }
}
