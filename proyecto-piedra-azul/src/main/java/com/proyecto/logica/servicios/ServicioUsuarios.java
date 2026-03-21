package com.proyecto.logica.servicios;

import com.proyecto.persistencia.interfaces.IRepositorioPersona;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Persona;

public class ServicioUsuarios {

    private IRepositorioPersona attRepositorioPersona;

    public ServicioUsuarios(IRepositorioPersona prmRepositorio) {
        this.attRepositorioPersona = prmRepositorio;
    }

    public int registrarPaciente(Paciente prmPaciente) {
        return attRepositorioPersona.guardar(prmPaciente);
    }

    public Persona buscarPorDocumento(String prmDocumento) {
        return attRepositorioPersona.buscarPorDocumento(prmDocumento);
    }

    public Persona autenticar(String prmUsuario, String prmClave) {

        Persona persona = attRepositorioPersona.buscarPorDocumento(prmUsuario);

        if (persona != null) {
            return persona;
        }

        return null;
    }
}