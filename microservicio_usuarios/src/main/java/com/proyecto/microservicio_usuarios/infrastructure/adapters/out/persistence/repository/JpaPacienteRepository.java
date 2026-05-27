package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPacienteRepository extends JpaRepository<PacienteEntity, Integer> {
}
