package com.proyecto.microservicio_usuarios.domain.ports.out;

import com.proyecto.microservicio_usuarios.domain.model.SesionToken;

import java.util.List;
import java.util.Optional;

public interface SesionTokenRepositoryPort {
    SesionToken save(SesionToken token);
    List<SesionToken> findAll();
    Optional<SesionToken> findByToken(String tokenHash);
}
