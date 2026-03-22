package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Usuario;
import java.util.List;

public interface IRepositorioUsuario {

    int guardar(Usuario prmUsuario);

    Usuario buscarPorId(int prmIdUsuario);

    Usuario buscarPorNombreUsuario(String prmNombreUsuario);

    List<Usuario> listar();

    boolean actualizar(Usuario prmUsuario);

    boolean inactivar(int prmIdUsuario);
    
    boolean validarCredenciales(String prmUsuario, String prmContrasena);
}