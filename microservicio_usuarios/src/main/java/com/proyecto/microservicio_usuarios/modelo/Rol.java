package com.proyecto.microservicio_usuarios.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private int idRol;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "id_usuario", nullable = false)
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
