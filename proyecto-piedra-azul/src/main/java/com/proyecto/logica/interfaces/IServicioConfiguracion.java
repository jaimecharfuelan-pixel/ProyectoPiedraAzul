package com.proyecto.logica.interfaces;

import com.proyecto.logica.modelos.ConfiguracionAgenda;

public interface IServicioConfiguracion {
    boolean guardarConfiguracion(ConfiguracionAgenda prmConfiguracion);

    ConfiguracionAgenda obtenerConfiguracion(String prmIdMedico);
}
