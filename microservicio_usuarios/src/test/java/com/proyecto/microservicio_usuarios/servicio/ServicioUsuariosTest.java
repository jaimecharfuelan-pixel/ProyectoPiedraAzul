package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioUsuariosTest {

    @Mock
    private RepositorioUsuario repoUsuario;

    @InjectMocks
    private ServicioUsuarios servicioUsuarios;

    @Test
    void autenticarUsuarioValidoDevuelveUsuario() {
        Usuario usuario = new Usuario(1, "usuario1", "clave123");
        when(repoUsuario.findByUsuario("usuario1")).thenReturn(Optional.of(usuario));

        Usuario resultado = servicioUsuarios.autenticar("usuario1", "clave123");

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdUsuario());
        assertEquals("usuario1", resultado.getUsuario());
    }

    @Test
    void autenticarUsuarioInvalidoDevuelveNull() {
        when(repoUsuario.findByUsuario("usuario1")).thenReturn(Optional.empty());

        Usuario resultado = servicioUsuarios.autenticar("usuario1", "contrasenaIncorrecta");

        assertNull(resultado);
    }

    @Test
    void registrarUsuarioValidoGuardaYRetornaTrue() {
        Usuario usuario = new Usuario(0, "nuevoUsuario", "pass123");

        boolean guardado = servicioUsuarios.registrarUsuario(usuario);

        assertTrue(guardado);
        verify(repoUsuario, times(1)).save(usuario);
    }

    @Test
    void editarUsuarioNoExistenteRetornaFalse() {
        Usuario usuario = new Usuario(99, "usuario99", "pass99");
        when(repoUsuario.existsById(99)).thenReturn(false);

        boolean resultado = servicioUsuarios.editarUsuario(usuario);

        assertFalse(resultado);
        verify(repoUsuario, never()).save(any());
    }

    @Test
    void listarUsuariosRetornaTodosLosUsuarios() {
        Usuario u1 = new Usuario(1, "u1", "p1");
        Usuario u2 = new Usuario(2, "u2", "p2");
        when(repoUsuario.findAll()).thenReturn(List.of(u1, u2));

        List<Usuario> todos = servicioUsuarios.listarUsuarios();

        assertEquals(2, todos.size());
        assertEquals("u1", todos.get(0).getUsuario());
        assertEquals("u2", todos.get(1).getUsuario());
    }
}
