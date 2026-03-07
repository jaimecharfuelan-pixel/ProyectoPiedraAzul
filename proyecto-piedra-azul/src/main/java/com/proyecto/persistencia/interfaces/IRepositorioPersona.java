package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Persona;

public interface IRepositorioPersona {
    boolean guardar(Persona prmPersona);

    Persona buscarPorDoc(String prmDocumento);
}
