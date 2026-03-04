package com.proyecto.servicios;

import com.proyecto.datos.IRepositorioPersona;
import com.proyecto.modelo.Persona;

public class ServicioUsuariosImpl implements IServicioUsuarios {
    private IRepositorioPersona repoPersona;

    public ServicioUsuariosImpl() {
    }

    @Override
    public boolean registrarPersona(Persona p) {
        throw new UnsupportedOperationException("No implementada");
    }

    @Override
    public boolean modificarPersona(Persona p) {
        throw new UnsupportedOperationException("No implementada");
    }
}
