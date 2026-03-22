package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.DominioEspecialidad;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioDominioEspecialidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDominioEspecialidad implements IRepositorioDominioEspecialidad {

    @Override
    public boolean guardar(DominioEspecialidad prmEspecialidad) {

        String sql = "INSERT INTO Dominio_Especialidad (nombre) VALUES (?)";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, prmEspecialidad.getNombre());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar especialidad: " + e.getMessage());
            return false;
        }
    }

    @Override
    public DominioEspecialidad buscar(int prmIdEspecialidad) {

        String sql = "SELECT * FROM Dominio_Especialidad WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdEspecialidad);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<DominioEspecialidad> listar() {

        List<DominioEspecialidad> lista = new ArrayList<>();

        String sql = "SELECT * FROM Dominio_Especialidad";

        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean actualizar(DominioEspecialidad prmEspecialidad) {

        String sql = "UPDATE Dominio_Especialidad SET nombre=? WHERE id_especialidad=?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, prmEspecialidad.getNombre());
            stmt.setInt(2, prmEspecialidad.getIdEspecialidad());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean inactivar(int prmIdEspecialidad) {

        String sql = "DELETE FROM Dominio_Especialidad WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdEspecialidad);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar especialidad: " + e.getMessage());
            return false;
        }
    }

    private DominioEspecialidad mapearResultSet(ResultSet rs) throws SQLException {

        return new DominioEspecialidad(
                rs.getInt("id_especialidad"),
                rs.getString("nombre")
        );
    }
}