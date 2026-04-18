package com.proyecto.microservicio_agendamiento.dto;

import java.time.LocalTime;

/**
 * DTO ligero recibido desde ms-configuracion via HTTP.
 * Solo contiene los campos necesarios para calcular disponibilidad.
 */
public class JornadaResumenDTO {

    private int idMedico;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public JornadaResumenDTO() {}

    public JornadaResumenDTO(int idMedico, String diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        this.idMedico = idMedico;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
