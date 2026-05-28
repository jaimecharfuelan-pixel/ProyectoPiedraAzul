package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_usuarios.domain.model.Paciente;
import com.proyecto.microservicio_usuarios.domain.ports.out.PacienteRepositoryPort;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.PacienteEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository.JpaPacienteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PacienteRepositoryAdapter implements PacienteRepositoryPort {

    private final JpaPacienteRepository jpa;

    public PacienteRepositoryAdapter(JpaPacienteRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Paciente save(Paciente paciente) {
        return toDomain(jpa.save(toEntity(paciente)));
    }

    @Override
    public Optional<Paciente> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Paciente> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Paciente toDomain(PacienteEntity e) {
        Paciente p = new Paciente();
        if (e.getIdPersona() != null) p.setIdPersona(e.getIdPersona());
        p.setNombre(e.getNombre());
        p.setApellido(e.getApellido());
        p.setCedulaCiudadania(e.getCedulaCiudadania());
        p.setCelular(e.getCelular());
        p.setCorreo(e.getCorreo());
        p.setIdGenero(e.getIdGenero());
        p.setFechaNacimiento(e.getFechaNacimiento());
        p.setIdUsuario(e.getIdUsuario());
        p.setIdEstado(e.getIdEstado());
        p.setActivo(e.isActivo());
        return p;
    }

    private PacienteEntity toEntity(Paciente p) {
        PacienteEntity e = new PacienteEntity();
        // Solo setear idPersona si es > 0 (registro existente); null = nuevo registro para JPA
        if (p.getIdPersona() > 0) e.setIdPersona(p.getIdPersona());
        e.setNombre(p.getNombre());
        e.setApellido(p.getApellido());
        e.setCedulaCiudadania(p.getCedulaCiudadania());
        e.setCelular(p.getCelular());
        e.setCorreo(p.getCorreo());
        e.setIdGenero(p.getIdGenero());
        e.setFechaNacimiento(p.getFechaNacimiento());
        e.setIdUsuario(p.getIdUsuario());
        e.setIdEstado(p.getIdEstado());
        e.setActivo(p.isActivo());
        return e;
    }
}
