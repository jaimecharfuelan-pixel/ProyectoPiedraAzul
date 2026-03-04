package com.proyecto.datos;

import com.proyecto.modelo.Persona;
import com.proyecto.modelo.Cita;
import java.util.List;

public class RepositorioPostgres implements IRepositorioPersona, IRepositorioCitas {
    private ConexionBD conexion;

    public RepositorioPostgres() {
    }

    @Override
    public boolean guardar(Persona p) {
        throw new UnsupportedOperationException("No implementada");
    }

    @Override
    public Persona buscarPorId(String id) {
        throw new UnsupportedOperationException("No implementada");
    }

    @Override
    public Persona buscarPorUsuario(String user) {
        throw new UnsupportedOperationException("No implementada");
    }

    @Override
    public boolean guardar(Cita c) {
        throw new UnsupportedOperationException("No implementada");
    }

    @Override
    public List<Cita> buscar(Object filtro) {
        throw new UnsupportedOperationException("No implementada");
    }
}
