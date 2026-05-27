package com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_agendamiento.domain.model.HistorialCita;
import com.proyecto.microservicio_agendamiento.domain.ports.out.HistorialCitaRepositoryPort;
import com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.entity.HistorialCitaEntity;
import com.proyecto.microservicio_agendamiento.infrastructure.adapters.out.persistence.repository.JpaHistorialCitaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HistorialCitaRepositoryAdapter implements HistorialCitaRepositoryPort {

    private final JpaHistorialCitaRepository jpa;

    public HistorialCitaRepositoryAdapter(JpaHistorialCitaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public HistorialCita save(HistorialCita historial) {
        return toDomain(jpa.save(toEntity(historial)));
    }

    @Override
    public Optional<HistorialCita> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<HistorialCita> findByCitaId(int idCita) {
        return jpa.findByIdCita(idCita).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<HistorialCita> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    // ── Mapeos ────────────────────────────────────────────────────────────────

    private HistorialCita toDomain(HistorialCitaEntity e) {
        HistorialCita h = new HistorialCita(
                e.getIdCita(),
                e.getTipoCambio(),
                e.getValorAnterior(),
                e.getValorNuevo(),
                e.getFechaHora(),
                e.getIdUsuario(),
                e.getDescripcion()
        );
        h.setIdHistorial(e.getIdHistorial());
        return h;
    }

    private HistorialCitaEntity toEntity(HistorialCita h) {
        HistorialCitaEntity e = new HistorialCitaEntity(
                h.getIdCita(),
                h.getTipoCambio(),
                h.getValorAnterior(),
                h.getValorNuevo(),
                h.getFechaHora(),
                h.getIdUsuario(),
                h.getDescripcion()
        );
        e.setIdHistorial(h.getIdHistorial());
        return e;
    }
}
