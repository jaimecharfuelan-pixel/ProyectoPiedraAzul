package com.proyecto.microservicio_configuracion.application.usecase;

import com.proyecto.microservicio_configuracion.domain.model.Especialidad;
import com.proyecto.microservicio_configuracion.domain.ports.in.ConsultarEspecialidadPort;
import com.proyecto.microservicio_configuracion.domain.ports.out.EspecialidadRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultarEspecialidadUseCase implements ConsultarEspecialidadPort {

    private final EspecialidadRepositoryPort especialidadRepo;

    public ConsultarEspecialidadUseCase(EspecialidadRepositoryPort especialidadRepo) {
        this.especialidadRepo = especialidadRepo;
    }

    @Override
    public List<Especialidad> listar() {
        return especialidadRepo.findAll();
    }

    @Override
    public Optional<Especialidad> buscarPorId(int id) {
        return especialidadRepo.findById(id);
    }
}
