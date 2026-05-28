package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.application.dto.CrearPersonaCommand;
import com.proyecto.microservicio_usuarios.domain.model.Paciente;
import com.proyecto.microservicio_usuarios.domain.model.Persona;
import com.proyecto.microservicio_usuarios.domain.model.Rol;
import com.proyecto.microservicio_usuarios.domain.model.Usuario;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarPersonaPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.PacienteRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.PersonaRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.RolRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GestionarPersonaUseCase implements GestionarPersonaPort {

    private static final Logger log = LoggerFactory.getLogger(GestionarPersonaUseCase.class);

    private final PersonaRepositoryPort personaRepo;
    private final PacienteRepositoryPort pacienteRepo;
    private final UsuarioRepositoryPort usuarioRepo;
    private final RolRepositoryPort rolRepo;

    public GestionarPersonaUseCase(PersonaRepositoryPort personaRepo,
                                   PacienteRepositoryPort pacienteRepo,
                                   UsuarioRepositoryPort usuarioRepo,
                                   RolRepositoryPort rolRepo) {
        this.personaRepo = personaRepo;
        this.pacienteRepo = pacienteRepo;
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
    }

    @Override
    @Transactional
    public Persona crearAdmin(CrearPersonaCommand command) {
        if (command.getCedulaCiudadania() == null || command.getCedulaCiudadania().isBlank()
                || command.getNombre() == null || command.getNombre().isBlank()
                || command.getApellido() == null || command.getApellido().isBlank()) {
            throw new IllegalArgumentException("Cédula, nombre y apellido son obligatorios.");
        }

        if (personaRepo.findByCedula(command.getCedulaCiudadania()).isPresent()) {
            throw new IllegalArgumentException("La cédula '" + command.getCedulaCiudadania() + "' ya existe.");
        }

        String loginUsuario = command.getUsuarioLogin() != null && !command.getUsuarioLogin().isBlank()
                ? command.getUsuarioLogin() : command.getCedulaCiudadania();
        String clave = command.getContrasena() != null && !command.getContrasena().isBlank()
                ? command.getContrasena() : command.getCedulaCiudadania();

        if (usuarioRepo.existsByNombreUsuario(loginUsuario)) {
            throw new IllegalArgumentException("El nombre de usuario '" + loginUsuario + "' ya existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(loginUsuario);
        usuario.setContrasena(clave);
        Usuario usuarioGuardado = usuarioRepo.save(usuario);

        Paciente paciente = new Paciente();
        paciente.setNombre(command.getNombre());
        paciente.setApellido(command.getApellido());
        paciente.setCedulaCiudadania(command.getCedulaCiudadania());
        paciente.setCelular(command.getCelular());
        paciente.setCorreo(command.getCorreo());
        paciente.setIdGenero(command.getIdGenero());
        paciente.setFechaNacimiento(command.getFechaNacimiento());
        paciente.setIdUsuario(usuarioGuardado.getIdUsuario());
        paciente.setIdEstado(2); // Activo
        paciente.setActivo(true);
        Paciente guardado = pacienteRepo.save(paciente);

        String rolNombre = command.getRol() != null && !command.getRol().isBlank()
                ? command.getRol() : "Paciente";
        Rol rol = new Rol();
        rol.setNombre(rolNombre);
        rol.setIdUsuario(usuarioGuardado.getIdUsuario());
        rolRepo.save(rol);

        return guardado;
    }

    @Override
    public boolean editarCampos(int id, Map<String, Object> campos) {
        Optional<Persona> opt = personaRepo.findById(id);
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
            p.setFechaNacimiento(LocalDate.parse(campos.get("fechaNacimiento").toString()));
        if (campos.containsKey("idEstado") && campos.get("idEstado") != null)
            p.setIdEstado(((Number) campos.get("idEstado")).intValue());
        personaRepo.save(p);
        return true;
    }

    @Override
    public boolean inactivar(int id) {
        Optional<Persona> opt = personaRepo.findById(id);
        if (opt.isEmpty()) return false;
        Persona p = opt.get();
        p.setActivo(false);
        p.setIdEstado(1); // Inactivo
        personaRepo.save(p);
        return true;
    }

    @Override
    public Optional<Persona> buscarPorId(int id) {
        return personaRepo.findById(id);
    }

    @Override
    public Optional<Persona> buscarPorDocumento(String cedula) {
        return personaRepo.findByCedula(cedula);
    }

    @Override
    public List<Persona> listar() {
        return personaRepo.findActivos();
    }
}
