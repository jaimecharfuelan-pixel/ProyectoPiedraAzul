package com.proyecto.microservicio_agendamiento.application.usecase;

import com.proyecto.microservicio_agendamiento.application.dto.ReagendarCitaCommand;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.model.EstadoCitaId;
import com.proyecto.microservicio_agendamiento.domain.model.ValidadorSolapamiento;
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
    private final RegistrarHistorialCitaUseCase registrarHistorial;

    public GestionarCitaUseCase(CitaRepositoryPort citaRepo,
                                 EventoCitaPublisherPort publisher,
                                 RegistrarHistorialCitaUseCase registrarHistorial) {
        this.citaRepo             = citaRepo;
        this.publisher            = publisher;
        this.registrarHistorial   = registrarHistorial;
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
            int estadoAnterior = cita.getIdEstadoCita();
            String nombreEstadoAnterior = cita.getNombreEstado();
            
            if (!cita.cancelar()) return false;
            citaRepo.save(cita);
            
            // Registrar el cambio en el historial
            registrarHistorial.registrarCambio(
                    idCita,
                    "CANCELACION",
                    nombreEstadoAnterior,
                    "Cancelada",
                    1, // ID usuario predeterminado (puede mejorase con contexto de sesión)
                    "Cita cancelada por el agendador"
            );
            
            publisher.publicarCitaCancelada(idCita);
            return true;
        }).orElse(false);
    }

    @Override
    public ReagendamientoResultado reagendar(ReagendarCitaCommand command) {
        Cita cita = citaRepo.findById(command.getIdCita()).orElse(null);
        if (cita == null) return ReagendamientoResultado.NO_ENCONTRADA;
        if (cita.getIdEstadoCita() == EstadoCitaId.CANCELADA) return ReagendamientoResultado.CITA_CANCELADA;

        // Guardar valores anteriores para el historial
        String fechaHoraAnterior = cita.getFecha() + " " + cita.getHoraInicio();
        String fechaHoraNueva = command.getNuevaFecha() + " " + command.getNuevaHora();

        List<Cita> otrasCitas = citaRepo.findAll().stream()
                .filter(c -> c.getIdCita() != command.getIdCita())
                .toList();
        List<String> errores = ValidadorSolapamiento.validarCompletamente(
                cita.getIdMedico(),
                cita.getIdPaciente(),
                command.getNuevaFecha(),
                command.getNuevaHora(),
                command.getNuevaHora().plusMinutes(DURACION_REAGENDA_MINUTOS),
                otrasCitas
        );
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errores));
        }
        
        cita.setFecha(command.getNuevaFecha());
        cita.setHoraInicio(command.getNuevaHora());
        cita.setHoraFin(command.getNuevaHora().plusMinutes(DURACION_REAGENDA_MINUTOS));
        cita.setIdEstadoCita(EstadoCitaId.PENDIENTE);
        citaRepo.save(cita);
        
        // Registrar el cambio en el historial
        registrarHistorial.registrarCambio(
                command.getIdCita(),
                "REAGENDAMIENTO",
                fechaHoraAnterior,
                fechaHoraNueva,
                1, // ID usuario predeterminado
                "Cita reagendada desde " + fechaHoraAnterior + " a " + fechaHoraNueva
        );
        
        return ReagendamientoResultado.OK;
    }

    public enum ReagendamientoResultado {
        OK, NO_ENCONTRADA, CITA_CANCELADA
    }
}
