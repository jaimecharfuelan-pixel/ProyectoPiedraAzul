package com.proyecto.microservicio_configuracion.servicio;

import com.proyecto.microservicio_configuracion.modelo.JornadaLaboral;
import com.proyecto.microservicio_configuracion.repositorio.RepositorioDominioEspecialidad;
import com.proyecto.microservicio_configuracion.repositorio.RepositorioJornadaLaboral;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioConfiguracionTest {

    @Mock
    private RepositorioJornadaLaboral repoJornada;

    @Mock
    private RepositorioDominioEspecialidad repoEspecialidad;

    @InjectMocks
    private ServicioConfiguracion servicio;

    // ── configurarDisponibilidadMedico ────────────────────────────────────────

    @Test
    void configurarJornada_valida_guardaYRetornaJornada() {
        JornadaLaboral jornada = new JornadaLaboral();
        jornada.setIdUsuario(3);
        jornada.setDiaSemana("Lunes");
        jornada.setHoraInicio(LocalTime.of(7, 0));
        jornada.setHoraFin(LocalTime.of(12, 0));
        jornada.setDuracionEstimadaAtencion(30);

        when(repoJornada.save(any(JornadaLaboral.class))).thenReturn(jornada);

        JornadaLaboral resultado = servicio.configurarDisponibilidadMedico(jornada);

        assertNotNull(resultado);
        assertEquals("Lunes", resultado.getDiaSemana());
        assertEquals(3, resultado.getIdUsuario());
        verify(repoJornada, times(1)).save(jornada);
    }

    @Test
    void configurarJornada_sinHoraInicio_lanzaExcepcion() {
        JornadaLaboral jornada = new JornadaLaboral();
        jornada.setHoraFin(LocalTime.of(12, 0));
        // horaInicio null

        assertThrows(IllegalArgumentException.class,
                () -> servicio.configurarDisponibilidadMedico(jornada),
                "Debe lanzar excepción si horaInicio es null");
    }

    @Test
    void configurarJornada_horaInicioMayorQueHoraFin_lanzaExcepcion() {
        JornadaLaboral jornada = new JornadaLaboral();
        jornada.setHoraInicio(LocalTime.of(14, 0));
        jornada.setHoraFin(LocalTime.of(8, 0));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.configurarDisponibilidadMedico(jornada),
                "Debe lanzar excepción si horaInicio es posterior a horaFin");
    }

    @Test
    void configurarJornada_duracionCero_asignaDefault30() {
        JornadaLaboral jornada = new JornadaLaboral();
        jornada.setHoraInicio(LocalTime.of(7, 0));
        jornada.setHoraFin(LocalTime.of(12, 0));
        jornada.setDuracionEstimadaAtencion(0); // inválido → debe quedar 30

        when(repoJornada.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JornadaLaboral resultado = servicio.configurarDisponibilidadMedico(jornada);

        assertEquals(30, resultado.getDuracionEstimadaAtencion(),
                "La duración debe ser 30 por defecto si se pasa 0");
    }

    // ── obtenerJornadasPorMedico ──────────────────────────────────────────────

    @Test
    void obtenerJornadasPorMedico_retornaListaCorrecta() {
        JornadaLaboral j1 = new JornadaLaboral();
        j1.setIdUsuario(3); j1.setDiaSemana("Lunes");
        JornadaLaboral j2 = new JornadaLaboral();
        j2.setIdUsuario(3); j2.setDiaSemana("Miércoles");

        when(repoJornada.findByIdUsuario(3)).thenReturn(List.of(j1, j2));

        List<JornadaLaboral> resultado = servicio.obtenerJornadasPorMedico(3);

        assertEquals(2, resultado.size());
        assertEquals("Lunes", resultado.get(0).getDiaSemana());
    }

    // ── obtenerDiasConJornada ─────────────────────────────────────────────────

    @Test
    void obtenerDiasConJornada_retornaDiasSinDuplicados() {
        JornadaLaboral j1 = new JornadaLaboral(); j1.setDiaSemana("Lunes");
        JornadaLaboral j2 = new JornadaLaboral(); j2.setDiaSemana("Lunes"); // duplicado
        JornadaLaboral j3 = new JornadaLaboral(); j3.setDiaSemana("Viernes");

        when(repoJornada.findByIdUsuario(3)).thenReturn(List.of(j1, j2, j3));

        List<String> dias = servicio.obtenerDiasConJornada(3);

        assertEquals(2, dias.size(), "No debe haber días duplicados");
        assertTrue(dias.contains("Lunes"));
        assertTrue(dias.contains("Viernes"));
    }

    // ── eliminarTurno ─────────────────────────────────────────────────────────

    @Test
    void eliminarTurno_existente_retornaTrue() {
        when(repoJornada.existsById(1)).thenReturn(true);

        boolean resultado = servicio.eliminarTurno(1);

        assertTrue(resultado);
        verify(repoJornada, times(1)).deleteById(1);
    }

    @Test
    void eliminarTurno_noExistente_retornaFalse() {
        when(repoJornada.existsById(99)).thenReturn(false);

        boolean resultado = servicio.eliminarTurno(99);

        assertFalse(resultado);
        verify(repoJornada, never()).deleteById(any());
    }
}
