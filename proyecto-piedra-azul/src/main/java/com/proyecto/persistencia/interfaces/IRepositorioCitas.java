package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Cita;
import java.time.LocalDate;
import java.util.List;

public interface IRepositorioCitas {
    boolean guardar(Cita prmCita);

    List<Cita> buscarPorMedicoFecha(String prmIdMedico, LocalDate prmFecha);
}
