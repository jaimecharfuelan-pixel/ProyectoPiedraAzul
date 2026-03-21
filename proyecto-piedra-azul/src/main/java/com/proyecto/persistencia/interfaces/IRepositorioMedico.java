package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.MedicoTerapista;
import java.util.List;

public interface IRepositorioMedico {

    boolean guardar(int idPersona, int especialidad);

    MedicoTerapista buscar(int idPersona);

    List<MedicoTerapista> listar();

    boolean eliminar(int idPersona);

}