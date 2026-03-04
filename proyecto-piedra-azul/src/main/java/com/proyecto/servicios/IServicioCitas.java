package com.proyecto.servicios;

import com.proyecto.modelo.Cita;
import java.time.LocalDate;
import java.util.List;

public interface IServicioCitas {
    boolean agendarCita(Cita c);

    List<Cita> listarPorMedicoYFecha(String id, LocalDate fecha);

    boolean actualizarHistoriaClinica(int idCita, String texto);
}
