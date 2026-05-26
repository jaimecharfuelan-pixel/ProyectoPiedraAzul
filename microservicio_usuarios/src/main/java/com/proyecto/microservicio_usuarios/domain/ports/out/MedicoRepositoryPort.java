package com.proyecto.microservicio_usuarios.domain.ports.out;

import com.proyecto.microservicio_usuarios.domain.model.MedicoTerapista;

import java.util.List;
import java.util.Optional;

public interface MedicoRepositoryPort {
    Optional<MedicoTerapista> findById(int id);
    List<MedicoTerapista> findByEstado(int idEstado);
    MedicoTerapista save(MedicoTerapista medico);
}
