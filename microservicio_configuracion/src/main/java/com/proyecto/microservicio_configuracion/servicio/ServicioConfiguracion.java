package com.proyecto.microservicio_configuracion.servicio;

import com.proyecto.microservicio_configuracion.modelo.DominioEspecialidad;
import com.proyecto.microservicio_configuracion.modelo.JornadaLaboral;
import com.proyecto.microservicio_configuracion.repositorio.RepositorioDominioEspecialidad;
import com.proyecto.microservicio_configuracion.repositorio.RepositorioJornadaLaboral;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioConfiguracion {

    private final RepositorioJornadaLaboral repoJornada;
    private final RepositorioDominioEspecialidad repoEspecialidad;

    public ServicioConfiguracion(RepositorioJornadaLaboral repoJornada,
                                  RepositorioDominioEspecialidad repoEspecialidad) {
        this.repoJornada = repoJornada;
        this.repoEspecialidad = repoEspecialidad;
    }

    // ─── Jornadas ────────────────────────────────────────────────────────────

    /**
     * RF4: Configura la jornada de un médico (días, franja horaria, intervalo).
     * Si idJornada <= 0 crea, si no actualiza.
     */
    public JornadaLaboral configurarDisponibilidadMedico(JornadaLaboral jornada) {
        if (jornada.getHoraInicio() == null || jornada.getHoraFin() == null) {
            throw new IllegalArgumentException("Hora de inicio y fin son obligatorias.");
        }
        if (jornada.getHoraInicio().isAfter(jornada.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la de fin.");
        }
        if (jornada.getDuracionEstimadaAtencion() <= 0) {
            jornada.setDuracionEstimadaAtencion(30); // default 30 min
        }
        return repoJornada.save(jornada);
    }

    public List<JornadaLaboral> obtenerTodasLasJornadas() {
        return repoJornada.findAll();
    }

    /** Jornadas de un médico específico (usado por ms-agendamiento). */
    public List<JornadaLaboral> obtenerJornadasPorMedico(int idMedico) {
        return repoJornada.findByIdUsuario(idMedico);
    }

    /**
     * Devuelve los nombres de días de la semana en que el médico tiene jornada.
     * Usado por el frontend para deshabilitar días sin turno en el DatePicker.
     */
    public List<String> obtenerDiasConJornada(int idMedico) {
        return repoJornada.findByIdUsuario(idMedico).stream()
                .map(JornadaLaboral::getDiaSemana)
                .distinct()
                .toList();
    }

    /** Jornada de un médico en un día específico de la semana. */
    public Optional<JornadaLaboral> obtenerJornadaPorMedicoYDia(int idMedico, String diaSemana) {
        return repoJornada.findByIdUsuarioAndDiaSemana(idMedico, diaSemana)
                .stream().findFirst();
    }

    public JornadaLaboral editarTurno(int idJornada, JornadaLaboral jornada) {
        if (!repoJornada.existsById(idJornada)) {
            throw new IllegalArgumentException("Jornada no encontrada: " + idJornada);
        }
        if (jornada.getHoraInicio().isAfter(jornada.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la de fin.");
        }
        jornada.setIdJornada(idJornada);
        return repoJornada.save(jornada);
    }

    public boolean eliminarTurno(int idJornada) {
        if (!repoJornada.existsById(idJornada)) return false;
        repoJornada.deleteById(idJornada);
        return true;
    }

    // ─── Especialidades ──────────────────────────────────────────────────────

    public List<DominioEspecialidad> listarEspecialidades() {
        return repoEspecialidad.findAll();
    }

    public Optional<DominioEspecialidad> buscarEspecialidad(int idEspecialidad) {
        return repoEspecialidad.findById(idEspecialidad);
    }
}
