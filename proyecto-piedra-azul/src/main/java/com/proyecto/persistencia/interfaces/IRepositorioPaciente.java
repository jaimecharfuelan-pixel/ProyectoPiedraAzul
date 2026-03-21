package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Paciente;
import java.util.List;

public interface IRepositorioPaciente {

    boolean guardar(int idPersona);

    Paciente buscar(int idPersona);

    List<Paciente> listar();

    boolean eliminar(int idPersona);

}