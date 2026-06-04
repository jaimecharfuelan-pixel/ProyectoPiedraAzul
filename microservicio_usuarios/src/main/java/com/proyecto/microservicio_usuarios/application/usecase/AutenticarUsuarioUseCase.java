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
import com.proyecto.microservicio_usuarios.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AutenticarUsuarioUseCase implements AutenticarUsuarioPort {

    private final UsuarioRepositoryPort usuarioRepo;
    private final SesionTokenRepositoryPort tokenRepo;
    private final RolRepositoryPort rolRepo;
    private final TokenGeneratorPort tokenGenerator;
    private final JwtTokenProvider jwtTokenProvider;

    public AutenticarUsuarioUseCase(UsuarioRepositoryPort usuarioRepo,
                                    SesionTokenRepositoryPort tokenRepo,
                                    RolRepositoryPort rolRepo,
                                    TokenGeneratorPort tokenGenerator,
                                    JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepo = usuarioRepo;
        this.tokenRepo = tokenRepo;
        this.rolRepo = rolRepo;
        this.tokenGenerator = tokenGenerator;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResult login(LoginCommand command) {
        Usuario usuario = usuarioRepo.findByNombreUsuario(command.getUsuario())
                .filter(u -> u.getContrasena().equals(command.getContrasena()))
                .orElse(null);

        if (usuario == null) return null;

        String rol = rolRepo.findFirstByIdUsuario(usuario.getIdUsuario())
                .map(r -> r.getNombre())
                .orElse("Sin rol");

        String token = tokenGenerator.generarToken(usuario.getIdUsuario(), usuario.getUsuario(), rol);

        SesionToken sesion = new SesionToken();
        sesion.setTokenHash(token);
        sesion.setFechaCreacion(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(1));
        sesion.setIdEstado(2); // activo
        sesion.setIdUsuario(usuario.getIdUsuario());
        tokenRepo.save(sesion);

        return new LoginResult(token, usuario.getIdUsuario(), usuario.getUsuario(), rol);
    }

    @Override
    public boolean validarToken(String token) {
        if (!jwtTokenProvider.validarToken(token)) {
            return false;
        }

        return tokenRepo.findByToken(token)
                .filter(t -> t.getIdEstado() == 2)
                .filter(t -> t.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .isPresent();
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
