package com.proyecto.microservicio_agendamiento.domain.ports.out;

import com.proyecto.microservicio_agendamiento.domain.model.Cita;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CitaRepositoryPort {
    Cita save(Cita cita);
    Optional<Cita> findById(int id);
    boolean existsById(int id);
    List<Cita> findAll();
    List<Cita> findByMedicoFechaExcluyendoEstado(int idMedico, LocalDate fecha, int idEstadoExcluido);
    List<Cita> findByMedicoExcluyendoEstado(int idMedico, int idEstadoExcluido);
    List<Cita> findByFechaExcluyendoEstado(LocalDate fecha, int idEstadoExcluido);
    List<Cita> findByPaciente(int idPaciente);
}
