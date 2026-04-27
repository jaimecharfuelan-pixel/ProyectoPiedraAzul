package com.proyecto.logica.servicios;

import com.proyecto.logica.modelos.Usuario;
import com.proyecto.persistencia.interfaces.IRepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioUsuariosTest {

    private IRepositorioUsuario repoUsuario;
    private ServicioUsuarios servicio;

    @BeforeEach
    void setUp() {
        repoUsuario = Mockito.mock(IRepositorioUsuario.class);
        servicio = new ServicioUsuarios(repoUsuario);
    }

    @Test
    void autenticar_credencialesCorrectas_retornaUsuario() {
        Usuario usuario = new Usuario(1, "admin", "1234");
        when(repoUsuario.buscarPorNombreUsuario("admin")).thenReturn(usuario);

        Usuario resultado = servicio.autenticar("admin", "1234");

        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsuario());
    }

    @Test
    void autenticar_contrasenaIncorrecta_retornaNull() {
        Usuario usuario = new Usuario(1, "admin", "1234");
        when(repoUsuario.buscarPorNombreUsuario("admin")).thenReturn(usuario);

        Usuario resultado = servicio.autenticar("admin", "wrongpass");

        assertNull(resultado);
    }

    @Test
    void autenticar_usuarioNoExiste_retornaNull() {
        when(repoUsuario.buscarPorNombreUsuario("noexiste")).thenReturn(null);

        Usuario resultado = servicio.autenticar("noexiste", "1234");

        assertNull(resultado);
    }

    @Test
    void registrarUsuario_nombreVacio_retornaFalse() {
        Usuario usuario = new Usuario(0, "", "pass");

        boolean resultado = servicio.registrarUsuario(usuario);

        assertFalse(resultado);
        verify(repoUsuario, never()).guardar(any());
    }

    @Test
    void registrarUsuario_datosValidos_retornaTrue() {
        Usuario usuario = new Usuario(0, "nuevo", "pass");
        when(repoUsuario.guardar(usuario)).thenReturn(5);

        boolean resultado = servicio.registrarUsuario(usuario);

        assertTrue(resultado);
    }

    @Test
    void editarUsuario_llamaRepositorio() {
        Usuario usuario = new Usuario(1, "admin", "pass");
        when(repoUsuario.actualizar(usuario)).thenReturn(true);

        boolean resultado = servicio.editarUsuario(usuario);

        assertTrue(resultado);
        verify(repoUsuario).actualizar(usuario);
    }

    @Test
    void eliminarUsuario_llamaInactivar() {
        when(repoUsuario.inactivar(1)).thenReturn(true);

        boolean resultado = servicio.eliminarUsuario(1);

        assertTrue(resultado);
        verify(repoUsuario).inactivar(1);
    }

    @Test
    void listarUsuarios_retornaLista() {
        when(repoUsuario.listar()).thenReturn(List.of(new Usuario(1, "a", "b")));

        List<Usuario> lista = servicio.listarUsuarios();

        assertEquals(1, lista.size());
    }
}
