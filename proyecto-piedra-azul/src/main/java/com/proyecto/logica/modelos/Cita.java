package com.proyecto.logica.modelos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private int attIdCita;
    private int attIdPaciente; // FK
    private int attIdMedico; // FK
    private LocalDate attFecha;
    private LocalTime attHoraInicio;
    private LocalTime attHoraFin;
    private Integer attIdEstadoCita; // FK

    // Constructor vacío
    public Cita() {}

    // Constructor completo
    public Cita(int prmIdCita, int prmIdPaciente, int prmIdMedico,
                LocalDate prmFecha, LocalTime prmHoraInicio, LocalTime prmHoraFin,
                Integer prmIdEstadoCita) {

        this.attIdCita = prmIdCita;
        this.attIdPaciente = prmIdPaciente;
        this.attIdMedico = prmIdMedico;
        this.attFecha = prmFecha;
        this.attHoraInicio = prmHoraInicio;
        this.attHoraFin = prmHoraFin;
        this.attIdEstadoCita = prmIdEstadoCita;
    }

    // Getters y Setters

    public int getIdCita() {
        return attIdCita;
    }

    public void setIdCita(int prmIdCita) {
        this.attIdCita = prmIdCita;
    }

    public int getIdPaciente() {
        return attIdPaciente;
    }

    public void setIdPaciente(int prmIdPaciente) {
        this.attIdPaciente = prmIdPaciente;
    }

    public int getIdMedico() {
        return attIdMedico;
    }

    public void setIdMedico(int prmIdMedico) {
        this.attIdMedico = prmIdMedico;
    }

    public LocalDate getFecha() {
        return attFecha;
    }

    public void setFecha(LocalDate prmFecha) {
        this.attFecha = prmFecha;
    }

    public LocalTime getHoraInicio() {
        return attHoraInicio;
    }

    public void setHoraInicio(LocalTime prmHoraInicio) {
        this.attHoraInicio = prmHoraInicio;
    }

    public LocalTime getHoraFin() {
        return attHoraFin;
    }

    public void setHoraFin(LocalTime prmHoraFin) {
        this.attHoraFin = prmHoraFin;
    }

    public Integer getIdEstadoCita() {
        return attIdEstadoCita;
    }

    public void setIdEstadoCita(Integer prmIdEstadoCita) {
        this.attIdEstadoCita = prmIdEstadoCita;
    }
}