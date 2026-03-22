package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Rol;
import java.util.List;

public interface IRepositorioRol {

    int guardar(Rol prmRol);

    Rol buscarPorId(int prmIdRol);

    List<Rol> listarPorUsuario(int prmIdUsuario);

    List<Rol> listarTodo();

    boolean actualizar(Rol prmRol);

    boolean inactivar(int prmIdRol);

    boolean eliminarPorUsuario(int prmIdUsuario);
}