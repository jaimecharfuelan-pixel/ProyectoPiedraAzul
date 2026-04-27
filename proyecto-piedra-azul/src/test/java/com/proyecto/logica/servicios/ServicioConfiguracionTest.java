package com.proyecto.logica.servicios;

import com.proyecto.logica.modelos.JornadaLaboral;
import com.proyecto.persistencia.interfaces.IRepositorioJornadaLaboral;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioConfiguracionTest {

    private IRepositorioJornadaLaboral repoJornada;
    private ServicioConfiguracion servicio;

    @BeforeEach
    void setUp() {
        repoJornada = Mockito.mock(IRepositorioJornadaLaboral.class);
        servicio = new ServicioConfiguracion(repoJornada);
    }

    @Test
    void configurarDisponibilidad_horaInicioAntesFin_guardaJornada() {
        JornadaLaboral jornada = new JornadaLaboral(0, "Lunes",
                LocalTime.of(8, 0), LocalTime.of(17, 0), 1, 1);
        when(repoJornada.guardar(jornada)).thenReturn(true);

        boolean resultado = servicio.configurarDisponibilidadMedico(jornada);

        assertTrue(resultado);
        verify(repoJornada).guardar(jornada);
    }

    @Test
    void configurarDisponibilidad_horaInicioDepuesFin_retornaFalse() {
        JornadaLaboral jornada = new JornadaLaboral(0, "Lunes",
                LocalTime.of(18, 0), LocalTime.of(8, 0), 1, 1);

        boolean resultado = servicio.configurarDisponibilidadMedico(jornada);

        assertFalse(resultado);
        verify(repoJornada, never()).guardar(any());
    }

    @Test
    void configurarDisponibilidad_idExistente_actualizaJornada() {
        JornadaLaboral jornada = new JornadaLaboral(5, "Martes",
                LocalTime.of(9, 0), LocalTime.of(13, 0), 1, 2);
        when(repoJornada.actualizar(jornada)).thenReturn(true);

        boolean resultado = servicio.configurarDisponibilidadMedico(jornada);

        assertTrue(resultado);
        verify(repoJornada).actualizar(jornada);
    }

    @Test
    void obtenerTodasLasJornadas_retornaLista() {
        JornadaLaboral j = new JornadaLaboral(1, "Lunes",
                LocalTime.of(8, 0), LocalTime.of(12, 0), 1, 1);
        when(repoJornada.listar()).thenReturn(List.of(j));

        List<JornadaLaboral> lista = servicio.obtenerTodasLasJornadas();

        assertEquals(1, lista.size());
    }

    @Test
    void eliminarTurno_llamaInactivar() {
        when(repoJornada.inactivar(2)).thenReturn(true);

        boolean resultado = servicio.eliminarTurno(2);

        assertTrue(resultado);
        verify(repoJornada).inactivar(2);
    }
}
