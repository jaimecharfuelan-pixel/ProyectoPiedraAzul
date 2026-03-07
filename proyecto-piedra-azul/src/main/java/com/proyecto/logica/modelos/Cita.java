package com.proyecto.logica.modelos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private LocalDate attFecha;
    private LocalTime attHora;
    private Medico attMedico;
    private Paciente attPaciente;

    public Cita(LocalDate prmFecha, LocalTime prmHora, Medico prmMedico, Paciente prmPaciente) {
        this.attFecha = prmFecha;
        this.attHora = prmHora;
        this.attMedico = prmMedico;
        this.attPaciente = prmPaciente;
    }

    public LocalDate getFecha() {
        return attFecha;
    }

    public void setFecha(LocalDate prmFecha) {
        this.attFecha = prmFecha;
    }

    public LocalTime getHora() {
        return attHora;
    }

    public void setHora(LocalTime prmHora) {
        this.attHora = prmHora;
    }

    public Medico getMedico() {
        return attMedico;
    }

    public void setMedico(Medico prmMedico) {
        this.attMedico = prmMedico;
    }

    public Paciente getPaciente() {
        return attPaciente;
    }

    public void setPaciente(Paciente prmPaciente) {
        this.attPaciente = prmPaciente;
    }
}
