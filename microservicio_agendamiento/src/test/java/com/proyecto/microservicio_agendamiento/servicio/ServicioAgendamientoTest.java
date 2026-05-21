package com.proyecto.microservicio_agendamiento.servicio;

import com.proyecto.microservicio_agendamiento.mensajeria.PublicadorCitas;
import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.repositorio.RepositorioCitas;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioAgendamientoTest {

    @Mock
    private RepositorioCitas repoCitas;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PublicadorCitas publicador;

    @InjectMocks
    private ServicioAgendamiento servicio;

    // ── listarCitas ───────────────────────────────────────────────────────────

    @Test
    void listarCitas_conFechaYMedico_retornaListaFiltrada() {
        Cita c1 = new Cita();
        c1.setIdMedico(3);
        c1.setFecha(LocalDate.of(2026, 5, 11));

        when(repoCitas.findByIdMedicoAndFechaAndIdEstadoCitaNot(3, LocalDate.of(2026, 5, 11), 1))
                .thenReturn(List.of(c1));

        List<Cita> resultado = servicio.listarCitas(3, LocalDate.of(2026, 5, 11));

        assertEquals(1, resultado.size());
        assertEquals(3, resultado.get(0).getIdMedico());
    }

    @Test
    void listarCitas_sinMedico_retornaTodasLasCitasDelDia() {
        Cita c1 = new Cita(); c1.setFecha(LocalDate.of(2026, 5, 11));
        Cita c2 = new Cita(); c2.setFecha(LocalDate.of(2026, 5, 11));

        when(repoCitas.findByFechaAndIdEstadoCitaNot(LocalDate.of(2026, 5, 11), 1))
                .thenReturn(List.of(c1, c2));

        List<Cita> resultado = servicio.listarCitas(null, LocalDate.of(2026, 5, 11));

        assertEquals(2, resultado.size());
    }

    // ── cancelarCita ──────────────────────────────────────────────────────────

    @Test
    void cancelarCita_existente_cambiaEstadoYRetornaTrue() {
        Cita cita = new Cita();
        cita.setIdCita(10);
        cita.setIdEstadoCita(2); // Pendiente

        when(repoCitas.findById(10)).thenReturn(Optional.of(cita));
        when(repoCitas.save(any(Cita.class))).thenReturn(cita);

        boolean resultado = servicio.cancelarCita(10);

        assertTrue(resultado);
        assertEquals(1, cita.getIdEstadoCita(), "El estado debe cambiar a Cancelada (1)");
        verify(publicador, times(1)).publicarCitaCancelada(10);
    }

    @Test
    void cancelarCita_noExistente_retornaFalse() {
        when(repoCitas.findById(99)).thenReturn(Optional.empty());

        boolean resultado = servicio.cancelarCita(99);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    // ── editarCita ────────────────────────────────────────────────────────────

    @Test
    void editarCita_existente_guardaYRetornaTrue() {
        Cita cita = new Cita();
        cita.setIdCita(5);

        when(repoCitas.existsById(5)).thenReturn(true);
        when(repoCitas.save(any(Cita.class))).thenReturn(cita);

        boolean resultado = servicio.editarCita(cita);

        assertTrue(resultado);
        verify(repoCitas, times(1)).save(cita);
    }

    @Test
    void editarCita_noExistente_retornaFalse() {
        Cita cita = new Cita();
        cita.setIdCita(99);

        when(repoCitas.existsById(99)).thenReturn(false);

        boolean resultado = servicio.editarCita(cita);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    // ── reagendarCita ─────────────────────────────────────────────────────────

    @Test
    void reagendarCita_existente_actualizaFechaYHora() {
        Cita cita = new Cita();
        cita.setIdCita(7);
        cita.setIdEstadoCita(2); // Pendiente

        when(repoCitas.findById(7)).thenReturn(Optional.of(cita));
        when(repoCitas.save(any())).thenReturn(cita);

        ServicioAgendamiento.ReagendamientoResultado resultado = servicio.reagendarCita(
                7, LocalDate.of(2026, 5, 15), LocalTime.of(10, 0));

        assertEquals(ServicioAgendamiento.ReagendamientoResultado.OK, resultado);
        assertEquals(LocalDate.of(2026, 5, 15), cita.getFecha());
        assertEquals(LocalTime.of(10, 0), cita.getHoraInicio());
    }

    @Test
    void reagendarCita_cancelada_retornaCitaCancelada() {
        Cita cita = new Cita();
        cita.setIdCita(8);
        cita.setIdEstadoCita(1); // Cancelada

        when(repoCitas.findById(8)).thenReturn(Optional.of(cita));

        ServicioAgendamiento.ReagendamientoResultado resultado = servicio.reagendarCita(
                8, LocalDate.of(2026, 5, 15), LocalTime.of(10, 0));

        assertEquals(ServicioAgendamiento.ReagendamientoResultado.CITA_CANCELADA, resultado);
        verify(repoCitas, never()).save(any());
    }

    @Test
    void reagendarCita_noExistente_retornaNoEncontrada() {
        when(repoCitas.findById(999)).thenReturn(Optional.empty());

        ServicioAgendamiento.ReagendamientoResultado resultado = servicio.reagendarCita(
                999, LocalDate.of(2026, 5, 15), LocalTime.of(10, 0));

        assertEquals(ServicioAgendamiento.ReagendamientoResultado.NO_ENCONTRADA, resultado);
    }
}
