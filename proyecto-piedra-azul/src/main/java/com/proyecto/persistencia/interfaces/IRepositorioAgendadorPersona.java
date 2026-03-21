package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Agendador;
import java.util.List;

public interface IRepositorioAgendadorPersona {

    boolean guardar(int idPersona);

    boolean eliminar(int idPersona);

    List<Agendador> listar();

    Agendador buscar(int idPersona);

}