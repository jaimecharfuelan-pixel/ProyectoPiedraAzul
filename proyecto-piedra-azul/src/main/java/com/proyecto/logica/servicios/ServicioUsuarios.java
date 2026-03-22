package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioUsuarios;
import com.proyecto.logica.modelos.Usuario;
import com.proyecto.persistencia.interfaces.IRepositorioUsuario;
import java.util.List;

public class ServicioUsuarios implements IServicioUsuarios {

    private final IRepositorioUsuario repoUsuario;

    public ServicioUsuarios(IRepositorioUsuario prmRepoUsuario) {
        this.repoUsuario = prmRepoUsuario;
    }

    @Override
    public Usuario autenticar(String prmUsuario, String prmContrasena) {
        // 1. Buscamos todos los usuarios (o implementamos un buscarPorNombre en el repo)
        List<Usuario> usuarios = repoUsuario.listar();

        // 2. Filtramos por nombre y contraseña
        return usuarios.stream()
                .filter(u -> u.getUsuario().equals(prmUsuario) && 
                             u.getContrasena().equals(prmContrasena))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean registrarUsuario(Usuario prmUsuario) {
        // Validaciones básicas de negocio
        if (prmUsuario.getUsuario() == null || prmUsuario.getUsuario().isEmpty()) {
            return false;
        }
        return repoUsuario.guardar(prmUsuario) > 0;
    }
}