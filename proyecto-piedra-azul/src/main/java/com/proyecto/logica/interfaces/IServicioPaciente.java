package com.proyecto.logica.interfaces;

import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Usuario;

public interface IServicioPaciente {
    public boolean registrarPaciente(Paciente prmPaciente, Usuario prmUsuario);
}