package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.Rol;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioRol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioRol implements IRepositorioRol {

    @Override
    public int guardar(Rol prmRol) {
        String sql = "INSERT INTO Rol (nombre, id_usuario) VALUES (?, ?) RETURNING id_rol";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmRol.getNombre());
            stmt.setInt(2, prmRol.getIdUsuario());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_rol");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar rol: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public Rol buscarPorId(int prmIdRol) {
        String sql = "SELECT id_rol, nombre, id_usuario FROM Rol WHERE id_rol = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdRol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetARol(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Rol> listarPorUsuario(int prmIdUsuario) {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT id_rol, nombre, id_usuario FROM Rol WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetARol(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Rol> listarTodo() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT id_rol, nombre, id_usuario FROM Rol";
        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapearResultSetARol(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizar(Rol prmRol) {
        String sql = "UPDATE Rol SET nombre = ?, id_usuario = ? WHERE id_rol = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, prmRol.getNombre());
            stmt.setInt(2, prmRol.getIdUsuario());
            stmt.setInt(3, prmRol.getIdRol());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean inactivar(int prmIdRol) {
        // Nota: En tu tabla Rol no hay columna 'id_estado', por lo que 'inactivar'
        // usualmente significaría eliminar el registro en este contexto de seguridad.
        // Si prefieres no borrarlo, deberías agregar id_estado a la tabla Rol.
        String sql = "DELETE FROM Rol WHERE id_rol = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdRol);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminarPorUsuario(int prmIdUsuario) {
        String sql = "DELETE FROM Rol WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mapeo de ResultSet a objeto Rol usando los setters con prefijo att
     */
    private Rol mapearResultSetARol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombre(rs.getString("nombre"));
        rol.setIdUsuario(rs.getInt("id_usuario"));
        return rol;
    }
}