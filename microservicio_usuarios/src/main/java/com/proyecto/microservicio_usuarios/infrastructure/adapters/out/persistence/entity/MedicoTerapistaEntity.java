package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medico_terapista")
@PrimaryKeyJoinColumn(name = "id_persona")
public class MedicoTerapistaEntity extends PersonaEntity {

    @Column(name = "id_especialidad")
    private int idEspecialidad;

    public MedicoTerapistaEntity() {
        super();
    }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }
}
