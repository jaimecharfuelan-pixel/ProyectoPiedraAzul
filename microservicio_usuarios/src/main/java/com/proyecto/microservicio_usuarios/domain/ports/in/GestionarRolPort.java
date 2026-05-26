package com.proyecto.microservicio_usuarios.domain.ports.in;

import com.proyecto.microservicio_usuarios.domain.model.Rol;

import java.util.List;

public interface GestionarRolPort {
    List<Rol> listar();
    List<Rol> listarPorUsuario(int idUsuario);
    Rol asignar(Rol rol);
    boolean eliminar(int idRol);
}
