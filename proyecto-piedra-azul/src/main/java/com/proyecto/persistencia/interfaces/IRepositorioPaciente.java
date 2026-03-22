package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Paciente;
import java.util.List;

public interface IRepositorioPaciente {

    int guardar(Paciente prmPaciente);

    Paciente buscarPorId(int prmIdPersona);

    List<Paciente> listar();

    boolean actualizar(Paciente prmPaciente);

    boolean inactivar(int prmIdPersona);
}