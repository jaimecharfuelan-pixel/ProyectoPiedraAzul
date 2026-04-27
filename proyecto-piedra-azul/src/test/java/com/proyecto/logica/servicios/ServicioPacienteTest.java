package com.proyecto.logica.servicios;

import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Usuario;
import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.persistencia.interfaces.IRepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioPacienteTest {

    private IRepositorioUsuario repoUsuario;
    private IRepositorioCitas repoCitas;
    private ServicioPaciente servicio;

    @BeforeEach
    void setUp() {
        repoUsuario = Mockito.mock(IRepositorioUsuario.class);
        repoCitas   = Mockito.mock(IRepositorioCitas.class);
        servicio = new ServicioPaciente(repoUsuario, repoCitas);
    }

    @Test
    void registrarPaciente_usuarioGuardadoOk_retornaTrue() {
        Paciente paciente = new Paciente();
        Usuario usuario = new Usuario(0, "paciente1", "pass");
        when(repoUsuario.guardar(usuario)).thenReturn(10);

        boolean resultado = servicio.registrarPaciente(paciente, usuario);

        assertTrue(resultado);
        assertEquals(10, paciente.getIdUsuario());
    }

    @Test
    void registrarPaciente_fallaGuardarUsuario_retornaFalse() {
        Paciente paciente = new Paciente();
        Usuario usuario = new Usuario(0, "paciente1", "pass");
        when(repoUsuario.guardar(usuario)).thenReturn(-1);

        boolean resultado = servicio.registrarPaciente(paciente, usuario);

        assertFalse(resultado);
    }

    @Test
    void obtenerHistorialCitas_retornaSoloPasadas() {
        LocalDate ayer = LocalDate.now().minusDays(1);
        LocalDate manana = LocalDate.now().plusDays(1);
        Cita pasada  = new Cita(1, 5, 1, ayer,   LocalTime.of(8, 0), LocalTime.of(8, 30), 1);
        Cita futura  = new Cita(2, 5, 1, manana, LocalTime.of(9, 0), LocalTime.of(9, 30), 1);
        when(repoCitas.listar()).thenReturn(List.of(pasada, futura));

        List<Cita> historial = servicio.obtenerHistorialCitas(5);

        assertEquals(1, historial.size());
        assertEquals(ayer, historial.get(0).getFecha());
    }

    @Test
    void obtenerCitasFuturas_retornaSoloFuturas() {
        LocalDate ayer = LocalDate.now().minusDays(1);
        LocalDate hoy  = LocalDate.now();
        Cita pasada = new Cita(1, 5, 1, ayer, LocalTime.of(8, 0), LocalTime.of(8, 30), 1);
        Cita hoyC   = new Cita(2, 5, 1, hoy,  LocalTime.of(9, 0), LocalTime.of(9, 30), 1);
        when(repoCitas.listar()).thenReturn(List.of(pasada, hoyC));

        List<Cita> futuras = servicio.obtenerCitasFuturas(5);

        assertEquals(1, futuras.size());
        assertEquals(hoy, futuras.get(0).getFecha());
    }

    @Test
    void obtenerHistorialCitas_otroPaciente_noAparece() {
        LocalDate ayer = LocalDate.now().minusDays(1);
        Cita otroPaciente = new Cita(1, 99, 1, ayer, LocalTime.of(8, 0), LocalTime.of(8, 30), 1);
        when(repoCitas.listar()).thenReturn(List.of(otroPaciente));

        List<Cita> historial = servicio.obtenerHistorialCitas(5);

        assertTrue(historial.isEmpty());
    }
}
