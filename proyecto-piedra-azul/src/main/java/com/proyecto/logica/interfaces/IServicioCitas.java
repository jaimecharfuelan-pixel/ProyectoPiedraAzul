package com.proyecto.logica.interfaces;

import com.proyecto.logica.modelos.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IServicioCitas {
    boolean agendarCita(Cita prmCita);

    List<Cita> listarCitasMedico(String prmIdMedico, LocalDate prmFecha);

    List<LocalTime> obtenerHorariosDisponibles(String prmIdMedico, LocalDate prmFecha);
}
