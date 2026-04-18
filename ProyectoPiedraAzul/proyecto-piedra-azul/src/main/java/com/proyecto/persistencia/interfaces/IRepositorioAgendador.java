package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Agendador;
import java.util.List;

public interface IRepositorioAgendador {

    int guardar(Agendador prmAgendador);

    Agendador buscarPorId(int prmIdPersona);

    List<Agendador> listar();

    boolean actualizar(Agendador prmAgendador);

    boolean inactivar(int prmIdPersona);
}