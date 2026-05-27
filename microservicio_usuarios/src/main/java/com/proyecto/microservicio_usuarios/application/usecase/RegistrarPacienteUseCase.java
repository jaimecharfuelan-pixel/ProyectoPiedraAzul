package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.application.dto.RegistrarPacienteCommand;
import com.proyecto.microservicio_usuarios.domain.model.Paciente;
import com.proyecto.microservicio_usuarios.domain.model.Rol;
import com.proyecto.microservicio_usuarios.domain.model.Usuario;
import com.proyecto.microservicio_usuarios.domain.ports.in.RegistrarPacientePort;
import com.proyecto.microservicio_usuarios.domain.ports.out.PacienteRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.RolRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RegistrarPacienteUseCase implements RegistrarPacientePort {

    private final UsuarioRepositoryPort usuarioRepo;
    private final PacienteRepositoryPort pacienteRepo;
    private final RolRepositoryPort rolRepo;

    public RegistrarPacienteUseCase(UsuarioRepositoryPort usuarioRepo,
                                    PacienteRepositoryPort pacienteRepo,
                                    RolRepositoryPort rolRepo) {
        this.usuarioRepo = usuarioRepo;
        this.pacienteRepo = pacienteRepo;
        this.rolRepo = rolRepo;
    }

    @Override
    @Transactional
    public Paciente registrar(RegistrarPacienteCommand command) {
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
        Paciente pacienteGuardado = pacienteRepo.save(paciente);

        Rol rol = new Rol();
        rol.setNombre("Paciente");
        rol.setIdUsuario(usuarioGuardado.getIdUsuario());
        rolRepo.save(rol);

        return pacienteGuardado;
    }

    @Override
    public List<Paciente> listar() {
        return pacienteRepo.findAll();
    }

    @Override
    public Optional<Paciente> buscarPorId(int id) {
        return pacienteRepo.findById(id);
    }
}
