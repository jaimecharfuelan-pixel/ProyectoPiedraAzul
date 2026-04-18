package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioUsuarios;
import com.proyecto.logica.modelos.Usuario;
import com.proyecto.persistencia.interfaces.IRepositorioUsuario;


public class ServicioUsuarios implements IServicioUsuarios {

    private final IRepositorioUsuario repoUsuario;

    public ServicioUsuarios(IRepositorioUsuario prmRepoUsuario) {
        this.repoUsuario = prmRepoUsuario;
    }

    @Override
    public Usuario autenticar(String prmUsuario, String prmContrasena) {

        Usuario usuario = repoUsuario.buscarPorNombreUsuario(prmUsuario);

        if (usuario == null)
            return null;

        if (!usuario.getContrasena().equals(prmContrasena)) {
            return null;
        }

        return usuario;
    }

    @Override
    public boolean registrarUsuario(Usuario prmUsuario) {
        if (prmUsuario.getUsuario() == null || prmUsuario.getUsuario().isEmpty()) {
            return false;
        }
        return repoUsuario.guardar(prmUsuario) > 0;
    }

    @Override
    public boolean editarUsuario(Usuario prmUsuario) {
        return repoUsuario.actualizar(prmUsuario);
    }

    @Override
    public boolean eliminarUsuario(int prmIdUsuario) {
        return repoUsuario.inactivar(prmIdUsuario);
    }

    @Override
    public java.util.List<Usuario> listarUsuarios() {
        return repoUsuario.listar();
    }
}