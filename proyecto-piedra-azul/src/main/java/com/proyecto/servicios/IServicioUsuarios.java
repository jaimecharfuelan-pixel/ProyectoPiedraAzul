package com.proyecto.servicios;

import com.proyecto.modelo.Persona;

public interface IServicioUsuarios {
    boolean registrarPersona(Persona p);

    boolean modificarPersona(Persona p);
}
