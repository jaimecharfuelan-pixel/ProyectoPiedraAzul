package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.dto.CrearPersonaAdminDTO;
import com.proyecto.microservicio_usuarios.modelo.MedicoTerapista;
import com.proyecto.microservicio_usuarios.modelo.Paciente;
import com.proyecto.microservicio_usuarios.modelo.Persona;
import com.proyecto.microservicio_usuarios.modelo.Rol;
import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioMedicoTerapista;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioPaciente;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioPersona;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioRol;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioUsuario;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioPersona {

    private final RepositorioPersona repoPersona;
    private final RepositorioMedicoTerapista repoMedico;
    private final RepositorioPaciente repoPaciente;
    private final RepositorioUsuario repoUsuario;
    private final RepositorioRol repoRol;

    @PersistenceContext
    private EntityManager em;

    public ServicioPersona(RepositorioPersona repoPersona,
                           RepositorioMedicoTerapista repoMedico,
                           RepositorioPaciente repoPaciente,
                           RepositorioUsuario repoUsuario,
                           RepositorioRol repoRol) {
        this.repoPersona = repoPersona;
        this.repoMedico = repoMedico;
        this.repoPaciente = repoPaciente;
        this.repoUsuario = repoUsuario;
        this.repoRol = repoRol;
    }

    /** Migración: activa todas las personas existentes que no tengan activo seteado. */
    @PostConstruct
    @Transactional
    public void migrarActivoExistentes() {
        try {
            em.createNativeQuery("UPDATE persona SET activo = true WHERE activo IS NULL")
              .executeUpdate();
        } catch (Exception e) {
            // La columna puede no existir aún en el primer arranque — Hibernate la crea después
            System.out.println("[INFO] Migración activo omitida: " + e.getMessage());
        }
    }

    public Persona crearPersona(Persona persona) {
        if (persona.getCedulaCiudadania() == null || persona.getCedulaCiudadania().isBlank()
                || persona.getNombre() == null || persona.getNombre().isBlank()
                || persona.getApellido() == null || persona.getApellido().isBlank()) {
            throw new IllegalArgumentException("Cédula, nombre y apellido son obligatorios.");
        }
        return repoPersona.save(persona);
    }

    /**
     * Crea usuario + persona (paciente por defecto) + rol desde el panel admin.
     */
    @Transactional
    public Persona crearPersonaAdmin(CrearPersonaAdminDTO dto) {
        if (dto.getCedulaCiudadania() == null || dto.getCedulaCiudadania().isBlank()
                || dto.getNombre() == null || dto.getNombre().isBlank()
                || dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new IllegalArgumentException("Cédula, nombre y apellido son obligatorios.");
        }
        String loginUsuario = dto.getUsuarioLogin() != null && !dto.getUsuarioLogin().isBlank()
                ? dto.getUsuarioLogin() : dto.getCedulaCiudadania();
        String clave = dto.getContrasena() != null && !dto.getContrasena().isBlank()
                ? dto.getContrasena() : dto.getCedulaCiudadania();

        if (repoUsuario.existsByUsuario(loginUsuario)) {
            throw new IllegalArgumentException("El nombre de usuario '" + loginUsuario + "' ya existe.");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsuario(loginUsuario);
        usuario.setContrasena(clave);
        Usuario usuarioGuardado = repoUsuario.save(usuario);

        // Crear persona como Paciente (subtipo concreto)
        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setCedulaCiudadania(dto.getCedulaCiudadania());
        paciente.setCelular(dto.getCelular());
        paciente.setCorreo(dto.getCorreo());
        paciente.setIdGenero(dto.getIdGenero());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setIdUsuario(usuarioGuardado.getIdUsuario());
        paciente.setIdEstado(2); // Activo
        paciente.setActivo(true);
        Paciente guardado = repoPaciente.save(paciente);

        // Asignar rol
        String rolNombre = dto.getRol() != null && !dto.getRol().isBlank() ? dto.getRol() : "Paciente";
        Rol rol = new Rol();
        rol.setNombre(rolNombre);
        rol.setIdUsuario(usuarioGuardado.getIdUsuario());
        repoRol.save(rol);

        return guardado;
    }

    public boolean editarPersona(Persona persona) {
        Optional<Persona> opt = repoPersona.findById(persona.getIdPersona());
        if (opt.isEmpty()) return false;
        Persona existente = opt.get();
        existente.setNombre(persona.getNombre());
        existente.setApellido(persona.getApellido());
        existente.setCedulaCiudadania(persona.getCedulaCiudadania());
        existente.setCelular(persona.getCelular());
        existente.setCorreo(persona.getCorreo());
        existente.setIdGenero(persona.getIdGenero());
        existente.setFechaNacimiento(persona.getFechaNacimiento());
        if (persona.getIdEstado() != null) existente.setIdEstado(persona.getIdEstado());
        repoPersona.save(existente);
        return true;
    }

    /** Edita solo los campos presentes en el mapa, sin tocar el tipo JPA ni el estado. */
    public boolean editarPersonaCampos(int id, java.util.Map<String, Object> campos) {
        Optional<Persona> opt = repoPersona.findById(id);
        if (opt.isEmpty()) return false;
        Persona p = opt.get();
        if (campos.containsKey("nombre"))           p.setNombre((String) campos.get("nombre"));
        if (campos.containsKey("apellido"))         p.setApellido((String) campos.get("apellido"));
        if (campos.containsKey("cedulaCiudadania")) p.setCedulaCiudadania((String) campos.get("cedulaCiudadania"));
        if (campos.containsKey("celular"))          p.setCelular((String) campos.get("celular"));
        if (campos.containsKey("correo"))           p.setCorreo((String) campos.get("correo"));
        if (campos.containsKey("idGenero") && campos.get("idGenero") != null)
            p.setIdGenero(((Number) campos.get("idGenero")).intValue());
        if (campos.containsKey("fechaNacimiento") && campos.get("fechaNacimiento") != null)
            p.setFechaNacimiento(java.time.LocalDate.parse(campos.get("fechaNacimiento").toString()));
        if (campos.containsKey("idEstado") && campos.get("idEstado") != null)
            p.setIdEstado(((Number) campos.get("idEstado")).intValue());
        repoPersona.save(p);
        return true;
    }

    public Optional<Persona> buscarPorId(int idPersona) {
        return repoPersona.findById(idPersona);
    }

    public boolean inactivarPersona(int idPersona) {
        Optional<Persona> opt = repoPersona.findById(idPersona);
        if (opt.isEmpty()) return false;
        Persona p = opt.get();
        p.setActivo(false);
        p.setIdEstado(1); // 1 = Inactivo en dominio_estado
        repoPersona.save(p);
        return true;
    }

    public List<Persona> listarPersonas() {
        return repoPersona.findByActivoTrue();
    }

    public Optional<Persona> buscarPorDocumento(String cedula) {
        return repoPersona.findByCedulaCiudadania(cedula);
    }

    // ─── Médicos ─────────────────────────────────────────────────────────────

    /** RF1/RF2: Lista médicos activos (id_estado = 2 = Activo según datos de prueba). */
    public List<MedicoTerapista> listarMedicosActivos() {
        return repoMedico.findByIdEstado(2);
    }

    public boolean asignarEspecialidad(int idMedico, int idEspecialidad) {
        Optional<MedicoTerapista> opt = repoMedico.findById(idMedico);
        if (opt.isEmpty()) return false;
        MedicoTerapista medico = opt.get();
        medico.setIdEspecialidad(idEspecialidad);
        repoMedico.save(medico);
        return true;
    }

    // ─── Pacientes ───────────────────────────────────────────────────────────

    public Optional<Paciente> buscarPacientePorId(int idPaciente) {
        return repoPaciente.findById(idPaciente);
    }
}
