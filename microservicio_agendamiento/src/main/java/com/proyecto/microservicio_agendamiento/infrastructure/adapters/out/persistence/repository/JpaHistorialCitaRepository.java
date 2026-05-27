package com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.entity.HistorialCitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaHistorialCitaRepository extends JpaRepository<HistorialCitaEntity, Integer> {
    List<HistorialCitaEntity> findByIdCita(int idCita);
}
