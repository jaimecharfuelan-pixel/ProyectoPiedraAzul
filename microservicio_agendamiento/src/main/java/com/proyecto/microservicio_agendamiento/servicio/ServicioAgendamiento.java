package com.proyecto.microservicio_agendamiento.servicio;

import com.proyecto.microservicio_agendamiento.dto.JornadaResumenDTO;
import com.proyecto.microservicio_agendamiento.mensajeria.PublicadorCitas;
import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.modelo.EstadoCita;
import com.proyecto.microservicio_agendamiento.repositorio.RepositorioCitas;
import com.proyecto.microservicio_agendamiento.servicio.template.CitaProcesoTemplate;
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
    private final CitaProcesoTemplate flujoWeb = new CitaProcesoTemplate() {
        @Override
        protected void despuesDeGuardar(Cita cita) {
            // Hook para futuras acciones especificas del flujo web.
        }
    };
    private final CitaProcesoTemplate flujoManual = new CitaProcesoTemplate() {
        @Override
        protected void despuesDeGuardar(Cita cita) {
            // Hook para futuras acciones especificas del flujo manual.
        }
    };

    @org.springframework.beans.factory.annotation.Value("${ms.configuracion.url:http://microservicio-configuracion:8080}")
    private String msConfiguracionUrl;

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
                msConfiguracionUrl + "/api/jornadas?medicoId=" + idMedico, JornadaResumenDTO[].class);

        if (jornadas == null) return horariosLibres;

        JornadaResumenDTO jornadaHoy = Arrays.stream(jornadas)
                .filter(j -> j.getDiaSemana().equalsIgnoreCase(diaSemana))
                .findFirst()
                .orElse(null);

        if (jornadaHoy == null) return horariosLibres;

        List<Cita> citasOcupadas = repoCitas.findByIdMedicoAndFechaAndIdEstadoCitaNot(
                idMedico, fecha, EstadoCita.CANCELADA);

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

        Cita cita = Cita.builder()
                .idPaciente(idPaciente)
                .idMedico(idMedico)
                .fecha(fecha)
                .horaInicio(hora)
                .horaFin(hora.plusMinutes(30))
                .idEstadoCita(2)
                .build();
        return flujoWeb.guardarYPublicar(cita, repoCitas, publicador);
    }

    public boolean crearCitaManual(Cita cita) {
        return flujoManual.guardarYPublicar(cita, repoCitas, publicador);
    }

    public List<Cita> listarCitas(Integer idMedico, LocalDate fecha) {
        LocalDate fechaFiltro = (fecha != null) ? fecha : LocalDate.now();
        if (idMedico != null) {
            return repoCitas.findByIdMedicoAndFechaAndIdEstadoCitaNot(idMedico, fechaFiltro, EstadoCita.CANCELADA);
        }
        return repoCitas.findByFechaAndIdEstadoCitaNot(fechaFiltro, EstadoCita.CANCELADA);
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

    /**
     * Cancela una cita cambiando su estado a 1 (Cancelada).
     * No elimina el registro de la BD.
     */
    public boolean cancelarCita(int idCita) {
        return repoCitas.findById(idCita).map(cita -> {
            cita.setIdEstadoCita(EstadoCita.CANCELADA);
            repoCitas.save(cita);
            publicador.publicarCitaCancelada(idCita);
            return true;
        }).orElse(false);
    }

    /**
     * Reagenda una cita existente a una nueva fecha y hora.
     * No valida disponibilidad — solo cambia fecha/hora y vuelve el estado a Pendiente.
     */
    public ReagendamientoResultado reagendarCita(int idCita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Cita cita = repoCitas.findById(idCita).orElse(null);
        if (cita == null) {
            return ReagendamientoResultado.NO_ENCONTRADA;
        }
        if (cita.getIdEstadoCita() != null && cita.getIdEstadoCita() == EstadoCita.CANCELADA) {
            return ReagendamientoResultado.CITA_CANCELADA;
        }

        cita.setFecha(nuevaFecha);
        cita.setHoraInicio(nuevaHora);
        cita.setHoraFin(nuevaHora.plusMinutes(30));
        cita.setIdEstadoCita(EstadoCita.PENDIENTE);
        repoCitas.save(cita);
        return ReagendamientoResultado.OK;
    }

    public enum ReagendamientoResultado {
        OK, NO_ENCONTRADA, CITA_CANCELADA
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
