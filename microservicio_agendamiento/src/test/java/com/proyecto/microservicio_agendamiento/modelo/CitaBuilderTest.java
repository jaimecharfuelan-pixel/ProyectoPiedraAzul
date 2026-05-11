package com.proyecto.microservicio_agendamiento.modelo;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del patrón Builder para la creación de citas.
 *
 * Se verifican:
 * - CitaWebBuilder:    estado Pendiente, cálculo automático de horaFin.
 * - CitaManualBuilder: estado Confirmada, horaFin obligatoria.
 * - CitaBuilder base:  construcción con todos los campos explícitos.
 */
class CitaBuilderTest {

    // ── CitaWebBuilder ────────────────────────────────────────────────────────

    @Test
    void citaWeb_debeCrearseConEstadoPendiente() {
        Cita cita = new CitaWebBuilder()
                .idPaciente(10)
                .idMedico(3)
                .fecha(LocalDate.of(2026, 5, 11))
                .horaInicio(LocalTime.of(9, 0))
                .build();

        assertEquals(2, cita.getIdEstadoCita(),
                "El estado de una cita web debe ser Pendiente (id=2)");
    }

    @Test
    void citaWeb_debeCalcularHoraFinAutomaticamente() {
        LocalTime inicio = LocalTime.of(9, 0);

        Cita cita = new CitaWebBuilder()
                .idPaciente(10)
                .idMedico(3)
                .fecha(LocalDate.of(2026, 5, 11))
                .horaInicio(inicio)
                .build();

        assertEquals(LocalTime.of(9, 30), cita.getHoraFin(),
                "La horaFin debe calcularse sumando 30 minutos a horaInicio");
    }

    @Test
    void citaWeb_debeAsignarTodosLosCampos() {
        LocalDate fecha  = LocalDate.of(2026, 5, 12);
        LocalTime inicio = LocalTime.of(8, 0);

        Cita cita = new CitaWebBuilder()
                .idPaciente(15)
                .idMedico(5)
                .fecha(fecha)
                .horaInicio(inicio)
                .build();

        assertEquals(15,    cita.getIdPaciente());
        assertEquals(5,     cita.getIdMedico());
        assertEquals(fecha, cita.getFecha());
        assertEquals(inicio, cita.getHoraInicio());
    }

    // ── CitaManualBuilder ─────────────────────────────────────────────────────

    @Test
    void citaManual_debeCrearseConEstadoConfirmada() {
        Cita cita = new CitaManualBuilder()
                .idPaciente(12)
                .idMedico(4)
                .fecha(LocalDate.of(2026, 5, 13))
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(10, 45))
                .build();

        assertEquals(3, cita.getIdEstadoCita(),
                "El estado de una cita manual debe ser Confirmada (id=3)");
    }

    @Test
    void citaManual_sinHoraFin_debeLanzarExcepcion() {
        assertThrows(IllegalStateException.class, () -> new CitaManualBuilder()
                .idPaciente(12)
                .idMedico(4)
                .fecha(LocalDate.of(2026, 5, 13))
                .horaInicio(LocalTime.of(10, 0))
                .build(),
                "CitaManualBuilder debe lanzar excepción si horaFin no fue asignada");
    }

    @Test
    void citaManual_debeAsignarTodosLosCampos() {
        LocalDate fecha  = LocalDate.of(2026, 5, 14);
        LocalTime inicio = LocalTime.of(7, 30);
        LocalTime fin    = LocalTime.of(8, 0);

        Cita cita = new CitaManualBuilder()
                .idPaciente(20)
                .idMedico(6)
                .fecha(fecha)
                .horaInicio(inicio)
                .horaFin(fin)
                .build();

        assertEquals(20,    cita.getIdPaciente());
        assertEquals(6,     cita.getIdMedico());
        assertEquals(fecha, cita.getFecha());
        assertEquals(inicio, cita.getHoraInicio());
        assertEquals(fin,    cita.getHoraFin());
    }

    // ── Herencia ──────────────────────────────────────────────────────────────

    @Test
    void citaWebBuilder_debeSerSubclaseDeCitaBuilder() {
        assertTrue(CitaBuilder.class.isAssignableFrom(CitaWebBuilder.class),
                "CitaWebBuilder debe heredar de CitaBuilder");
    }

    @Test
    void citaManualBuilder_debeSerSubclaseDeCitaBuilder() {
        assertTrue(CitaBuilder.class.isAssignableFrom(CitaManualBuilder.class),
                "CitaManualBuilder debe heredar de CitaBuilder");
    }
}
