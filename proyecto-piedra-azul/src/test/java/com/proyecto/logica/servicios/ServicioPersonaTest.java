package com.proyecto.logica.servicios;

import com.proyecto.logica.modelos.DominioEspecialidad;
import com.proyecto.logica.modelos.MedicoTerapista;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Persona;
import com.proyecto.persistencia.interfaces.IRepositorioDominioEspecialidad;
import com.proyecto.persistencia.interfaces.IRepositorioMedicoTerapista;
import com.proyecto.persistencia.interfaces.IRepositorioPersona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioPersonaTest {

    private IRepositorioPersona repoPersona;
    private IRepositorioMedicoTerapista repoMedico;
    private IRepositorioDominioEspecialidad repoEspecialidad;
    private ServicioPersona servicio;

    @BeforeEach
    void setUp() {
        repoPersona      = Mockito.mock(IRepositorioPersona.class);
        repoMedico       = Mockito.mock(IRepositorioMedicoTerapista.class);
        repoEspecialidad = Mockito.mock(IRepositorioDominioEspecialidad.class);
        servicio = new ServicioPersona(repoPersona, repoMedico, repoEspecialidad);
    }

    @Test
    void crearPersona_datosCompletos_retornaId() {
        Persona p = new Paciente();
        p.setCedulaCiudadania("123456");
        p.setNombre("Juan");
        p.setApellido("Perez");
        when(repoPersona.guardar(p)).thenReturn(7);

        int id = servicio.crearPersona(p);

        assertEquals(7, id);
    }

    @Test
    void crearPersona_cedulaVacia_retornaMenosUno() {
        Persona p = new Paciente();
        p.setCedulaCiudadania("");
        p.setNombre("Juan");
        p.setApellido("Perez");

        int id = servicio.crearPersona(p);

        assertEquals(-1, id);
        verify(repoPersona, never()).guardar(any());
    }

    @Test
    void crearPersona_nombreNulo_retornaMenosUno() {
        Persona p = new Paciente();
        p.setCedulaCiudadania("123456");
        p.setNombre(null);
        p.setApellido("Perez");

        int id = servicio.crearPersona(p);

        assertEquals(-1, id);
    }

    @Test
    void editarPersona_llamaRepositorio() {
        Persona p = new Paciente();
        when(repoPersona.actualizar(p)).thenReturn(true);

        assertTrue(servicio.editarPersona(p));
        verify(repoPersona).actualizar(p);
    }

    @Test
    void inactivarPersona_llamaInactivar() {
        when(repoPersona.inactivar(3)).thenReturn(true);

        assertTrue(servicio.inactivarPersona(3));
        verify(repoPersona).inactivar(3);
    }

    @Test
    void buscarPorDocumento_retornaPersona() {
        Persona p = new Paciente();
        p.setCedulaCiudadania("999");
        when(repoPersona.buscarPorDocumento("999")).thenReturn(p);

        Persona resultado = servicio.buscarPorDocumento("999");

        assertNotNull(resultado);
        assertEquals("999", resultado.getCedulaCiudadania());
    }

    @Test
    void listarMedicosActivos_retornaLista() {
        MedicoTerapista medico = new MedicoTerapista(1, "Ana", "111", "Lopez",
                "3001", 2, LocalDate.of(1985, 3, 10), "ana@mail.com", 1, 1, 2);
        when(repoMedico.listarActivos()).thenReturn(List.of(medico));

        List<MedicoTerapista> lista = servicio.listarMedicosActivos();

        assertEquals(1, lista.size());
    }

    @Test
    void asignarEspecialidad_actualizaMedico() {
        MedicoTerapista medico = new MedicoTerapista(1, "Ana", "111", "Lopez",
                "3001", 2, LocalDate.of(1985, 3, 10), "ana@mail.com", 1, 1, 0);
        when(repoMedico.actualizar(medico)).thenReturn(true);

        boolean resultado = servicio.asignarEspecialidad(medico, 3);

        assertTrue(resultado);
        assertEquals(3, medico.getIdEspecialidad());
    }

    @Test
    void listarEspecialidades_retornaLista() {
        DominioEspecialidad esp = new DominioEspecialidad();
        when(repoEspecialidad.listar()).thenReturn(List.of(esp));

        List<DominioEspecialidad> lista = servicio.listarEspecialidades();

        assertEquals(1, lista.size());
    }
}
