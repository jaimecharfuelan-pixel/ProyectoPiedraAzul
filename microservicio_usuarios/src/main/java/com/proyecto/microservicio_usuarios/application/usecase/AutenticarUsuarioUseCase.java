package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.application.dto.LoginCommand;
import com.proyecto.microservicio_usuarios.application.dto.LoginResult;
import com.proyecto.microservicio_usuarios.domain.model.SesionToken;
import com.proyecto.microservicio_usuarios.domain.model.Usuario;
import com.proyecto.microservicio_usuarios.domain.ports.in.AutenticarUsuarioPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.RolRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.SesionTokenRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.TokenGeneratorPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AutenticarUsuarioUseCase implements AutenticarUsuarioPort {

    private final UsuarioRepositoryPort usuarioRepo;
    private final SesionTokenRepositoryPort tokenRepo;
    private final RolRepositoryPort rolRepo;
    private final TokenGeneratorPort tokenGenerator;

    public AutenticarUsuarioUseCase(UsuarioRepositoryPort usuarioRepo,
                                    SesionTokenRepositoryPort tokenRepo,
                                    RolRepositoryPort rolRepo,
                                    TokenGeneratorPort tokenGenerator) {
        this.usuarioRepo = usuarioRepo;
        this.tokenRepo = tokenRepo;
        this.rolRepo = rolRepo;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public LoginResult login(LoginCommand command) {
        Usuario usuario = usuarioRepo.findByNombreUsuario(command.getUsuario())
                .filter(u -> u.getContrasena().equals(command.getContrasena()))
                .orElse(null);

        if (usuario == null) return null;

        String token = tokenGenerator.generarToken(usuario.getIdUsuario(), usuario.getUsuario());

        SesionToken sesion = new SesionToken();
        sesion.setTokenHash(token);
        sesion.setFechaCreacion(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(1));
        sesion.setIdEstado(2); // activo
        sesion.setIdUsuario(usuario.getIdUsuario());
        tokenRepo.save(sesion);

        String rol = rolRepo.findFirstByIdUsuario(usuario.getIdUsuario())
                .map(r -> r.getNombre())
                .orElse("Sin rol");

        return new LoginResult(token, usuario.getIdUsuario(), usuario.getUsuario(), rol);
    }

    @Override
    public boolean validarToken(String token) {
        return tokenRepo.findAll().stream()
                .anyMatch(t -> t.getTokenHash().equals(token)
                        && t.getIdEstado() == 2
                        && t.getFechaExpiracion().isAfter(LocalDateTime.now()));
    }

    @Override
    public boolean cerrarSesion(String token) {
        return tokenRepo.findByToken(token)
                .map(t -> {
                    t.setIdEstado(1); // inactivo
                    tokenRepo.save(t);
                    return true;
                }).orElse(false);
    }
}
