package com.proyecto.logica.servicios;



import com.proyecto.logica.interfaces.IServicioPaciente;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Usuario;
import com.proyecto.persistencia.interfaces.IRepositorioUsuario;

public class ServicioPaciente implements IServicioPaciente {

    private final IRepositorioUsuario repoUsuario;
    // Necesitarás el repositorio de Persona para guardar los datos básicos
    // private final IRepositorioPersona repoPersona; 

    public ServicioPaciente(IRepositorioUsuario prmRepoUsuario) {
        this.repoUsuario = prmRepoUsuario;
    }

    @Override
    public boolean registrarPaciente(Paciente prmPaciente, Usuario prmUsuario) {
        // 1. Primero guardamos el Usuario para obtener su ID
        int idUsuarioGenerado = repoUsuario.guardar(prmUsuario);
        
        if (idUsuarioGenerado > 0) {
            prmPaciente.setIdUsuario(idUsuarioGenerado);
            // 2. Aquí llamarías a repoPersona.guardar(prmPaciente) 
            // y luego a repoPaciente.guardar(...)
            return true; 
        }
        return false;
    }
}