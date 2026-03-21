package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Cita;
import java.util.List;

public interface IRepositorioCitas {

    boolean guardar(Cita prmCita);

    boolean eliminar(int prmIdCita);

    List<Cita> listar();

}