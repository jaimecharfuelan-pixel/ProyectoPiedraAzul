package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.domain.model.MedicoTerapista;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarMedicoPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.MedicoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestionarMedicoUseCase implements GestionarMedicoPort {

    private final MedicoRepositoryPort medicoRepo;

    public GestionarMedicoUseCase(MedicoRepositoryPort medicoRepo) {
        this.medicoRepo = medicoRepo;
    }

    @Override
    public List<MedicoTerapista> listarActivos() {
        return medicoRepo.findByEstado(2);
    }

    @Override
    public boolean asignarEspecialidad(int idMedico, int idEspecialidad) {
        return medicoRepo.findById(idMedico)
                .map(m -> {
                    m.setIdEspecialidad(idEspecialidad);
                    medicoRepo.save(m);
                    return true;
                }).orElse(false);
    }
}
