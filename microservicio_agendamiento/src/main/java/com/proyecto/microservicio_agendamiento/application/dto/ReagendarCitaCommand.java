package com.proyecto.microservicio_agendamiento.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Comando para reagendar una cita existente a nueva fecha/hora.
 */
public class ReagendarCitaCommand {

    private int idCita;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nuevaFecha;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime nuevaHora;

    public ReagendarCitaCommand() {}

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public LocalDate getNuevaFecha() { return nuevaFecha; }
    public void setNuevaFecha(LocalDate nuevaFecha) { this.nuevaFecha = nuevaFecha; }

    public LocalTime getNuevaHora() { return nuevaHora; }
    public void setNuevaHora(LocalTime nuevaHora) { this.nuevaHora = nuevaHora; }
}
