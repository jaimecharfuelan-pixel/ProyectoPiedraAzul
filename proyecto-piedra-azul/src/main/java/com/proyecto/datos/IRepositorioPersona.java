package com.proyecto.datos;

import com.proyecto.modelo.Persona;

public interface IRepositorioPersona {
    boolean guardar(Persona p);

    Persona buscarPorId(String id);

    Persona buscarPorUsuario(String user);
}
