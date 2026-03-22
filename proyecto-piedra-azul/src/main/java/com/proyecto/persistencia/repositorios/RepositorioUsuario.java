package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.Usuario;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioUsuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioUsuario implements IRepositorioUsuario {

    @Override
    public int guardar(Usuario prmUsuario) {
        // Usamos RETURNING para obtener el ID generado por el SERIAL de Postgres
        String sql = "INSERT INTO Usuario (usuario, contrasena) VALUES (?, ?) RETURNING id_usuario";
        
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmUsuario.getUsuario());
            stmt.setString(2, prmUsuario.getContrasena());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public Usuario buscarPorId(int prmIdUsuario) {
        String sql = "SELECT id_usuario, usuario, contrasena FROM Usuario WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Usuario buscarPorNombreUsuario(String prmNombreUsuario) {
        String sql = "SELECT id_usuario, usuario, contrasena FROM Usuario WHERE usuario = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmNombreUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, usuario, contrasena FROM Usuario";
        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapearResultSetAUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizar(Usuario prmUsuario) {
        String sql = "UPDATE Usuario SET usuario = ?, contrasena = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmUsuario.getUsuario());
            stmt.setString(2, prmUsuario.getContrasena());
            stmt.setInt(3, prmUsuario.getIdUsuario());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean inactivar(int prmIdUsuario) {
        String sql = "DELETE FROM Usuario WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean validarCredenciales(String prmUsuario, String prmContrasena) {
        String sql = "SELECT id_usuario FROM Usuario WHERE usuario = ? AND contrasena = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmUsuario);
            stmt.setString(2, prmContrasena);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true si encontró coincidencia
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método privado para convertir una fila de la BD en un objeto Usuario.
     */
    private Usuario mapearResultSetAUsuario(ResultSet rs) throws SQLException {
        Usuario usu = new Usuario();
        usu.setIdUsuario(rs.getInt("id_usuario"));
        usu.setUsuario(rs.getString("usuario"));
        usu.setContrasena(rs.getString("contrasena"));
        return usu;
    }
}