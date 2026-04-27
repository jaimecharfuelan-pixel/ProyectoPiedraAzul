package com.proyecto.logica.servicios;

import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.JornadaLaboral;
import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.persistencia.interfaces.IRepositorioJornadaLaboral;
import com.proyecto.persistencia.interfaces.IRepositorioMedicoTerapista;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioAgendamientoTest {

    private IRepositorioCitas repoCitas;
    private IRepositorioJornadaLaboral repoJornada;
    private IRepositorioMedicoTerapista repoMedico;
    private ServicioAgendamiento servicio;

    @BeforeEach
    void setUp() {
        repoCitas  = Mockito.mock(IRepositorioCitas.class);
        repoJornada = Mockito.mock(IRepositorioJornadaLaboral.class);
        repoMedico  = Mockito.mock(IRepositorioMedicoTerapista.class);
        servicio = new ServicioAgendamiento(repoCitas, repoJornada, repoMedico);
    }

    @Test
    void consultarDisponibilidad_sinJornada_retornaListaVacia() {
        when(repoJornada.listar()).thenReturn(List.of());
        when(repoCitas.listar()).thenReturn(List.of());

        LocalDate lunes = LocalDate.of(2025, 4, 28);
        List<LocalTime> resultado = servicio.consultarDisponibilidad(1, lunes);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void consultarDisponibilidad_jornadaLibre_retornaFranjas() {
        JornadaLaboral jornada = new JornadaLaboral(1, "Lunes",
                LocalTime.of(8, 0), LocalTime.of(10, 0), 1, 1);
        when(repoJornada.listar()).thenReturn(List.of(jornada));
        when(repoCitas.listar()).thenReturn(List.of());

        LocalDate lunes = LocalDate.of(2025, 4, 28);
        List<LocalTime> resultado = servicio.consultarDisponibilidad(1, lunes);

        assertEquals(4, resultado.size());
        assertEquals(LocalTime.of(8, 0), resultado.get(0));
    }

    @Test
    void consultarDisponibilidad_horaOcupada_noAparece() {
        JornadaLaboral jornada = new JornadaLaboral(1, "Lunes",
                LocalTime.of(8, 0), LocalTime.of(9, 0), 1, 1);
        Cita citaExistente = new Cita(1, 10, 1,
                LocalDate.of(2025, 4, 28),
                LocalTime.of(8, 0), LocalTime.of(8, 30), 1);

        when(repoJornada.listar()).thenReturn(List.of(jornada));
        when(repoCitas.listar()).thenReturn(List.of(citaExistente));

        LocalDate lunes = LocalDate.of(2025, 4, 28);
        List<LocalTime> resultado = servicio.consultarDisponibilidad(1, lunes);

        assertFalse(resultado.contains(LocalTime.of(8, 0)));
        assertTrue(resultado.contains(LocalTime.of(8, 30)));
    }

    @Test
    void agendarCitaWeb_horaDisponible_retornaTrue() {
        JornadaLaboral jornada = new JornadaLaboral(1, "Lunes",
                LocalTime.of(8, 0), LocalTime.of(9, 0), 1, 1);
        when(repoJornada.listar()).thenReturn(List.of(jornada));
        when(repoCitas.listar()).thenReturn(List.of());
        when(repoCitas.guardar(any())).thenReturn(1);

        boolean resultado = servicio.agendarCitaWeb(5, 1,
                LocalDate.of(2025, 4, 28), LocalTime.of(8, 0));

        assertTrue(resultado);
    }

    @Test
    void agendarCitaWeb_horaNoDisponible_retornaFalse() {
        when(repoJornada.listar()).thenReturn(List.of());
        when(repoCitas.listar()).thenReturn(List.of());

        boolean resultado = servicio.agendarCitaWeb(5, 1,
                LocalDate.of(2025, 4, 28), LocalTime.of(8, 0));

        assertFalse(resultado);
    }

    @Test
    void crearCitaManual_guardadoExitoso_retornaTrue() {
        Cita cita = new Cita();
        when(repoCitas.guardar(cita)).thenReturn(1);

        assertTrue(servicio.crearCitaManual(cita));
    }

    @Test
    void cancelarCita_llamaInactivar() {
        when(repoCitas.inactivar(3)).thenReturn(true);

        assertTrue(servicio.cancelarCita(3));
        verify(repoCitas).inactivar(3);
    }

    @Test
    void listarCitas_filtraPorMedicoYFecha() {
        LocalDate hoy = LocalDate.now();
        Cita c1 = new Cita(1, 10, 1, hoy, LocalTime.of(8, 0), LocalTime.of(8, 30), 1);
        Cita c2 = new Cita(2, 11, 2, hoy, LocalTime.of(9, 0), LocalTime.of(9, 30), 1);
        when(repoCitas.listar()).thenReturn(List.of(c1, c2));

        List<Cita> resultado = servicio.listarCitas(1, hoy);

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getIdMedico());
    }
}
