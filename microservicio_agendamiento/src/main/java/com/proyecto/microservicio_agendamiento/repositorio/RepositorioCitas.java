package com.proyecto.microservicio_agendamiento.repositorio;

import com.proyecto.microservicio_agendamiento.modelo.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepositorioCitas extends JpaRepository<Cita, Integer> {

    List<Cita> findByIdMedicoAndFecha(int idMedico, LocalDate fecha);

    List<Cita> findByIdPaciente(int idPaciente);

    List<Cita> findByFecha(LocalDate fecha);
}
