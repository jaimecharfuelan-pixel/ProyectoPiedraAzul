package com.proyecto.servicios;

import com.proyecto.datos.IRepositorioCitas;
import com.proyecto.modelo.Cita;
import java.time.LocalDate;
import java.util.List;

public class ServicioCitasImpl implements IServicioCitas {
    private IRepositorioCitas repoCitas;

    public ServicioCitasImpl() {
    }

    @Override
    public boolean agendarCita(Cita c) {
        throw new UnsupportedOperationException("No implementada");
    }

    @Override
    public List<Cita> listarPorMedicoYFecha(String id, LocalDate fecha) {
        throw new UnsupportedOperationException("No implementada");
    }

    @Override
    public boolean actualizarHistoriaClinica(int idCita, String texto) {
        throw new UnsupportedOperationException("No implementada");
    }
}
