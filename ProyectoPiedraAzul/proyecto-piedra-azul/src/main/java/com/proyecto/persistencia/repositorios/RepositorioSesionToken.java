package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.SesionToken;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioSesionToken;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioSesionToken implements IRepositorioSesionToken {

    @Override
    public int guardar(SesionToken prmToken) {
        String sql = "INSERT INTO Sesion_Token (token_hash, fecha_creacion, fecha_expiracion, id_estado, id_usuario) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING id_token";
        
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmToken.getTokenHash());
            stmt.setTimestamp(2, Timestamp.valueOf(prmToken.getFechaCreacion()));
            stmt.setTimestamp(3, Timestamp.valueOf(prmToken.getFechaExpiracion()));
            stmt.setInt(4, prmToken.getIdEstado());
            stmt.setInt(5, prmToken.getIdUsuario());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_token");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar token: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public SesionToken buscarPorHash(String prmTokenHash) {
        String sql = "SELECT * FROM Sesion_Token WHERE token_hash = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmTokenHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAToken(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SesionToken buscarActivoPorUsuario(int prmIdUsuario) {
        // Buscamos el token que esté en estado 'Activo' (asumiendo ID 2) y no haya expirado
        String sql = "SELECT * FROM Sesion_Token WHERE id_usuario = ? AND id_estado = 2 AND fecha_expiracion > CURRENT_TIMESTAMP LIMIT 1";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAToken(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SesionToken> listarPorUsuario(int prmIdUsuario) {
        List<SesionToken> lista = new ArrayList<>();
        String sql = "SELECT * FROM Sesion_Token WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetAToken(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizarEstado(int prmIdToken, int prmIdEstado) {
        String sql = "UPDATE Sesion_Token SET id_estado = ? WHERE id_token = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdEstado);
            stmt.setInt(2, prmIdToken);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean inactivarExpirados() {
        // Cambia a 'Inactivo' (ID 1) todos los que ya pasaron su fecha de expiración
        String sql = "UPDATE Sesion_Token SET id_estado = 1 WHERE fecha_expiracion < CURRENT_TIMESTAMP AND id_estado = 2";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean esTokenValido(String prmTokenHash) {
        // Un token es válido si existe, está activo y la fecha de expiración es mayor a ahora
        String sql = "SELECT COUNT(*) FROM Sesion_Token WHERE token_hash = ? AND id_estado = 2 AND fecha_expiracion > CURRENT_TIMESTAMP";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmTokenHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private SesionToken mapearResultSetAToken(ResultSet rs) throws SQLException {
        SesionToken token = new SesionToken();
        token.setIdToken(rs.getInt("id_token"));
        token.setTokenHash(rs.getString("token_hash"));
        token.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        token.setFechaExpiracion(rs.getTimestamp("fecha_expiracion").toLocalDateTime());
        token.setIdEstado(rs.getInt("id_estado"));
        token.setIdUsuario(rs.getInt("id_usuario"));
        return token;
    }
}