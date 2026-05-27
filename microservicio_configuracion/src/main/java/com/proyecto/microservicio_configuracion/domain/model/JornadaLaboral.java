package com.proyecto.microservicio_configuracion.domain.model;

import java.time.LocalTime;

/**
 * Modelo de dominio de una jornada laboral de un médico. POJO puro, sin JPA.
 */
public class JornadaLaboral {

    private int idJornada;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int idEstado;
    private int idUsuario;
    private int duracionEstimadaAtencion;

    public JornadaLaboral() {}

    public int getIdJornada() { return idJornada; }
    public void setIdJornada(int idJornada) { this.idJornada = idJornada; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public int getIdEstado() { return idEstado; }
    public void setIdEstado(int idEstado) { this.idEstado = idEstado; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getDuracionEstimadaAtencion() { return duracionEstimadaAtencion; }
    public void setDuracionEstimadaAtencion(int duracionEstimadaAtencion) {
        this.duracionEstimadaAtencion = duracionEstimadaAtencion;
    }
}
