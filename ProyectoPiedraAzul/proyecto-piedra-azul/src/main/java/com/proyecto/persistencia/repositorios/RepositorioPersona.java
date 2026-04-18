package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.Persona;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioPersona;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPersona implements IRepositorioPersona {

    @Override
    public int guardar(Persona prmPersona) {
        String sql = "INSERT INTO Persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_persona";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, prmPersona.getNombre());
            stmt.setString(2, prmPersona.getCedulaCiudadania());
            stmt.setString(3, prmPersona.getApellido());
            stmt.setString(4, prmPersona.getCelular());

            if (prmPersona.getIdGenero() != null) stmt.setInt(5, prmPersona.getIdGenero());
            else stmt.setNull(5, Types.INTEGER);

            stmt.setDate(6, Date.valueOf(prmPersona.getFechaNacimiento()));
            stmt.setString(7, prmPersona.getCorreo());

            if (prmPersona.getIdUsuario() != null) stmt.setInt(8, prmPersona.getIdUsuario());
            else stmt.setNull(8, Types.INTEGER);

            stmt.setInt(9, prmPersona.getIdEstado());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_persona");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar persona: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public Persona buscarPorId(int prmIdPersona) {
        String sql = "SELECT * FROM Persona WHERE id_persona = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdPersona);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAPersona(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Persona buscarPorDocumento(String prmCedulaCiudadania) {
        String sql = "SELECT * FROM Persona WHERE cedula_ciudadania = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, prmCedulaCiudadania);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAPersona(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Persona> listar() {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM Persona";
        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearResultSetAPersona(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Persona> listarPorEstado(int prmIdEstado) {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM Persona WHERE id_estado = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdEstado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetAPersona(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizar(Persona prmPersona) {
        String sql = "UPDATE Persona SET nombre=?, cedula_ciudadania=?, apellido=?, celular=?, id_genero=?, "
                   + "fecha_nacimiento=?, correo=?, id_usuario=?, id_estado=? WHERE id_persona=?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, prmPersona.getNombre());
            stmt.setString(2, prmPersona.getCedulaCiudadania());
            stmt.setString(3, prmPersona.getApellido());
            stmt.setString(4, prmPersona.getCelular());

            if (prmPersona.getIdGenero() != null) stmt.setInt(5, prmPersona.getIdGenero());
            else stmt.setNull(5, Types.INTEGER);

            stmt.setDate(6, Date.valueOf(prmPersona.getFechaNacimiento()));
            stmt.setString(7, prmPersona.getCorreo());

            if (prmPersona.getIdUsuario() != null) stmt.setInt(8, prmPersona.getIdUsuario());
            else stmt.setNull(8, Types.INTEGER);

            stmt.setInt(9, prmPersona.getIdEstado());
            stmt.setInt(10, prmPersona.getIdPersona());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean inactivar(int prmIdPersona) {
        // Suponiendo que el estado "Inactivo" es el ID 1
        String sql = "UPDATE Persona SET id_estado = 1 WHERE id_persona = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdPersona);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mapea el ResultSet a un objeto concreto. 
     * Nota: Como Persona es abstracta, se usa una clase anónima para instanciarla.
     */
    private Persona mapearResultSetAPersona(ResultSet rs) throws SQLException {
        Persona p = new Persona() {}; // Instancia anónima de la clase abstracta

        p.setIdPersona(rs.getInt("id_persona"));
        p.setNombre(rs.getString("nombre"));
        p.setCedulaCiudadania(rs.getString("cedula_ciudadania"));
        p.setApellido(rs.getString("apellido"));
        p.setCelular(rs.getString("celular"));
        
        int idGen = rs.getInt("id_genero");
        p.setIdGenero(rs.wasNull() ? null : idGen);
        
        p.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        p.setCorreo(rs.getString("correo"));
        
        int idUsu = rs.getInt("id_usuario");
        p.setIdUsuario(rs.wasNull() ? null : idUsu);
        
        p.setIdEstado(rs.getInt("id_estado"));

        return p;
    }
}