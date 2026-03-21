package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Persona;
import java.util.List;

public interface IRepositorioPersona {

    int guardar(Persona persona);

    Persona buscarPorDocumento(String documento);

    Persona buscarPorId(int id);

    List<Persona> listar();

    boolean actualizar(Persona persona);

    boolean eliminar(int id);

}