package com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_configuracion.domain.model.Especialidad;
import com.proyecto.microservicio_configuracion.domain.ports.out.EspecialidadRepositoryPort;
import com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.entity.EspecialidadEntity;
import com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.repository.JpaEspecialidadRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EspecialidadRepositoryAdapter implements EspecialidadRepositoryPort {

    private final JpaEspecialidadRepository jpa;

    public EspecialidadRepositoryAdapter(JpaEspecialidadRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Especialidad> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Especialidad> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    private Especialidad toDomain(EspecialidadEntity e) {
        return new Especialidad(e.getIdEspecialidad(), e.getNombre());
    }
}
