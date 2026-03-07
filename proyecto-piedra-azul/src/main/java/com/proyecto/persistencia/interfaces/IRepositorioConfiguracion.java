package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.ConfiguracionAgenda;

public interface IRepositorioConfiguracion {
    boolean guardar(ConfiguracionAgenda prmConfiguracion);

    ConfiguracionAgenda buscarPorMedico(String prmIdMedico);
}
