package com.proyecto.presentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO mínimo para leer jornadas desde ms-configuracion.
 * Solo necesitamos el día de la semana para filtrar el calendario.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JornadaDTO {
    private String diaSemana;

    public JornadaDTO() {}

    public String getDiaSemana()             { return diaSemana; }
    public void   setDiaSemana(String v)     { this.diaSemana = v; }
}
