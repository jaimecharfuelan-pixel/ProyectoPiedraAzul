package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.modelo.MedicoTerapista;
import com.proyecto.microservicio_usuarios.modelo.Persona;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioMedicoTerapista;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioPersona;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioPersona {

    private final RepositorioPersona repoPersona;
    private final RepositorioMedicoTerapista repoMedico;

    public ServicioPersona(RepositorioPersona repoPersona, RepositorioMedicoTerapista repoMedico) {
        this.repoPersona = repoPersona;
        this.repoMedico = repoMedico;
    }

    public Persona crearPersona(Persona persona) {
        if (persona.getCedulaCiudadania() == null || persona.getCedulaCiudadania().isBlank()
                || persona.getNombre() == null || persona.getNombre().isBlank()
                || persona.getApellido() == null || persona.getApellido().isBlank()) {
            throw new IllegalArgumentException("Cédula, nombre y apellido son obligatorios.");
        }
        return repoPersona.save(persona);
    }

    public boolean editarPersona(Persona persona) {
        if (!repoPersona.existsById(persona.getIdPersona())) return false;
        repoPersona.save(persona);
        return true;
    }

    public boolean inactivarPersona(int idPersona) {
        Optional<Persona> opt = repoPersona.findById(idPersona);
        if (opt.isEmpty()) return false;
        Persona p = opt.get();
        p.setIdEstado(0); // inactivo
        repoPersona.save(p);
        return true;
    }

    public List<Persona> listarPersonas() {
        return repoPersona.findAll();
    }

    public Optional<Persona> buscarPorDocumento(String cedula) {
        return repoPersona.findByCedulaCiudadania(cedula);
    }

    public List<MedicoTerapista> listarMedicosActivos() {
        return repoMedico.findByIdEstado(1); // 1 = activo
    }

    public boolean asignarEspecialidad(int idMedico, int idEspecialidad) {
        Optional<MedicoTerapista> opt = repoMedico.findById(idMedico);
        if (opt.isEmpty()) return false;
        MedicoTerapista medico = opt.get();
        medico.setIdEspecialidad(idEspecialidad);
        repoMedico.save(medico);
        return true;
    }
}
