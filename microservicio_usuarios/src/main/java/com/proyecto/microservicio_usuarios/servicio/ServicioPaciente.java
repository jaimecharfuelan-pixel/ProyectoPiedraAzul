package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.modelo.Paciente;
import com.proyecto.microservicio_usuarios.modelo.Rol;
import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioPaciente;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioRol;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioPaciente {

    private final RepositorioUsuario repoUsuario;
    private final RepositorioPaciente repoPaciente;
    private final RepositorioRol repoRol;

    public ServicioPaciente(RepositorioUsuario repoUsuario,
                            RepositorioPaciente repoPaciente,
                            RepositorioRol repoRol) {
        this.repoUsuario = repoUsuario;
        this.repoPaciente = repoPaciente;
        this.repoRol = repoRol;
    }

    /**
     * RF3: Registra usuario + persona paciente + rol en una transacción.
     */
    @Transactional
    public Paciente registrarPaciente(Paciente paciente, Usuario usuario) {
        if (repoUsuario.existsByUsuario(usuario.getUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }
        Usuario usuarioGuardado = repoUsuario.save(usuario);
        paciente.setIdUsuario(usuarioGuardado.getIdUsuario());
        paciente.setIdEstado(2); // Activo
        Paciente pacienteGuardado = repoPaciente.save(paciente);

        Rol rol = new Rol();
        rol.setNombre("Paciente");
        rol.setIdUsuario(usuarioGuardado.getIdUsuario());
        repoRol.save(rol);

        return pacienteGuardado;
    }

    public List<Paciente> listarPacientes() {
        return repoPaciente.findAll();
    }

    public Optional<Paciente> buscarPorId(int idPaciente) {
        return repoPaciente.findById(idPaciente);
    }
}
