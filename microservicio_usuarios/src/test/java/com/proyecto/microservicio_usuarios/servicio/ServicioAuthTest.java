package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.dto.LoginResponseDTO;
import com.proyecto.microservicio_usuarios.modelo.Rol;
import com.proyecto.microservicio_usuarios.modelo.SesionToken;
import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioRol;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioSesionToken;
import com.proyecto.microservicio_usuarios.servicio.strategy.TokenGenerationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioAuthTest {

    @Mock
    private ServicioUsuarios servicioUsuarios;

    @Mock
    private RepositorioSesionToken repoToken;

    @Mock
    private RepositorioRol repoRol;

    @Mock
    private TokenGenerationStrategy tokenGenerationStrategy;

    @InjectMocks
    private ServicioAuth servicioAuth;

    @Test
    void loginValidoGuardaSesionYDevuelveToken() {
        Usuario usuario = new Usuario(5, "usuario5", "clave5");
        when(servicioUsuarios.autenticar("usuario5", "clave5")).thenReturn(usuario);
        when(tokenGenerationStrategy.generarToken(usuario)).thenReturn("tokentest");
        when(repoRol.findFirstByIdUsuario(5)).thenReturn(Optional.of(new Rol(1, "ADMIN", 5)));

        LoginResponseDTO response = servicioAuth.login("usuario5", "clave5");

        assertNotNull(response);
        assertEquals("tokentest", response.getToken());
        assertEquals(5, response.getIdUsuario());
        assertEquals("ADMIN", response.getRol());
        verify(repoToken, times(1)).save(any(SesionToken.class));
    }

    @Test
    void loginInvalidoDevuelveNull() {
        when(servicioUsuarios.autenticar("usuarioX", "passX")).thenReturn(null);

        LoginResponseDTO response = servicioAuth.login("usuarioX", "passX");

        assertNull(response);
        verify(repoToken, never()).save(any());
    }

    @Test
    void validarTokenActivoRetornaTrue() {
        SesionToken token = new SesionToken();
        token.setTokenHash("valido");
        token.setIdEstado(2);
        token.setFechaExpiracion(java.time.LocalDateTime.now().plusMinutes(10));
        when(repoToken.findAll()).thenReturn(List.of(token));

        assertTrue(servicioAuth.validarToken("valido"));
    }

    @Test
    void cerrarSesionInvalidaRetornaFalse() {
        when(repoToken.findAll()).thenReturn(List.of());

        assertFalse(servicioAuth.cerrarSesion("noExiste"));
    }
}
