package com.proyecto.microservicio_agendamiento.servicio.template;

import com.proyecto.microservicio_agendamiento.mensajeria.PublicadorCitas;
import com.proyecto.microservicio_agendamiento.modelo.Cita;
import com.proyecto.microservicio_agendamiento.repositorio.RepositorioCitas;

public abstract class CitaProcesoTemplate {

    public final boolean guardarYPublicar(Cita cita,
                                          RepositorioCitas repoCitas,
                                          PublicadorCitas publicador) {
        if (cita == null) {
            return false;
        }
        Cita guardada = repoCitas.save(cita);
        publicador.publicarCitaCreada(guardada);
        despuesDeGuardar(guardada);
        return true;
    }

    protected abstract void despuesDeGuardar(Cita cita);
}
