package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Cita;
import java.util.List;

public interface IRepositorioCitas {

    int guardar(Cita prmCita);

    Cita buscarPorId(int prmIdCita);

    List<Cita> listar();

    boolean actualizar(Cita prmCita);

    boolean inactivar(int prmIdCita);
}