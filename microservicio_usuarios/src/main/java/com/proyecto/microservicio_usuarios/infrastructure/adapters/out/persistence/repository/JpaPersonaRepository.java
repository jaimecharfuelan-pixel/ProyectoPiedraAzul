package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaPersonaRepository extends JpaRepository<PersonaEntity, Integer> {
    Optional<PersonaEntity> findByCedulaCiudadania(String cedulaCiudadania);
    List<PersonaEntity> findByActivoTrue();
}
