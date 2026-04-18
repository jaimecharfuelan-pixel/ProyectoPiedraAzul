package com.proyecto.microservicio_usuarios.servicio;

import com.proyecto.microservicio_usuarios.modelo.Paciente;
import com.proyecto.microservicio_usuarios.modelo.Usuario;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioPaciente;
import com.proyecto.microservicio_usuarios.repositorio.RepositorioUsuario;
import org.springframework.stereotype.Service;

@Service
public class ServicioPaciente {

    private final RepositorioUsuario repoUsuario;
    private final RepositorioPaciente repoPaciente;

    public ServicioPaciente(RepositorioUsuario repoUsuario, RepositorioPaciente repoPaciente) {
        this.repoUsuario = repoUsuario;
        this.repoPaciente = repoPaciente;
    }

    /**
     * Registra el usuario primero, luego asocia el paciente con el id generado.
     */
    public boolean registrarPaciente(Paciente paciente, Usuario usuario) {
        Usuario usuarioGuardado = repoUsuario.save(usuario);
        paciente.setIdUsuario(usuarioGuardado.getIdUsuario());
        repoPaciente.save(paciente);
        return true;
    }
}
