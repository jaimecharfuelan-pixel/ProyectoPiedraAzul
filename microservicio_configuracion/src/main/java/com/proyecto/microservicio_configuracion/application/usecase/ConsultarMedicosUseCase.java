package com.proyecto.microservicio_configuracion.application.usecase;

import com.proyecto.microservicio_configuracion.domain.model.MedicoResumen;
import com.proyecto.microservicio_configuracion.domain.ports.in.ConsultarMedicosPort;
import com.proyecto.microservicio_configuracion.domain.ports.out.MedicoClientPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultarMedicosUseCase implements ConsultarMedicosPort {

    private final MedicoClientPort medicoClient;

    public ConsultarMedicosUseCase(MedicoClientPort medicoClient) {
        this.medicoClient = medicoClient;
    }

    @Override
    public List<MedicoResumen> listarActivos() {
        return medicoClient.listarMedicosActivos();
    }
}
