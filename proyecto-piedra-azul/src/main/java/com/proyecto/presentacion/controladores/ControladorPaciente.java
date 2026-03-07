package com.proyecto.presentacion.controladores;

import com.proyecto.logica.interfaces.IServicioCitas;
import com.proyecto.logica.modelos.Medico;

import java.time.LocalDate;
import java.time.LocalTime;

public class ControladorPaciente {
    private IServicioCitas attServicioCitas;

    public ControladorPaciente(IServicioCitas prmServicio) {
        this.attServicioCitas = prmServicio;
    }

    public void agendarCita(LocalDate prmFecha, LocalTime prmHora, Medico prmMedico) {
        // Lógica de agendamiento
    }
}
