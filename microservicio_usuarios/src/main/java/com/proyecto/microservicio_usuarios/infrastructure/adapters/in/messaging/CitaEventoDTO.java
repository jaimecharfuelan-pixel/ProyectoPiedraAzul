package com.proyecto.microservicio_usuarios.infrastructure.adapters.in.messaging;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Contrato de mensaje recibido desde ms-agendamiento via RabbitMQ.
 * Debe coincidir con el CitaEventoDTO publicado por ms-agendamiento.
 */
public class CitaEventoDTO {

    private int idCita;
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;

    public CitaEventoDTO() {}

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
}
