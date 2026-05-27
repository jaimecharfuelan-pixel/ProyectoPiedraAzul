package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.domain.model.Rol;
import com.proyecto.microservicio_usuarios.domain.ports.in.GestionarRolPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.RolRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestionarRolUseCase implements GestionarRolPort {

    private final RolRepositoryPort rolRepo;

    public GestionarRolUseCase(RolRepositoryPort rolRepo) {
        this.rolRepo = rolRepo;
    }

    @Override
    public List<Rol> listar() {
        return rolRepo.findAll();
    }

    @Override
    public List<Rol> listarPorUsuario(int idUsuario) {
        return rolRepo.findByIdUsuario(idUsuario);
    }

    @Override
    public Rol asignar(Rol rol) {
        if (rol.getNombre() == null || rol.getNombre().isBlank() || rol.getIdUsuario() == 0) {
            throw new IllegalArgumentException("Nombre de rol e idUsuario son obligatorios.");
        }
        return rolRepo.save(rol);
    }

    @Override
    public boolean eliminar(int idRol) {
        if (!rolRepo.existsById(idRol)) return false;
        rolRepo.deleteById(idRol);
        return true;
    }
}
