package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioUsuarios;
import com.proyecto.persistencia.interfaces.IRepositorioPersona;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Persona;

public class ServicioUsuariosImpl implements IServicioUsuarios {
    private IRepositorioPersona attRepositorioPersona;

    public ServicioUsuariosImpl(IRepositorioPersona prmRepositorio) {
        this.attRepositorioPersona = prmRepositorio;
    }

    @Override
    public boolean registrarPaciente(Paciente prmPaciente) {
        return attRepositorioPersona.guardar(prmPaciente);
    }

    @Override
    public Persona buscarPorDocumento(String prmDocumento) {
        return attRepositorioPersona.buscarPorDoc(prmDocumento);
    }

    @Override
    public Persona autenticar(String prmUsuario, String prmClave) {
        // Lógica de validación de clave no contemplada estrictamente en Repositorio
        // según el diagrama
        // Se puede buscar por documento (usuario) y validar
        throw new UnsupportedOperationException("Lógica de autenticación pendiente");
    }
}
