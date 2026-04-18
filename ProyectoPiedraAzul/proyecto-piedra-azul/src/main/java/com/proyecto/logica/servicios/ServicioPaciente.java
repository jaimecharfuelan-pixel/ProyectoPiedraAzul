package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioPaciente;
import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Usuario;
import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.persistencia.interfaces.IRepositorioUsuario;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioPaciente implements IServicioPaciente {

    private final IRepositorioUsuario repoUsuario;
    private final IRepositorioCitas   repoCitas;

    public ServicioPaciente(IRepositorioUsuario prmRepoUsuario, IRepositorioCitas prmRepoCitas) {
        this.repoUsuario = prmRepoUsuario;
        this.repoCitas   = prmRepoCitas;
    }

    @Override
    public boolean registrarPaciente(Paciente prmPaciente, Usuario prmUsuario) {
        int idUsuarioGenerado = repoUsuario.guardar(prmUsuario);
        if (idUsuarioGenerado > 0) {
            prmPaciente.setIdUsuario(idUsuarioGenerado);
            return true;
        }
        return false;
    }

    /**
     * Citas pasadas del paciente (fecha anterior a hoy)
     */
    @Override
    public List<Cita> obtenerHistorialCitas(int prmIdPaciente) {
        return repoCitas.listar().stream()
            .filter(c -> c.getIdPaciente() == prmIdPaciente)
            .filter(c -> c.getFecha() != null && c.getFecha().isBefore(LocalDate.now()))
            .collect(Collectors.toList());
    }

    /**
     * Citas futuras del paciente (fecha igual o posterior a hoy)
     */
    @Override
    public List<Cita> obtenerCitasFuturas(int prmIdPaciente) {
        return repoCitas.listar().stream()
            .filter(c -> c.getIdPaciente() == prmIdPaciente)
            .filter(c -> c.getFecha() != null && !c.getFecha().isBefore(LocalDate.now()))
            .collect(Collectors.toList());
    }
}
