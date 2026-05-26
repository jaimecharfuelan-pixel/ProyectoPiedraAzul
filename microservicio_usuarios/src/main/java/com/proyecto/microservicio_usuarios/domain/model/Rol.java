package com.proyecto.microservicio_usuarios.domain.model;

public class Rol {

    private int idRol;
    private String nombre;
    private int idUsuario;

    public Rol() {}

    public Rol(int idRol, String nombre, int idUsuario) {
        this.idRol = idRol;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
    }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}
