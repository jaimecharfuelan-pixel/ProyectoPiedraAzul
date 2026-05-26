package com.proyecto.microservicio_agendamiento.application.usecase;

import com.proyecto.microservicio_agendamiento.application.dto.JornadaResumen;
import com.proyecto.microservicio_agendamiento.domain.model.EstadoCitaId;
import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.ports.in.ConsultarDisponibilidadPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.CitaRepositoryPort;
import com.proyecto.microservicio_agendamiento.domain.ports.out.JornadaConsultaPort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultarDisponibilidadUseCase implements ConsultarDisponibilidadPort {

    private static final int DURACION_SLOT_MINUTOS = 30;

    private final CitaRepositoryPort citaRepo;
    private final JornadaConsultaPort jornadaConsulta;

    public ConsultarDisponibilidadUseCase(CitaRepositoryPort citaRepo,
                                          JornadaConsultaPort jornadaConsulta) {
        this.citaRepo        = citaRepo;
        this.jornadaConsulta = jornadaConsulta;
    }

    @Override
    public List<LocalTime> consultar(int idMedico, LocalDate fecha) {
        List<LocalTime> horariosLibres = new ArrayList<>();
        String diaSemana = traducirDia(fecha.getDayOfWeek());

        List<JornadaResumen> jornadas = jornadaConsulta.obtenerJornadasPorMedico(idMedico);

        JornadaResumen jornadaHoy = jornadas.stream()
                .filter(j -> j.getDiaSemana().equalsIgnoreCase(diaSemana))
                .findFirst()
                .orElse(null);

        if (jornadaHoy == null) return horariosLibres;

        List<Cita> citasOcupadas = citaRepo.findByMedicoFechaExcluyendoEstado(
                idMedico, fecha, EstadoCitaId.CANCELADA);

        LocalTime actual = jornadaHoy.getHoraInicio();
        while (actual.isBefore(jornadaHoy.getHoraFin())) {
            LocalTime slot = actual;
            boolean ocupado = citasOcupadas.stream().anyMatch(c ->
                    slot.equals(c.getHoraInicio()) ||
                    (slot.isAfter(c.getHoraInicio()) && slot.isBefore(c.getHoraFin()))
            );
            if (!ocupado) horariosLibres.add(slot);
            actual = actual.plusMinutes(DURACION_SLOT_MINUTOS);
        }

        return horariosLibres;
    }

    private String traducirDia(DayOfWeek day) {
        return switch (day) {
            case MONDAY    -> "Lunes";
            case TUESDAY   -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY  -> "Jueves";
            case FRIDAY    -> "Viernes";
            case SATURDAY  -> "Sábado";
            case SUNDAY    -> "Domingo";
        };
    }
}
