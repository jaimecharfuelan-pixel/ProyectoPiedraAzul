package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_usuarios.domain.model.Usuario;
import com.proyecto.microservicio_usuarios.domain.ports.out.UsuarioRepositoryPort;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository.JpaUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final JpaUsuarioRepository jpa;

    public UsuarioRepositoryAdapter(JpaUsuarioRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return jpa.findByUsuario(nombreUsuario).map(this::toDomain);
    }

    @Override
    public boolean existsByNombreUsuario(String nombreUsuario) {
        return jpa.existsByUsuario(nombreUsuario);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return toDomain(jpa.save(toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(int id) {
        return jpa.existsById(id);
    }

    @Override
    public void deleteById(int id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Usuario toDomain(UsuarioEntity e) {
        return new Usuario(e.getIdUsuario(), e.getUsuario(), e.getContrasena());
    }

    private UsuarioEntity toEntity(Usuario u) {
        UsuarioEntity e = new UsuarioEntity();
        e.setIdUsuario(u.getIdUsuario());
        e.setUsuario(u.getUsuario());
        e.setContrasena(u.getContrasena());
        return e;
    }
}
