package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.DominioEspecialidad;
import java.util.List;

public interface IRepositorioDominioEspecialidad {

    boolean guardar(DominioEspecialidad prmEspecialidad);

    DominioEspecialidad buscar(int prmidEspecialidad);

    List<DominioEspecialidad> listar();

    boolean actualizar(DominioEspecialidad prmEspecialidad);

    boolean inactivar(int prmidEspecialidad);
}