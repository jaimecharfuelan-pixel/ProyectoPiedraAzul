package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.MedicoTerapista;
import java.util.List;

public interface IRepositorioMedicoTerapista {

    boolean guardar(MedicoTerapista prmMedicoTerapista);

    MedicoTerapista buscar(int prmIdPersona);

    List<MedicoTerapista> listar();
    
    List<MedicoTerapista> listarActivos();

    boolean actualizar(MedicoTerapista prmMedicoTerapista);

    boolean inactivar(int prmIdPersona);
}