package com.proyecto.logica.modelos;

import java.time.LocalDate;

public abstract class Persona {

    private int attIdPersona;
    private String attNombre;
    private String attCedulaCiudadania;
    private String attApellido;
    private String attCelular;
    private Integer attIdGenero; // FK
    private LocalDate attFechaNacimiento;
    private String attCorreo;
    private Integer attIdUsuario; // nullable
    private Integer attIdEstado; // FK

    // Constructor vacío
    public Persona() {}

    // Constructor completo
    public Persona(int prmIdPersona, String prmNombre, String prmCedulaCiudadania, String prmApellido,
                   String prmCelular, Integer prmIdGenero, LocalDate prmFechaNacimiento,
                   String prmCorreo, Integer prmIdUsuario, Integer prmIdEstado) {

        this.attIdPersona = prmIdPersona;
        this.attNombre = prmNombre;
        this.attCedulaCiudadania = prmCedulaCiudadania;
        this.attApellido = prmApellido;
        this.attCelular = prmCelular;
        this.attIdGenero = prmIdGenero;
        this.attFechaNacimiento = prmFechaNacimiento;
        this.attCorreo = prmCorreo;
        this.attIdUsuario = prmIdUsuario;
        this.attIdEstado = prmIdEstado;
    }

    // Getters y Setters

    public int getIdPersona() {
        return attIdPersona;
    }

    public void setIdPersona(int prmIdPersona) {
        this.attIdPersona = prmIdPersona;
    }

    public String getNombre() {
        return attNombre;
    }

    public void setNombre(String prmNombre) {
        this.attNombre = prmNombre;
    }

    public String getCedulaCiudadania() {
        return attCedulaCiudadania;
    }

    public void setCedulaCiudadania(String prmCedulaCiudadania) {
        this.attCedulaCiudadania = prmCedulaCiudadania;
    }

    public String getApellido() {
        return attApellido;
    }

    public void setApellido(String prmApellido) {
        this.attApellido = prmApellido;
    }

    public String getCelular() {
        return attCelular;
    }

    public void setCelular(String prmCelular) {
        this.attCelular = prmCelular;
    }

    public Integer getIdGenero() {
        return attIdGenero;
    }

    public void setIdGenero(Integer prmIdGenero) {
        this.attIdGenero = prmIdGenero;
    }

    public LocalDate getFechaNacimiento() {
        return attFechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate prmFechaNacimiento) {
        this.attFechaNacimiento = prmFechaNacimiento;
    }

    public String getCorreo() {
        return attCorreo;
    }

    public void setCorreo(String prmCorreo) {
        this.attCorreo = prmCorreo;
    }

    public Integer getIdUsuario() {
        return attIdUsuario;
    }

    public void setIdUsuario(Integer prmIdUsuario) {
        this.attIdUsuario = prmIdUsuario;
    }

    public Integer getIdEstado() {
        return attIdEstado;
    }

    public void setIdEstado(Integer prmIdEstado) {
        this.attIdEstado = prmIdEstado;
    }

}