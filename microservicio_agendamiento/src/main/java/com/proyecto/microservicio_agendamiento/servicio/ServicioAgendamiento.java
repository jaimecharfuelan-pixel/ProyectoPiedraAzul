package com.proyecto.microservicio_agendamiento.servicio;

import com.proyecto.microservicio_agendamiento.dto.JornadaResumenDTO;
import com.proyecto.microservicio_agendamiento.mensajeria.PublicadorCitas;
import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.repositorio.RepositorioCitas;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioAgendamiento {

    private final RepositorioCitas repoCitas;
    private final RestTemplate restTemplate;
    private final PublicadorCitas publicador;

    private static final String URL_JORNADAS = "http://ms-configuracion:8083/api/jornadas";

    public ServicioAgendamiento(RepositorioCitas repoCitas,
                                RestTemplate restTemplate,
                                PublicadorCitas publicador) {
        this.repoCitas   = repoCitas;
        this.restTemplate = restTemplate;
        this.publicador  = publicador;
    }

    /**
     * Consulta disponibilidad llamando a ms-configuracion via HTTP para obtener jornadas.
     */
    public List<LocalTime> consultarDisponibilidad(int idMedico, LocalDate fecha) {
        List<LocalTime> horariosLibres = new ArrayList<>();
        String diaSemana = traducirDia(fecha.getDayOfWeek());

        // Llamada HTTP a ms-configuracion
        JornadaResumenDTO[] jornadas = restTemplate.getForObject(
                URL_JORNADAS + "?medicoId=" + idMedico, JornadaResumenDTO[].class);

        if (jornadas == null) return horariosLibres;

        JornadaResumenDTO jornadaHoy = Arrays.stream(jornadas)
                .filter(j -> j.getDiaSemana().equalsIgnoreCase(diaSemana))
                .findFirst()
                .orElse(null);

        if (jornadaHoy == null) return horariosLibres;

        List<Cita> citasOcupadas = repoCitas.findByIdMedicoAndFecha(idMedico, fecha);

        LocalTime actual = jornadaHoy.getHoraInicio();
        while (actual.isBefore(jornadaHoy.getHoraFin())) {
            LocalTime horaEvaluar = actual;
            boolean ocupado = citasOcupadas.stream().anyMatch(c ->
                    horaEvaluar.equals(c.getHoraInicio()) ||
                    (horaEvaluar.isAfter(c.getHoraInicio()) && horaEvaluar.isBefore(c.getHoraFin()))
            );
            if (!ocupado) horariosLibres.add(horaEvaluar);
            actual = actual.plusMinutes(30);
        }

        return horariosLibres;
    }

    public boolean agendarCitaWeb(int idPaciente, int idMedico, LocalDate fecha, LocalTime hora) {
        List<LocalTime> disponibles = consultarDisponibilidad(idMedico, fecha);
        if (!disponibles.contains(hora)) return false;

        Cita cita = new Cita();
        cita.setIdPaciente(idPaciente);
        cita.setIdMedico(idMedico);
        cita.setFecha(fecha);
        cita.setHoraInicio(hora);
        cita.setHoraFin(hora.plusMinutes(30));
        cita.setIdEstadoCita(2); // Pendiente
        repoCitas.save(cita);
        publicador.publicarCitaCreada(cita); // avisa a ms-usuarios que se creó la cita
        return true;
    }

    public boolean crearCitaManual(Cita cita) {
        repoCitas.save(cita);
        publicador.publicarCitaCreada(cita); // avisa también en cita manual
        return true;
    }

    public List<Cita> listarCitas(Integer idMedico, LocalDate fecha) {
        LocalDate fechaFiltro = (fecha != null) ? fecha : LocalDate.now();
        if (idMedico != null) {
            return repoCitas.findByIdMedicoAndFecha(idMedico, fechaFiltro);
        }
        return repoCitas.findByFecha(fechaFiltro);
    }

    public List<Cita> listarTodasLasCitas() {
        return repoCitas.findAll();
    }

    public List<Cita> obtenerHistorialCitas(int idPaciente) {
        return repoCitas.findByIdPaciente(idPaciente).stream()
                .filter(c -> c.getFecha() != null && c.getFecha().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Cita> obtenerCitasFuturas(int idPaciente) {
        return repoCitas.findByIdPaciente(idPaciente).stream()
                .filter(c -> c.getFecha() != null && !c.getFecha().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public boolean editarCita(Cita cita) {
        if (!repoCitas.existsById(cita.getIdCita())) return false;
        repoCitas.save(cita);
        return true;
    }

    public boolean cancelarCita(int idCita) {
        if (!repoCitas.existsById(idCita)) return false;
        repoCitas.deleteById(idCita);
        publicador.publicarCitaCancelada(idCita); // avisa a ms-usuarios que se canceló
        return true;
    }

    private String traducirDia(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }
}
