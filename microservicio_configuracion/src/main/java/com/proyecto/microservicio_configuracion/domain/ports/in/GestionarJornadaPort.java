package com.proyecto.microservicio_configuracion.domain.ports.in;

import com.proyecto.microservicio_configuracion.domain.model.JornadaLaboral;

import java.util.List;
import java.util.Optional;

public interface GestionarJornadaPort {
    JornadaLaboral crear(JornadaLaboral jornada);
    JornadaLaboral editar(int idJornada, JornadaLaboral jornada);
    boolean eliminar(int idJornada);
    List<JornadaLaboral> listarTodas();
    List<JornadaLaboral> listarPorMedico(int idMedico);
    List<String> listarDiasConJornada(int idMedico);
    Optional<JornadaLaboral> buscarPorMedicoYDia(int idMedico, String diaSemana);
}
