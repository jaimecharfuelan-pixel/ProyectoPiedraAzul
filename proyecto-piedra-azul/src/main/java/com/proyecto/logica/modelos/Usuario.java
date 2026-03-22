package com.proyecto.logica.modelos;

public class Usuario {
    private int attIdUsuario;
    private String attUsuario;
    private String attContrasena;

    public Usuario() {}

    public Usuario(int prmIdUsuario, String prmUsuario, String prmContrasena) {
        this.attIdUsuario = prmIdUsuario;
        this.attUsuario = prmUsuario;
        this.attContrasena = prmContrasena;
    }

    // Getters y Setters
    public int getIdUsuario() { return attIdUsuario; }
    public void setIdUsuario(int prmIdUsuario) { this.attIdUsuario = prmIdUsuario; }

    public String getUsuario() { return attUsuario; }
    public void setUsuario(String prmUsuario) { this.attUsuario = prmUsuario; }

    public String getContrasena() { return attContrasena; }
    public void setContrasena(String prmContrasena) { this.attContrasena = prmContrasena; }
}