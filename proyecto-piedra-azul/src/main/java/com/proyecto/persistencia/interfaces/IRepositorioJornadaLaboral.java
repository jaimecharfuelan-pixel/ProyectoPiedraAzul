package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.JornadaLaboral;
import java.util.List;

public interface IRepositorioJornadaLaboral {

    boolean guardar(JornadaLaboral prmJornada);

    JornadaLaboral buscar(int prmIdJornada);

    List<JornadaLaboral> listar();

    boolean actualizar(JornadaLaboral prmJornada);

    boolean inactivar(int prmIdJornada);
}