package com.proyecto.logica.modelos;

import java.time.LocalDate;

public abstract class Persona {
    private String attDocumento;
    private String attNombre;
    private String attApellido;
    private String attCelular;
    private String attGenero;
    private LocalDate attFechaNacimiento;
    private String attEmail;

    public Persona(String prmDocumento, String prmNombre, String prmApellido, String prmCelular, String prmGenero,
            LocalDate prmFechaNacimiento, String prmEmail) {
        this.attDocumento = prmDocumento;
        this.attNombre = prmNombre;
        this.attApellido = prmApellido;
        this.attCelular = prmCelular;
        this.attGenero = prmGenero;
        this.attFechaNacimiento = prmFechaNacimiento;
        this.attEmail = prmEmail;
    }

    public String getDocumento() {
        return attDocumento;
    }

    public void setDocumento(String prmDocumento) {
        this.attDocumento = prmDocumento;
    }

    public String getNombre() {
        return attNombre;
    }

    public void setNombre(String prmNombre) {
        this.attNombre = prmNombre;
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

    public String getGenero() {
        return attGenero;
    }

    public void setGenero(String prmGenero) {
        this.attGenero = prmGenero;
    }

    public LocalDate getFechaNacimiento() {
        return attFechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate prmFechaNacimiento) {
        this.attFechaNacimiento = prmFechaNacimiento;
    }

    public String getEmail() {
        return attEmail;
    }

    public void setEmail(String prmEmail) {
        this.attEmail = prmEmail;
    }
}
