package com.proyecto.microservicio_agendamiento.application.usecase;

import com.proyecto.microservicio_agendamiento.application.dto.ReagendarCitaCommand;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.model.EstadoCitaId;
import com.proyecto.microservicio_agendamiento.domain.model.ValidadorSolapamiento;
import com.proyecto.microservicio_agendamiento.domain.ports.in.GestionarCitaPort;
import com.proyecto.microservicio_agendamiento.domain.ports.in.ConsultarDisponibilidadPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.CitaRepositoryPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.EventoCitaPublisherPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestionarCitaUseCase implements GestionarCitaPort {

    private static final int DURACION_REAGENDA_MINUTOS = 30;

    private final CitaRepositoryPort citaRepo;
    private final EventoCitaPublisherPort publisher;
    private final RegistrarHistorialCitaUseCase registrarHistorial;
    private final ConsultarDisponibilidadPort disponibilidad;

    public GestionarCitaUseCase(CitaRepositoryPort citaRepo,
                                 EventoCitaPublisherPort publisher,
                                 RegistrarHistorialCitaUseCase registrarHistorial,
                                 ConsultarDisponibilidadPort disponibilidad) {
        this.citaRepo             = citaRepo;
        this.publisher            = publisher;
        this.registrarHistorial   = registrarHistorial;
        this.disponibilidad       = disponibilidad;
    }

    @Override
    public List<Cita> listar(Integer idMedico, LocalDate fecha) {
        if (idMedico != null && fecha != null) {
            // Médico + fecha: filtro exacto
            return citaRepo.findByMedicoFechaExcluyendoEstado(idMedico, fecha, EstadoCitaId.CANCELADA);
        } else if (idMedico != null) {
            // Solo médico: todas las citas del médico (sin filtro de fecha)
            return citaRepo.findByMedicoExcluyendoEstado(idMedico, EstadoCitaId.CANCELADA);
        } else if (fecha != null) {
            // Solo fecha: todas las citas de ese día
            return citaRepo.findByFechaExcluyendoEstado(fecha, EstadoCitaId.CANCELADA);
        } else {
            // Sin filtros: citas de hoy (comportamiento por defecto del endpoint /api/citas)
            return citaRepo.findByFechaExcluyendoEstado(LocalDate.now(), EstadoCitaId.CANCELADA);
        }
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

        // Validar que la nueva hora esté dentro de la jornada y libre (excluyendo esta cita)
        List<LocalTime> horasDisponibles = disponibilidad.consultar(
                cita.getIdMedico(), command.getNuevaFecha(), command.getIdCita());
        if (!horasDisponibles.contains(command.getNuevaHora())) {
            throw new IllegalArgumentException(
                    "El horario seleccionado no está disponible según la jornada laboral del médico.");
        }

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
