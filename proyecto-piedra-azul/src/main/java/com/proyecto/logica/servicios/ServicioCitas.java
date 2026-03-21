package com.proyecto.logica.servicios;

import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.logica.modelos.Cita;

import java.util.List;

public class ServicioCitas {

    private IRepositorioCitas repositorioCitas;

    public ServicioCitas(IRepositorioCitas prmRepositorio) {
        this.repositorioCitas = prmRepositorio;
    }

    public boolean agendarCita(Cita prmCita) {
        return repositorioCitas.guardar(prmCita);
    }

    public boolean cancelarCita(int prmIdCita) {
        return repositorioCitas.eliminar(prmIdCita);
    }

    public List<Cita> listarCitas() {
        return repositorioCitas.listar();
    }
}