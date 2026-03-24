package com.proyecto.logica.interfaces;

import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Usuario;
import java.util.List;

public interface IServicioPaciente {
    boolean registrarPaciente(Paciente prmPaciente, Usuario prmUsuario);
    List<Cita> obtenerHistorialCitas(int prmIdPaciente);
    List<Cita> obtenerCitasFuturas(int prmIdPaciente);
}