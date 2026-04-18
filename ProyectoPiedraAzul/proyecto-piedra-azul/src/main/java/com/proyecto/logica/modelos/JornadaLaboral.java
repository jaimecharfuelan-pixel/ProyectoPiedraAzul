package com.proyecto.logica.modelos;

import java.time.LocalTime;

public class JornadaLaboral {

    private int attIdJornada;
    private String attDiaSemana;
    private LocalTime attHoraInicio;
    private LocalTime attHoraFin;
    private int attIdEstado;
    private int attIdUsuario;

    // Constructor vacío
    public JornadaLaboral() {}

    // Constructor completo
    public JornadaLaboral(int prmIdJornada, String prmDiaSemana, LocalTime prmHoraInicio,
                          LocalTime prmHoraFin, int prmIdEstado, int prmIdUsuario) {
        this.attIdJornada = prmIdJornada;
        this.attDiaSemana = prmDiaSemana;
        this.attHoraInicio = prmHoraInicio;
        this.attHoraFin = prmHoraFin;
        this.attIdEstado = prmIdEstado;
        this.attIdUsuario = prmIdUsuario;
    }

    // Getters y Setters
    public int getIdJornada() { return attIdJornada; }
    public void setIdJornada(int prmIdJornada) { this.attIdJornada = prmIdJornada; }

    public String getDiaSemana() { return attDiaSemana; }
    public void setDiaSemana(String prmDiaSemana) { this.attDiaSemana = prmDiaSemana; }

    public LocalTime getHoraInicio() { return attHoraInicio; }
    public void setHoraInicio(LocalTime prmHoraInicio) { this.attHoraInicio = prmHoraInicio; }

    public LocalTime getHoraFin() { return attHoraFin; }
    public void setHoraFin(LocalTime prmHoraFin) { this.attHoraFin = prmHoraFin; }

    public int getIdEstado() { return attIdEstado; }
    public void setIdEstado(int prmIdEstado) { this.attIdEstado = prmIdEstado; }

    public int getIdUsuario() { return attIdUsuario; }
    public void setIdUsuario(int prmIdUsuario) { this.attIdUsuario = prmIdUsuario; }
}