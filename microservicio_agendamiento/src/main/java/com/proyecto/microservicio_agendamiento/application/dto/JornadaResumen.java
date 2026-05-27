package com.proyecto.microservicio_agendamiento.application.dto;

import java.time.LocalTime;

/**
 * DTO de aplicación que representa una jornada laboral de un médico.
 * Recibido desde ms-configuracion via el adapter HTTP.
 */
public class JornadaResumen {

    private Integer idMedico;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public JornadaResumen() {}

    public Integer getIdMedico() { return idMedico; }
    public void setIdMedico(Integer idMedico) { this.idMedico = idMedico; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
