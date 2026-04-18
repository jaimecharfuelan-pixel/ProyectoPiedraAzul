package com.proyecto.persistencia.interfaces;

import com.proyecto.logica.modelos.SesionToken;
import java.util.List;

public interface IRepositorioSesionToken {

    // Guarda un nuevo token generado tras un login exitoso
    int guardar(SesionToken token);

    // Busca un token específico por su valor hash (el String del token)
    SesionToken buscarPorHash(String tokenHash);

    SesionToken buscarActivoPorUsuario(int idUsuario);

    List<SesionToken> listarPorUsuario(int idUsuario);

    boolean actualizarEstado(int idToken, int idEstado);

    boolean inactivarExpirados();
    
    boolean esTokenValido(String tokenHash);
}