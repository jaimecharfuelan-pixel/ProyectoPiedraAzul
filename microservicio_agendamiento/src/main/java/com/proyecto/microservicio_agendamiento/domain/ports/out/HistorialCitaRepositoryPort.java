package com.proyecto.microservicio_agendamiento.domain.ports.out;

import com.proyecto.microservicio_agendamiento.domain.model.HistorialCita;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistir el historial de cambios de citas.
 */
public interface HistorialCitaRepositoryPort {
    HistorialCita save(HistorialCita historial);
    Optional<HistorialCita> findById(int id);
    List<HistorialCita> findByCitaId(int idCita);
    List<HistorialCita> findAll();
}
