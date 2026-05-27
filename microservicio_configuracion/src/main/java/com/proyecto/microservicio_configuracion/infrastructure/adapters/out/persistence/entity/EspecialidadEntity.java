package com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dominio_especialidad")
public class EspecialidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private int idEspecialidad;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    public EspecialidadEntity() {}

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
