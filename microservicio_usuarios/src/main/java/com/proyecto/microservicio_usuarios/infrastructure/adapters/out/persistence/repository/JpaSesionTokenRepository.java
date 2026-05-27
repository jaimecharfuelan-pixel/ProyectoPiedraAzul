package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.SesionTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaSesionTokenRepository extends JpaRepository<SesionTokenEntity, Integer> {
    Optional<SesionTokenEntity> findByTokenHash(String tokenHash);
}
