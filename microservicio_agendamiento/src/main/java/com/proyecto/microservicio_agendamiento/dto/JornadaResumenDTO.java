package com.proyecto.microservicio_agendamiento.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalTime;

/**
 * DTO ligero recibido desde ms-configuracion via HTTP.
 * Solo contiene los campos necesarios para calcular disponibilidad.
 * El campo idMedico llega como "idUsuario" en la respuesta de JornadaLaboral.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JornadaResumenDTO {

    private Integer idUsuario;   // nombre real en la respuesta JSON
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public JornadaResumenDTO() {}

    public Integer getIdMedico()  { return idUsuario; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public void setIdMedico(Integer v)          { this.idUsuario = v; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
