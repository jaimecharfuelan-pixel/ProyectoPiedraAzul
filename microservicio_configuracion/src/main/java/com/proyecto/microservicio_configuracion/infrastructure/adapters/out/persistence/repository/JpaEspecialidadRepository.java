package com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.entity.EspecialidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEspecialidadRepository extends JpaRepository<EspecialidadEntity, Integer> {
}
