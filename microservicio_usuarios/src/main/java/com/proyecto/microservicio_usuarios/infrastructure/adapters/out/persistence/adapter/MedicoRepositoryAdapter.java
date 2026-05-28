package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_usuarios.domain.model.MedicoTerapista;
import com.proyecto.microservicio_usuarios.domain.ports.out.MedicoRepositoryPort;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.MedicoTerapistaEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository.JpaMedicoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MedicoRepositoryAdapter implements MedicoRepositoryPort {

    private final JpaMedicoRepository jpa;

    public MedicoRepositoryAdapter(JpaMedicoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<MedicoTerapista> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<MedicoTerapista> findByEstado(int idEstado) {
        return jpa.findByIdEstado(idEstado).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public MedicoTerapista save(MedicoTerapista medico) {
        return toDomain(jpa.save(toEntity(medico)));
    }

    private MedicoTerapista toDomain(MedicoTerapistaEntity e) {
        MedicoTerapista m = new MedicoTerapista();
        if (e.getIdPersona() != null) m.setIdPersona(e.getIdPersona());
        m.setNombre(e.getNombre());
        m.setApellido(e.getApellido());
        m.setCedulaCiudadania(e.getCedulaCiudadania());
        m.setCelular(e.getCelular());
        m.setCorreo(e.getCorreo());
        m.setIdGenero(e.getIdGenero());
        m.setFechaNacimiento(e.getFechaNacimiento());
        m.setIdUsuario(e.getIdUsuario());
        m.setIdEstado(e.getIdEstado());
        m.setActivo(e.isActivo());
        m.setIdEspecialidad(e.getIdEspecialidad());
        return m;
    }

    private MedicoTerapistaEntity toEntity(MedicoTerapista m) {
        MedicoTerapistaEntity e = new MedicoTerapistaEntity();
        if (m.getIdPersona() > 0) e.setIdPersona(m.getIdPersona());
        e.setNombre(m.getNombre());
        e.setApellido(m.getApellido());
        e.setCedulaCiudadania(m.getCedulaCiudadania());
        e.setCelular(m.getCelular());
        e.setCorreo(m.getCorreo());
        e.setIdGenero(m.getIdGenero());
        e.setFechaNacimiento(m.getFechaNacimiento());
        e.setIdUsuario(m.getIdUsuario());
        e.setIdEstado(m.getIdEstado());
        e.setActivo(m.isActivo());
        e.setIdEspecialidad(m.getIdEspecialidad());
        return e;
    }
}
