package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.MedicoTerapistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaMedicoRepository extends JpaRepository<MedicoTerapistaEntity, Integer> {
    List<MedicoTerapistaEntity> findByIdEstado(int idEstado);
}
