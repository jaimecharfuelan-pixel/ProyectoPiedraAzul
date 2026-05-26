package com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_configuracion.domain.model.JornadaLaboral;
import com.proyecto.microservicio_configuracion.domain.ports.out.JornadaRepositoryPort;
import com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.entity.JornadaLaboralEntity;
import com.proyecto.microservicio_configuracion.infrastructure.adapters.out.persistence.repository.JpaJornadaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JornadaRepositoryAdapter implements JornadaRepositoryPort {

    private final JpaJornadaRepository jpa;

    public JornadaRepositoryAdapter(JpaJornadaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public JornadaLaboral save(JornadaLaboral jornada) {
        return toDomain(jpa.save(toEntity(jornada)));
    }

    @Override
    public Optional<JornadaLaboral> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(int id) {
        return jpa.existsById(id);
    }

    @Override
    public void deleteById(int id) {
        jpa.deleteById(id);
    }

    @Override
    public List<JornadaLaboral> findAll() {
        return jpa.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<JornadaLaboral> findByMedico(int idMedico) {
        return jpa.findByIdUsuario(idMedico).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<JornadaLaboral> findByMedicoYDia(int idMedico, String diaSemana) {
        return jpa.findByIdUsuarioAndDiaSemana(idMedico, diaSemana)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    // ── Mapeos ────────────────────────────────────────────────────────────────

    private JornadaLaboral toDomain(JornadaLaboralEntity e) {
        JornadaLaboral j = new JornadaLaboral();
        j.setIdJornada(e.getIdJornada());
        j.setDiaSemana(e.getDiaSemana());
        j.setHoraInicio(e.getHoraInicio());
        j.setHoraFin(e.getHoraFin());
        j.setIdEstado(e.getIdEstado());
        j.setIdUsuario(e.getIdUsuario());
        j.setDuracionEstimadaAtencion(e.getDuracionEstimadaAtencion());
        return j;
    }

    private JornadaLaboralEntity toEntity(JornadaLaboral j) {
        JornadaLaboralEntity e = new JornadaLaboralEntity();
        e.setIdJornada(j.getIdJornada());
        e.setDiaSemana(j.getDiaSemana());
        e.setHoraInicio(j.getHoraInicio());
        e.setHoraFin(j.getHoraFin());
        e.setIdEstado(j.getIdEstado());
        e.setIdUsuario(j.getIdUsuario());
        e.setDuracionEstimadaAtencion(j.getDuracionEstimadaAtencion());
        return e;
    }
}
