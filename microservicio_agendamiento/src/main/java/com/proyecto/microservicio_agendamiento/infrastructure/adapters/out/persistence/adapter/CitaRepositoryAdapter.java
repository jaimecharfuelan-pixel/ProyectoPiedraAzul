package com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_agendamiento.domain.model.Cita;
import com.proyecto.microservicio_agendamiento.domain.ports.out.CitaRepositoryPort;
import com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.entity.CitaEntity;
import com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.repository.JpaCitaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CitaRepositoryAdapter implements CitaRepositoryPort {

    private final JpaCitaRepository jpa;

    public CitaRepositoryAdapter(JpaCitaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cita save(Cita cita) {
        return toDomain(jpa.save(toEntity(cita)));
    }

    @Override
    public Optional<Cita> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(int id) {
        return jpa.existsById(id);
    }

    @Override
    public List<Cita> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cita> findByMedicoFechaExcluyendoEstado(int idMedico, LocalDate fecha, int idEstadoExcluido) {
        return jpa.findByIdMedicoAndFechaAndIdEstadoCitaNot(idMedico, fecha, idEstadoExcluido)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cita> findByFechaExcluyendoEstado(LocalDate fecha, int idEstadoExcluido) {
        return jpa.findByFechaAndIdEstadoCitaNot(fecha, idEstadoExcluido)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cita> findByPaciente(int idPaciente) {
        return jpa.findByIdPaciente(idPaciente).stream().map(this::toDomain).collect(Collectors.toList());
    }

    // ── Mapeos ────────────────────────────────────────────────────────────────

    private Cita toDomain(CitaEntity e) {
        int idEstado = e.getIdEstadoCita() != null ? e.getIdEstadoCita() : 2; // default Pendiente
        Cita c = new Cita(e.getIdCita(), e.getIdPaciente(), e.getIdMedico(),
                e.getFecha(), e.getHoraInicio(), e.getHoraFin(), idEstado);
        return c;
    }

    private CitaEntity toEntity(Cita c) {
        CitaEntity e = new CitaEntity();
        e.setIdCita(c.getIdCita());
        e.setIdPaciente(c.getIdPaciente());
        e.setIdMedico(c.getIdMedico());
        e.setFecha(c.getFecha());
        e.setHoraInicio(c.getHoraInicio());
        e.setHoraFin(c.getHoraFin());
        e.setIdEstadoCita(c.getIdEstadoCita());
        return e;
    }
}
