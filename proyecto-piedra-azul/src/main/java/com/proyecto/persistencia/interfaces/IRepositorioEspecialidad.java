package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.DominioEspecialidad;
import java.util.List;

public interface IRepositorioEspecialidad {

    boolean guardar(DominioEspecialidad especialidad);

    DominioEspecialidad buscarPorId(int id);

    List<DominioEspecialidad> listar();

    boolean actualizar(DominioEspecialidad especialidad);

    boolean eliminar(int id);

}