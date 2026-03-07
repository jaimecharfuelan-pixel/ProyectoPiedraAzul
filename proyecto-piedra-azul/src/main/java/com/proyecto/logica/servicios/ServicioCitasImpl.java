package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioCitas;
import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.persistencia.interfaces.IRepositorioConfiguracion;
import com.proyecto.logica.modelos.Cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ServicioCitasImpl implements IServicioCitas {
    private IRepositorioCitas attRepoCitas;
    private IRepositorioConfiguracion attRepoConfiguracion;

    public ServicioCitasImpl(IRepositorioCitas prmRepoCitas, IRepositorioConfiguracion prmRepoConfig) {
        this.attRepoCitas = prmRepoCitas;
        this.attRepoConfiguracion = prmRepoConfig;
    }

    @Override
    public boolean agendarCita(Cita prmCita) {
        return attRepoCitas.guardar(prmCita);
    }

    @Override
    public List<Cita> listarCitasMedico(String prmIdMedico, LocalDate prmFecha) {
        return attRepoCitas.buscarPorMedicoFecha(prmIdMedico, prmFecha);
    }

    @Override
    public List<LocalTime> obtenerHorariosDisponibles(String prmIdMedico, LocalDate prmFecha) {
        throw new UnsupportedOperationException("Lógica de intervalos pendientes");
    }
}
