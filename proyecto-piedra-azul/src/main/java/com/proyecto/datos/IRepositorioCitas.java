package com.proyecto.datos;

import com.proyecto.modelo.Cita;
import java.util.List;

public interface IRepositorioCitas {
    boolean guardar(Cita c);

    List<Cita> buscar(Object filtro);
}
