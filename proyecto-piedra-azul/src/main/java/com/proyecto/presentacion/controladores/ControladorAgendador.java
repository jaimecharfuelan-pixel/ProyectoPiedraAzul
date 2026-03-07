package com.proyecto.presentacion.controladores;

import com.proyecto.logica.interfaces.IServicioCitas;
import com.proyecto.logica.interfaces.IServicioUsuarios;
import com.proyecto.logica.modelos.Medico;

import java.time.LocalDate;
import java.time.LocalTime;

public class ControladorAgendador {
    private IServicioCitas attServicioCitas;
    private IServicioUsuarios attServicioUsuarios;

    public ControladorAgendador(IServicioCitas prmServicioCitas, IServicioUsuarios prmServicioUsuarios) {
        this.attServicioCitas = prmServicioCitas;
        this.attServicioUsuarios = prmServicioUsuarios;
    }

    public void listarCitas(Medico prmMedico, LocalDate prmFecha) {
        // Lógica
    }

    public void agendarCita(String prmDocumento, String prmNombre, String prmApellido, String prmCelular,
            String prmGenero, LocalDate prmNacimiento, Medico prmMedico, LocalTime prmHora) {
        // Lógica
    }
}
