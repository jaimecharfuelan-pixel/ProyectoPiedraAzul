package com.proyecto.modelo;

public class Medico extends Persona {
    private String especialidad;
    private String nroTarjeta;

    public Medico() {
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    @Override
    public String getRol() {
        return "Medico";
    }
}
