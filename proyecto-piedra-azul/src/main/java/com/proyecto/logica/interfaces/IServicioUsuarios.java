package com.proyecto.logica.interfaces;

import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Persona;

public interface IServicioUsuarios {
    boolean registrarPaciente(Paciente prmPaciente);

    Persona buscarPorDocumento(String prmDocumento);

    Persona autenticar(String prmUsuario, String prmClave);
}
