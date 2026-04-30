package com.proyecto.presentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CitaDTO {
    private int       idCita;
    private int       idPaciente;
    private int       idMedico;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer   idEstadoCita;

    public CitaDTO() {}

    public int       getIdCita()                  { return idCita; }
    public void      setIdCita(int v)             { this.idCita = v; }
    public int       getIdPaciente()              { return idPaciente; }
    public void      setIdPaciente(int v)         { this.idPaciente = v; }
    public int       getIdMedico()                { return idMedico; }
    public void      setIdMedico(int v)           { this.idMedico = v; }
    public LocalDate getFecha()                   { return fecha; }
    public void      setFecha(LocalDate v)        { this.fecha = v; }
    public LocalTime getHoraInicio()              { return horaInicio; }
    public void      setHoraInicio(LocalTime v)   { this.horaInicio = v; }
    public LocalTime getHoraFin()                 { return horaFin; }
    public void      setHoraFin(LocalTime v)      { this.horaFin = v; }
    public Integer   getIdEstadoCita()            { return idEstadoCita; }
    public void      setIdEstadoCita(Integer v)   { this.idEstadoCita = v; }
}
