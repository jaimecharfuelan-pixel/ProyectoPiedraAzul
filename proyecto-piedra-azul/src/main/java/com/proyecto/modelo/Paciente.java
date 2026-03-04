package com.proyecto.modelo;

public class Paciente extends Persona {
    private String motivoConsulta;
    private String historiaClinica;

    public Paciente() {
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(String historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    @Override
    public String getRol() {
        return "Paciente";
    }
}
