package com.proyecto.servicios;

import com.proyecto.datos.IRepositorioPersona;

public class ServicioAutenticacionImpl implements IServicioAutenticacion {
    private IRepositorioPersona repoPersona;

    public ServicioAutenticacionImpl() {
    }

    @Override
    public Object autenticar(String user, String pass) {
        throw new UnsupportedOperationException("No implementada");
    }
}
