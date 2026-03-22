package com.proyecto.logica.interfaces;

import com.proyecto.logica.modelos.Usuario;

public interface IServicioUsuarios {
    /**
     * Valida las credenciales y devuelve el objeto Usuario con sus roles.
     * Retorna null si las credenciales son incorrectas.
     */
    Usuario autenticar(String prmUsuario, String prmContrasena);
    
    /**
     * Registra un nuevo usuario en el sistema.
     */
    boolean registrarUsuario(Usuario prmUsuario);
}