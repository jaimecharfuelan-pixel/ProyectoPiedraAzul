package com.proyecto.microservicio_agendamiento.application.usecase;

import com.proyecto.microservicio_agendamiento.application.dto.AgendarCitaWebCommand;
import com.proyecto.microservicio_agendamiento.application.dto.CrearCitaManualCommand;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.model.EstadoCitaId;
import com.proyecto.microservicio_agendamiento.domain.model.ValidadorSolapamiento;
import com.proyecto.microservicio_agendamiento.domain.ports.in.AgendarCitaPort;
import com.proyecto.microservicio_agendamiento.domain.ports.in.ConsultarDisponibilidadPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.CitaRepositoryPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.EventoCitaPublisherPort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class AgendarCitaUseCase implements AgendarCitaPort {

    private static final int DURACION_WEB_MINUTOS = 30;

    private final CitaRepositoryPort citaRepo;
    private final EventoCitaPublisherPort publisher;
    private final ConsultarDisponibilidadPort disponibilidad;

    public AgendarCitaUseCase(CitaRepositoryPort citaRepo,
                               EventoCitaPublisherPort publisher,
                               ConsultarDisponibilidadPort disponibilidad) {
        this.citaRepo       = citaRepo;
        this.publisher      = publisher;
        this.disponibilidad = disponibilidad;
    }

    /**
     * Flujo web: valida disponibilidad con reglas de solapamiento, crea cita en estado Pendiente y publica evento.
     * Retorna true si fue exitosa, false si hay conflicto.
     */
    @Override
    public boolean agendarWeb(AgendarCitaWebCommand command) {
        List<LocalTime> horasLibres = disponibilidad.consultar(command.getIdMedico(), command.getFecha());
        if (!horasLibres.contains(command.getHora())) {
            return false; // Hora no disponible
        }

        // Validar reglas de solapamiento en el dominio
        LocalTime horaFin = command.getHora().plusMinutes(DURACION_WEB_MINUTOS);
        List<Cita> todasLasCitas = citaRepo.findAll();
        List<String> errores = ValidadorSolapamiento.validarCompletamente(
                command.getIdMedico(),
                command.getIdPaciente(),
                command.getFecha(),
                command.getHora(),
                horaFin,
                todasLasCitas
        );

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errores));
        }

        Cita cita = new Cita(
                command.getIdPaciente(),
                command.getIdMedico(),
                command.getFecha(),
                command.getHora(),
                horaFin
        );
        // Estado inicial Pendiente ya asignado por el constructor de Cita

        Cita guardada = citaRepo.save(cita);
        publisher.publicarCitaCreada(guardada);
        return true;
    }

    /**
     * Flujo manual: el agendador crea la cita directamente en estado Confirmada.
     * También valida las reglas de solapamiento para evitar conflictos.
     */
    @Override
    public boolean crearManual(CrearCitaManualCommand command) {
        if (command.getHoraFin() == null) {
            throw new IllegalArgumentException("horaFin es obligatoria para citas manuales.");
        }
        if (command.getHoraFin().isBefore(command.getHoraInicio())) {
            throw new IllegalArgumentException("horaFin debe ser posterior a horaInicio.");
        }

        // Validar reglas de solapamiento en el dominio
        List<Cita> todasLasCitas = citaRepo.findAll();
        List<String> errores = ValidadorSolapamiento.validarCompletamente(
                command.getIdMedico(),
                command.getIdPaciente(),
                command.getFecha(),
                command.getHoraInicio(),
                command.getHoraFin(),
                todasLasCitas
        );

        if (!errores.isEmpty()) {
            // Hay conflictos - loguear y lanzar excepción
            String mensaje = String.join("; ", errores);
            throw new IllegalArgumentException("No se puede agendar la cita: " + mensaje);
        }

        Cita cita = new Cita(
                command.getIdPaciente(),
                command.getIdMedico(),
                command.getFecha(),
                command.getHoraInicio(),
                command.getHoraFin()
        );
        cita.setIdEstadoCita(EstadoCitaId.CONFIRMADA); // manual = ya confirmada

        Cita guardada = citaRepo.save(cita);
        publisher.publicarCitaCreada(guardada);
        return true;
    }
}
