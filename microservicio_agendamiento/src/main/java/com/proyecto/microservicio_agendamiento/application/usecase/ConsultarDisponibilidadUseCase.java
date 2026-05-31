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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        return consultar(idMedico, fecha, null);
    }

    @Override
    public List<LocalTime> consultar(int idMedico, LocalDate fecha, Integer excluirCitaId) {
        Set<LocalTime> horariosLibres = new LinkedHashSet<>();
        String diaSemana = traducirDia(fecha.getDayOfWeek());

        List<JornadaResumen> jornadas = jornadaConsulta.obtenerJornadasPorMedico(idMedico);
        List<JornadaResumen> jornadasDelDia = jornadas.stream()
                .filter(j -> j.getDiaSemana() != null && j.getDiaSemana().equalsIgnoreCase(diaSemana))
                .toList();

        if (jornadasDelDia.isEmpty()) return List.of();

        List<Cita> citasOcupadas = citaRepo.findByMedicoFechaExcluyendoEstado(
                idMedico, fecha, EstadoCitaId.CANCELADA);

        for (JornadaResumen jornada : jornadasDelDia) {
            LocalTime actual = jornada.getHoraInicio();
            while (actual != null && actual.isBefore(jornada.getHoraFin())) {
                LocalTime slot = actual;
                boolean ocupado = citasOcupadas.stream()
                        .filter(c -> excluirCitaId == null || c.getIdCita() != excluirCitaId)
                        .anyMatch(c ->
                                slot.equals(c.getHoraInicio()) ||
                                (slot.isAfter(c.getHoraInicio()) && slot.isBefore(c.getHoraFin()))
                        );
                if (!ocupado) horariosLibres.add(slot);
                actual = actual.plusMinutes(DURACION_SLOT_MINUTOS);
            }
        }

        return horariosLibres.stream().sorted(Comparator.naturalOrder()).toList();
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
