package com.proyecto.microservicio_agendamiento.domain.ports.in;

import com.proyecto.microservicio_agendamiento.application.dto.ReagendarCitaCommand;
import com.proyecto.microservicio_agendamiento.application.usecase.GestionarCitaUseCase.ReagendamientoResultado;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;

import java.time.LocalDate;
import java.util.List;

public interface GestionarCitaPort {
    List<Cita> listar(Integer idMedico, LocalDate fecha);
    List<Cita> listarTodas();
    List<Cita> historialPaciente(int idPaciente);
    List<Cita> citasFuturasPaciente(int idPaciente);
    boolean editar(Cita cita);
    boolean cancelar(int idCita);
    ReagendamientoResultado reagendar(ReagendarCitaCommand command);
}
