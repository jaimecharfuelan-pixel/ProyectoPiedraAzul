package com.proyecto.microservicio_configuracion.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "dominio_especialidad")
public class DominioEspecialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private int idEspecialidad;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    public DominioEspecialidad() {}

    public DominioEspecialidad(int idEspecialidad, String nombre) {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
    }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
