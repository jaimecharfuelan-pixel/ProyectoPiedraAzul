package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.Cita;

import java.time.LocalDate;
import java.util.List;

public interface IServicioCitas {

    boolean guardar(Cita cita);

    List<Cita> buscarPorMedicoFecha(String idMedico, LocalDate fecha);

}