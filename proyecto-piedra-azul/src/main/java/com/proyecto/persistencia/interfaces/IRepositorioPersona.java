package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Persona;
import java.util.List;

public interface IRepositorioPersona {

    int guardar(Persona prmPersona);

    Persona buscarPorId(int prmIdPersona);

    Persona buscarPorDocumento(String prmCedulaCiudadania);

    List<Persona> listar(); 

    List<Persona> listarPorEstado(int prmIdEstado); 

    boolean actualizar(Persona prmPersona);

    boolean inactivar(int prmIdPersona); 
}