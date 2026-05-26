package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_usuarios.domain.model.Rol;
import com.proyecto.microservicio_usuarios.domain.ports.out.RolRepositoryPort;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.RolEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository.JpaRolRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final JpaRolRepository jpa;

    public RolRepositoryAdapter(JpaRolRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Rol save(Rol rol) {
        return toDomain(jpa.save(toEntity(rol)));
    }

    @Override
    public List<Rol> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Rol> findByIdUsuario(int idUsuario) {
        return jpa.findByIdUsuario(idUsuario).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Rol> findFirstByIdUsuario(int idUsuario) {
        return jpa.findFirstByIdUsuario(idUsuario).map(this::toDomain);
    }

    @Override
    public boolean existsById(int id) {
        return jpa.existsById(id);
    }

    @Override
    public void deleteById(int id) {
        jpa.deleteById(id);
    }

    private Rol toDomain(RolEntity e) {
        return new Rol(e.getIdRol(), e.getNombre(), e.getIdUsuario());
    }

    private RolEntity toEntity(Rol r) {
        RolEntity e = new RolEntity();
        e.setIdRol(r.getIdRol());
        e.setNombre(r.getNombre());
        e.setIdUsuario(r.getIdUsuario());
        return e;
    }
}
