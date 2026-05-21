package com.proyecto.microservicio_agendamiento.servicio.template;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proyecto.microservicio_agendamiento.mensajeria.PublicadorCitas;
import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.modelo.EstadoCita;
import com.proyecto.microservicio_agendamiento.repositorio.RepositorioCitas;

/**
 * Tests del patrón Template Method para el procesamiento de citas.
 * Verifica que el algoritmo general (CitaProcesoTemplate) y las
 * subclases concretas (CitaProcesoWeb, CitaProcesoManual) funcionen correctamente.
 */
@ExtendWith(MockitoExtension.class)
class CitaProcesoTemplateTest {

    @Mock
    private RepositorioCitas repoCitas;

    @Mock
    private PublicadorCitas publicador;

    private CitaProcesoTemplate flujoWeb;
    private CitaProcesoTemplate flujoManual;

    @BeforeEach
    void setUp() {
        flujoWeb    = new CitaProcesoWeb();
        flujoManual = new CitaProcesoManual();
    }

    // ── Validaciones comunes (Paso 1 y 2 del template) ────────────────────────

    @Test
    void procesarCita_citaNula_retornaFalse() {
        boolean resultado = flujoWeb.procesarCita(null, repoCitas, publicador);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
        verify(publicador, never()).publicarCitaCreada(any());
    }

    @Test
    void procesarCita_sinPaciente_retornaFalse() {
        Cita cita = citaValida();
        cita.setIdPaciente(0); // inválido

        boolean resultado = flujoWeb.procesarCita(cita, repoCitas, publicador);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    @Test
    void procesarCita_sinMedico_retornaFalse() {
        Cita cita = citaValida();
        cita.setIdMedico(0); // inválido

        boolean resultado = flujoManual.procesarCita(cita, repoCitas, publicador);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    @Test
    void procesarCita_sinFecha_retornaFalse() {
        Cita cita = citaValida();
        cita.setFecha(null);

        boolean resultado = flujoWeb.procesarCita(cita, repoCitas, publicador);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    // ── CitaProcesoWeb ────────────────────────────────────────────────────────

    @Test
    void flujoWeb_citaValida_guardaYPublicaYRetornaTrue() {
        Cita cita = citaValida();
        Cita guardada = citaValida();
        guardada.setIdCita(1);

        when(repoCitas.save(any(Cita.class))).thenReturn(guardada);

        boolean resultado = flujoWeb.procesarCita(cita, repoCitas, publicador);

        assertTrue(resultado);
        verify(repoCitas, times(1)).save(cita);
        verify(publicador, times(1)).publicarCitaCreada(guardada);
    }

    @Test
    void flujoWeb_asignaEstadoPendiente() {
        Cita cita = citaValida();
        when(repoCitas.save(any(Cita.class))).thenReturn(cita);

        flujoWeb.procesarCita(cita, repoCitas, publicador);

        assertEquals(EstadoCita.PENDIENTE, cita.getIdEstadoCita(),
                "El flujo web debe asignar estado PENDIENTE");
    }

    @Test
    void flujoWeb_horaFinAnteriorAInicio_retornaFalse() {
        Cita cita = citaValida();
        cita.setHoraInicio(LocalTime.of(10, 0));
        cita.setHoraFin(LocalTime.of(9, 0)); // fin antes que inicio

        boolean resultado = flujoWeb.procesarCita(cita, repoCitas, publicador);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    @Test
    void flujoWeb_horaFinIgualAInicio_retornaFalse() {
        Cita cita = citaValida();
        cita.setHoraInicio(LocalTime.of(10, 0));
        cita.setHoraFin(LocalTime.of(10, 0)); // igual

        boolean resultado = flujoWeb.procesarCita(cita, repoCitas, publicador);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    // ── CitaProcesoManual ─────────────────────────────────────────────────────

    @Test
    void flujoManual_citaValida_guardaYPublicaYRetornaTrue() {
        Cita cita = citaValida();
        Cita guardada = citaValida();
        guardada.setIdCita(2);

        when(repoCitas.save(any(Cita.class))).thenReturn(guardada);

        boolean resultado = flujoManual.procesarCita(cita, repoCitas, publicador);

        assertTrue(resultado);
        verify(repoCitas, times(1)).save(cita);
        verify(publicador, times(1)).publicarCitaCreada(guardada);
    }

    @Test
    void flujoManual_asignaEstadoConfirmada() {
        Cita cita = citaValida();
        when(repoCitas.save(any(Cita.class))).thenReturn(cita);

        flujoManual.procesarCita(cita, repoCitas, publicador);

        assertEquals(EstadoCita.CONFIRMADA, cita.getIdEstadoCita(),
                "El flujo manual debe asignar estado CONFIRMADA");
    }

    @Test
    void flujoManual_horaFinAnteriorAInicio_retornaFalse() {
        Cita cita = citaValida();
        cita.setHoraInicio(LocalTime.of(14, 0));
        cita.setHoraFin(LocalTime.of(13, 0)); // fin antes que inicio

        boolean resultado = flujoManual.procesarCita(cita, repoCitas, publicador);

        assertFalse(resultado);
        verify(repoCitas, never()).save(any());
    }

    // ── Diferencia entre flujos ───────────────────────────────────────────────

    @Test
    void flujoWeb_yFlujoManual_asignanEstadosDiferentes() {
        Cita citaWeb    = citaValida();
        Cita citaManual = citaValida();

        when(repoCitas.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        flujoWeb.procesarCita(citaWeb, repoCitas, publicador);
        flujoManual.procesarCita(citaManual, repoCitas, publicador);

        assertNotEquals(citaWeb.getIdEstadoCita(), citaManual.getIdEstadoCita(),
                "Web y Manual deben asignar estados distintos");
        assertEquals(EstadoCita.PENDIENTE,   citaWeb.getIdEstadoCita());
        assertEquals(EstadoCita.CONFIRMADA,  citaManual.getIdEstadoCita());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Cita citaValida() {
        Cita cita = new Cita();
        cita.setIdPaciente(1);
        cita.setIdMedico(2);
        cita.setFecha(LocalDate.now().plusDays(1));
        cita.setHoraInicio(LocalTime.of(9, 0));
        cita.setHoraFin(LocalTime.of(9, 30));
        return cita;
    }
}
