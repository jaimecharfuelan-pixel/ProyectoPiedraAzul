package com.proyecto.microservicio_agendamiento.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Body para el endpoint PATCH /api/citas/{idCita}/reagendar
 */
public class ReagendarCitaDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nuevaFecha;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime nuevaHora;

    public ReagendarCitaDTO() {}

    public LocalDate getNuevaFecha() { return nuevaFecha; }
    public void setNuevaFecha(LocalDate nuevaFecha) { this.nuevaFecha = nuevaFecha; }

    public LocalTime getNuevaHora() { return nuevaHora; }
    public void setNuevaHora(LocalTime nuevaHora) { this.nuevaHora = nuevaHora; }
}
