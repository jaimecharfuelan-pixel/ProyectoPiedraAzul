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

    public boolean configurarDisponibilidadMedico(JornadaLaboral jornada) {
        if (jornada.getHoraInicio().isAfter(jornada.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la de fin.");
        }
        repoJornada.save(jornada);
        return true;
    }

    public List<JornadaLaboral> obtenerTodasLasJornadas() {
        return repoJornada.findAll();
    }

    public List<JornadaLaboral> obtenerJornadasPorMedico(int idMedico) {
        return repoJornada.findByIdUsuario(idMedico);
    }

    public boolean editarTurno(JornadaLaboral jornada) {
        if (!repoJornada.existsById(jornada.getIdJornada())) return false;
        repoJornada.save(jornada);
        return true;
    }

    public boolean eliminarTurno(int idJornada) {
        if (!repoJornada.existsById(idJornada)) return false;
        repoJornada.deleteById(idJornada);
        return true;
    }

    public List<DominioEspecialidad> listarEspecialidades() {
        return repoEspecialidad.findAll();
    }

    public Optional<DominioEspecialidad> buscarEspecialidad(int idEspecialidad) {
        return repoEspecialidad.findById(idEspecialidad);
    }
}
