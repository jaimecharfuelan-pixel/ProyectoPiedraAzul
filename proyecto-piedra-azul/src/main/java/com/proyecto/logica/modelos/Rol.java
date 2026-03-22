package com.proyecto.logica.modelos;

public class Rol {
    private int attIdRol;
    private String attNombre;
    private int attIdUsuario;

    public Rol() {}

    public Rol(int prmIdRol, String prmNombre, int prmIdUsuario) {
        this.attIdRol = prmIdRol;
        this.attNombre = prmNombre;
        this.attIdUsuario = prmIdUsuario;
    }

    public int getIdRol() { return attIdRol; }
    public void setIdRol(int prmIdRol) { this.attIdRol = prmIdRol; }

    public String getNombre() { return attNombre; }
    public void setNombre(String prmNombre) { this.attNombre = prmNombre; }

    public int getIdUsuario() { return attIdUsuario; }
    public void setIdUsuario(int prmIdUsuario) { this.attIdUsuario = prmIdUsuario; }
}