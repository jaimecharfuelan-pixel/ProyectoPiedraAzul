package com.proyecto.presentacion.controladores;

import com.proyecto.logica.interfaces.IServicioConfiguracion;
import com.proyecto.logica.interfaces.IServicioUsuarios;
import com.proyecto.logica.modelos.Medico;

import java.time.LocalDate;
import java.time.LocalTime;

public class ControladorAdmin {
    private IServicioConfiguracion attServicioConfiguracion;
    private IServicioUsuarios attServicioUsuarios;

    public ControladorAdmin(IServicioConfiguracion prmServicioConfig, IServicioUsuarios prmServicioUsuarios) {
        this.attServicioConfiguracion = prmServicioConfig;
        this.attServicioUsuarios = prmServicioUsuarios;
    }

    public void gestionarUsuario(String prmDocumento, String prmNombre, String prmApellido, String prmCelular,
            String prmGenero, LocalDate prmNacimiento, String prmEmail, String prmUsuario, String prmClave,
            String prmRol) {
        // Lógica
    }

    public void configurarParametros(Medico prmMedico, int prmSemanas, String prmDiasAtencion, LocalTime prmHoraInicio,
            LocalTime prmHoraFin, int prmIntervalo) {
        // Lógica
    }
}
