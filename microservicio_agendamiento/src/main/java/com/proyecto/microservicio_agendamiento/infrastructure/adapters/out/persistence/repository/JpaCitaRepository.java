package com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.repository;

import com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.entity.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JpaCitaRepository extends JpaRepository<CitaEntity, Integer> {

    List<CitaEntity> findByIdMedicoAndFechaAndIdEstadoCitaNot(int idMedico, LocalDate fecha, int idEstadoCita);

    List<CitaEntity> findByFechaAndIdEstadoCitaNot(LocalDate fecha, int idEstadoCita);

    List<CitaEntity> findByIdPaciente(int idPaciente);
}
