package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "agendador")
@PrimaryKeyJoinColumn(name = "id_persona")
public class AgendadorEntity extends PersonaEntity {

    public AgendadorEntity() {
        super();
    }
}
