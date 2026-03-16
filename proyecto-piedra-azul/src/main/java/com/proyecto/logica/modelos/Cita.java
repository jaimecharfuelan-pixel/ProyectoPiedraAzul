package com.proyecto.logica.modelos;

import java.time.LocalDateTime;

public class Cita {

    private int id;
    private Paciente paciente;
    private Medico medico;
    private LocalDateTime fechaHora;
    private String estado;

    public Cita(int prmId, Paciente prmPaciente, Medico prmMedico, LocalDateTime prmFechaHora, String prmEstado) {
        this.id = prmId;
        this.paciente = prmPaciente;
        this.medico = prmMedico;
        this.fechaHora = prmFechaHora;
        this.estado = prmEstado;
    }

    public int getId() {
        return id;
    }

    public void setId(int prmId) {
        this.id = prmId;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente prmPaciente) {
        this.paciente = prmPaciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico prmMedico) {
        this.medico = prmMedico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime prmFechaHora) {
        this.fechaHora = prmFechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String prmEstado) {
        this.estado = prmEstado;
    }
}