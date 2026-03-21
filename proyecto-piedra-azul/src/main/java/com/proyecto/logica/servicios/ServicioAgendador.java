package com.proyecto.logica.servicios;

import com.proyecto.persistencia.repositorios.RepositorioCitasImpl;
import com.proyecto.logica.modelos.Cita;

import java.util.List;

public class ServicioAgendador {

    private RepositorioCitasImpl repoCitas;

    public ServicioAgendador() {
        repoCitas = new RepositorioCitasImpl();
    }

    public boolean agendarCita(Cita cita) {

        return repoCitas.guardar(cita);
    }

    public boolean cancelarCita(int idCita) {

        return repoCitas.eliminar(idCita);
    }

    public List<Cita> listarCitas() {

        return repoCitas.listar();
    }
}