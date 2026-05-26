package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "paciente")
@PrimaryKeyJoinColumn(name = "id_persona")
public class PacienteEntity extends PersonaEntity {

    public PacienteEntity() {
        super();
    }
}
