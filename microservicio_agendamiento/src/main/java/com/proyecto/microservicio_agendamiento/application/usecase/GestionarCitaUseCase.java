package com.proyecto.microservicio_agendamiento.application.usecase;

import com.proyecto.microservicio_agendamiento.application.dto.ReagendarCitaCommand;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.model.EstadoCitaId;
import com.proyecto.microservicio_agendamiento.domain.ports.in.GestionarCitaPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.CitaRepositoryPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.EventoCitaPublisherPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestionarCitaUseCase implements GestionarCitaPort {

    private static final int DURACION_REAGENDA_MINUTOS = 30;

    private final CitaRepositoryPort citaRepo;
    private final EventoCitaPublisherPort publisher;

    public GestionarCitaUseCase(CitaRepositoryPort citaRepo,
                                 EventoCitaPublisherPort publisher) {
        this.citaRepo  = citaRepo;
        this.publisher = publisher;
    }

    @Override
    public List<Cita> listar(Integer idMedico, LocalDate fecha) {
        LocalDate fechaFiltro = (fecha != null) ? fecha : LocalDate.now();
        if (idMedico != null) {
            return citaRepo.findByMedicoFechaExcluyendoEstado(idMedico, fechaFiltro, EstadoCitaId.CANCELADA);
        }
        return citaRepo.findByFechaExcluyendoEstado(fechaFiltro, EstadoCitaId.CANCELADA);
    }

    @Override
    public List<Cita> listarTodas() {
        return citaRepo.findAll();
    }

    @Override
    public List<Cita> historialPaciente(int idPaciente) {
        return citaRepo.findByPaciente(idPaciente).stream()
                .filter(c -> c.getFecha() != null && c.getFecha().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Cita> citasFuturasPaciente(int idPaciente) {
        return citaRepo.findByPaciente(idPaciente).stream()
                .filter(c -> c.getFecha() != null && !c.getFecha().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean editar(Cita cita) {
        if (!citaRepo.existsById(cita.getIdCita())) return false;
        citaRepo.save(cita);
        return true;
    }

    @Override
    public boolean cancelar(int idCita) {
        return citaRepo.findById(idCita).map(cita -> {
            if (!cita.cancelar()) return false;
            citaRepo.save(cita);
            publisher.publicarCitaCancelada(idCita);
            return true;
        }).orElse(false);
    }

    @Override
    public ReagendamientoResultado reagendar(ReagendarCitaCommand command) {
        Cita cita = citaRepo.findById(command.getIdCita()).orElse(null);
        if (cita == null) return ReagendamientoResultado.NO_ENCONTRADA;
        if (cita.getIdEstadoCita() == EstadoCitaId.CANCELADA) return ReagendamientoResultado.CITA_CANCELADA;

        cita.setFecha(command.getNuevaFecha());
        cita.setHoraInicio(command.getNuevaHora());
        cita.setHoraFin(command.getNuevaHora().plusMinutes(DURACION_REAGENDA_MINUTOS));
        cita.setIdEstadoCita(EstadoCitaId.PENDIENTE);
        citaRepo.save(cita);
        return ReagendamientoResultado.OK;
    }

    public enum ReagendamientoResultado {
        OK, NO_ENCONTRADA, CITA_CANCELADA
    }
}
