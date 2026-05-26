package com.proyecto.microservicio_usuarios.domain.ports.in;

import com.proyecto.microservicio_usuarios.domain.model.Usuario;

import java.util.List;

public interface GestionarUsuarioPort {
    boolean registrar(Usuario usuario);
    boolean editar(Usuario usuario);
    boolean eliminar(int idUsuario);
    List<Usuario> listar();
}
