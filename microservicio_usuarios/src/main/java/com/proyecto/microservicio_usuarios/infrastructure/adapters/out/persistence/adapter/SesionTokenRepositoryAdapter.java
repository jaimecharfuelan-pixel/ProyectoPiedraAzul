package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_usuarios.domain.model.SesionToken;
import com.proyecto.microservicio_usuarios.domain.ports.out.SesionTokenRepositoryPort;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.SesionTokenEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository.JpaSesionTokenRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SesionTokenRepositoryAdapter implements SesionTokenRepositoryPort {

    private final JpaSesionTokenRepository jpa;

    public SesionTokenRepositoryAdapter(JpaSesionTokenRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public SesionToken save(SesionToken token) {
        return toDomain(jpa.save(toEntity(token)));
    }

    @Override
    public List<SesionToken> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<SesionToken> findByToken(String tokenHash) {
        return jpa.findByTokenHash(tokenHash).map(this::toDomain);
    }

    private SesionToken toDomain(SesionTokenEntity e) {
        SesionToken t = new SesionToken();
        t.setIdToken(e.getIdToken());
        t.setTokenHash(e.getTokenHash());
        t.setFechaCreacion(e.getFechaCreacion());
        t.setFechaExpiracion(e.getFechaExpiracion());
        t.setIdEstado(e.getIdEstado());
        t.setIdUsuario(e.getIdUsuario());
        return t;
    }

    private SesionTokenEntity toEntity(SesionToken t) {
        SesionTokenEntity e = new SesionTokenEntity();
        e.setIdToken(t.getIdToken());
        e.setTokenHash(t.getTokenHash());
        e.setFechaCreacion(t.getFechaCreacion());
        e.setFechaExpiracion(t.getFechaExpiracion());
        e.setIdEstado(t.getIdEstado());
        e.setIdUsuario(t.getIdUsuario());
        return e;
    }
}
