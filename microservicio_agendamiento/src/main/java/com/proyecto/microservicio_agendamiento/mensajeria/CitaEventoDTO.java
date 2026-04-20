package com.proyecto.microservicio_agendamiento.mensajeria;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO que viaja por RabbitMQ cuando se crea o cancela una cita.
 * Solo contiene los campos que ms-usuarios necesita para procesar el evento.
 */
public class CitaEventoDTO {

    private int idCita;
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;

    public CitaEventoDTO() {}

    public CitaEventoDTO(int idCita, int idPaciente, int idMedico,
                         LocalDate fecha, LocalTime horaInicio) {
        this.idCita      = idCita;
        this.idPaciente  = idPaciente;
        this.idMedico    = idMedico;
        this.fecha       = fecha;
        this.horaInicio  = horaInicio;
    }

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
