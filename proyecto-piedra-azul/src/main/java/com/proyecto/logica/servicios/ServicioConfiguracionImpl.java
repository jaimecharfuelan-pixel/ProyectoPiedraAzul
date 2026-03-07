package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioConfiguracion;
import com.proyecto.persistencia.interfaces.IRepositorioConfiguracion;
import com.proyecto.logica.modelos.ConfiguracionAgenda;

public class ServicioConfiguracionImpl implements IServicioConfiguracion {
    private IRepositorioConfiguracion attRepositorioConfig;

    public ServicioConfiguracionImpl(IRepositorioConfiguracion prmRepositorio) {
        this.attRepositorioConfig = prmRepositorio;
    }

    @Override
    public boolean guardarConfiguracion(ConfiguracionAgenda prmConfiguracion) {
        return attRepositorioConfig.guardar(prmConfiguracion);
    }

    @Override
    public ConfiguracionAgenda obtenerConfiguracion(String prmIdMedico) {
        return attRepositorioConfig.buscarPorMedico(prmIdMedico);
    }
}
