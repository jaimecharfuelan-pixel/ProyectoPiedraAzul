package com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.adapter;

import com.proyecto.microservicio_usuarios.domain.model.Agendador;
import com.proyecto.microservicio_usuarios.domain.model.MedicoTerapista;
import com.proyecto.microservicio_usuarios.domain.model.Paciente;
import com.proyecto.microservicio_usuarios.domain.model.Persona;
import com.proyecto.microservicio_usuarios.domain.ports.out.PersonaRepositoryPort;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.AgendadorEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.MedicoTerapistaEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.PacienteEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import com.proyecto.microservicio_usuarios.infrastructure.adapters.out.persistence.repository.JpaPersonaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final JpaPersonaRepository jpa;

    public PersonaRepositoryAdapter(JpaPersonaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Persona save(Persona persona) {
        PersonaEntity entity = toEntity(persona);
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Persona> findById(int id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Persona> findByCedula(String cedula) {
        return jpa.findByCedulaCiudadania(cedula).map(this::toDomain);
    }

    @Override
    public List<Persona> findActivos() {
        return jpa.findByActivoTrue().stream().map(this::toDomain).collect(Collectors.toList());
    }

    // ── Mapeos ────────────────────────────────────────────────────────────────

    private Persona toDomain(PersonaEntity e) {
        if (e instanceof PacienteEntity pe) {
            Paciente p = new Paciente();
            copyToDomain(e, p);
            return p;
        } else if (e instanceof MedicoTerapistaEntity me) {
            MedicoTerapista m = new MedicoTerapista();
            copyToDomain(e, m);
            m.setIdEspecialidad(me.getIdEspecialidad());
            return m;
        } else if (e instanceof AgendadorEntity) {
            Agendador a = new Agendador();
            copyToDomain(e, a);
            return a;
        }
        // Fallback: no debería ocurrir con herencia JOINED
        Paciente p = new Paciente();
        copyToDomain(e, p);
        return p;
    }

    private void copyToDomain(PersonaEntity e, Persona p) {
        p.setIdPersona(e.getIdPersona());
        p.setNombre(e.getNombre());
        p.setApellido(e.getApellido());
        p.setCedulaCiudadania(e.getCedulaCiudadania());
        p.setCelular(e.getCelular());
        p.setCorreo(e.getCorreo());
        p.setIdGenero(e.getIdGenero());
        p.setFechaNacimiento(e.getFechaNacimiento());
        p.setIdUsuario(e.getIdUsuario());
        p.setIdEstado(e.getIdEstado());
        p.setActivo(e.isActivo());
    }

    private PersonaEntity toEntity(Persona persona) {
        PersonaEntity e;
        if (persona instanceof Paciente) {
            e = new PacienteEntity();
        } else if (persona instanceof MedicoTerapista mt) {
            MedicoTerapistaEntity me = new MedicoTerapistaEntity();
            me.setIdEspecialidad(mt.getIdEspecialidad());
            e = me;
        } else if (persona instanceof Agendador) {
            e = new AgendadorEntity();
        } else {
            e = new PacienteEntity();
        }
        copyToEntity(persona, e);
        return e;
    }

    private void copyToEntity(Persona p, PersonaEntity e) {
        e.setIdPersona(p.getIdPersona());
        e.setNombre(p.getNombre());
        e.setApellido(p.getApellido());
        e.setCedulaCiudadania(p.getCedulaCiudadania());
        e.setCelular(p.getCelular());
        e.setCorreo(p.getCorreo());
        e.setIdGenero(p.getIdGenero());
        e.setFechaNacimiento(p.getFechaNacimiento());
        e.setIdUsuario(p.getIdUsuario());
        e.setIdEstado(p.getIdEstado());
        e.setActivo(p.isActivo());
    }
}
