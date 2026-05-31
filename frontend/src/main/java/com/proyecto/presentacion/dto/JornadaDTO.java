package com.proyecto.presentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalTime;

/**
 * DTO para jornadas laborales (turnos) desde ms-configuracion.
 * idUsuario enlaza con persona.id_usuario en ms-usuarios.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JornadaDTO {
    private int       idJornada;
    private String    diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int       idEstado;
    private int       idUsuario;
    private int       duracionEstimadaAtencion;

    public JornadaDTO() {}

    public int       getIdJornada()                  { return idJornada; }
    public void      setIdJornada(int v)             { this.idJornada = v; }
    public String    getDiaSemana()                  { return diaSemana; }
    public void      setDiaSemana(String v)          { this.diaSemana = v; }
    public LocalTime getHoraInicio()                 { return horaInicio; }
    public void      setHoraInicio(LocalTime v)      { this.horaInicio = v; }
    public LocalTime getHoraFin()                    { return horaFin; }
    public void      setHoraFin(LocalTime v)         { this.horaFin = v; }
    public int       getIdEstado()                   { return idEstado; }
    public void      setIdEstado(int v)              { this.idEstado = v; }
    public int       getIdUsuario()                  { return idUsuario; }
    public void      setIdUsuario(int v)             { this.idUsuario = v; }
    public int       getDuracionEstimadaAtencion()   { return duracionEstimadaAtencion; }
    public void      setDuracionEstimadaAtencion(int v) { this.duracionEstimadaAtencion = v; }
}
