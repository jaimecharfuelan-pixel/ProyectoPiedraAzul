package com.proyecto.logica.interfaces;

import java.util.List;

import com.proyecto.logica.modelos.JornadaLaboral;

public interface IServicioConfiguracion {
    // Configurar jornada y duración de citas
    public boolean configurarDisponibilidadMedico(JornadaLaboral prmJornada);

    public List<JornadaLaboral> obtenerTodasLasJornadas();

}