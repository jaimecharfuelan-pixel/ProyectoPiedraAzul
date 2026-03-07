package com.proyecto.logica.modelos;

import java.time.LocalTime;

public class ConfiguracionAgenda {
    private String attIdMedico;
    private int attVentanaSemanas;
    private String attDiasAtencion;
    private LocalTime attHoraInicio;
    private LocalTime attHoraFin;
    private int attIntervaloMinutos;

    public ConfiguracionAgenda(String prmIdMedico, int prmVentanaSemanas, String prmDiasAtencion,
            LocalTime prmHoraInicio, LocalTime prmHoraFin, int prmIntervaloMinutos) {
        this.attIdMedico = prmIdMedico;
        this.attVentanaSemanas = prmVentanaSemanas;
        this.attDiasAtencion = prmDiasAtencion;
        this.attHoraInicio = prmHoraInicio;
        this.attHoraFin = prmHoraFin;
        this.attIntervaloMinutos = prmIntervaloMinutos;
    }

    public String getIdMedico() {
        return attIdMedico;
    }

    public void setIdMedico(String prmIdMedico) {
        this.attIdMedico = prmIdMedico;
    }

    public int getVentanaSemanas() {
        return attVentanaSemanas;
    }

    public void setVentanaSemanas(int prmVentanaSemanas) {
        this.attVentanaSemanas = prmVentanaSemanas;
    }

    public String getDiasAtencion() {
        return attDiasAtencion;
    }

    public void setDiasAtencion(String prmDiasAtencion) {
        this.attDiasAtencion = prmDiasAtencion;
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

    public int getIntervaloMinutos() {
        return attIntervaloMinutos;
    }

    public void setIntervaloMinutos(int prmIntervaloMinutos) {
        this.attIntervaloMinutos = prmIntervaloMinutos;
    }
}
