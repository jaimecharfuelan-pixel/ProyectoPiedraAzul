package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioMedico;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.logica.modelos.MedicoTerapista;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMedicoImpl implements IRepositorioMedico {

    @Override
    public boolean guardar(int idPersona, int especialidad) {

        String sql = "INSERT INTO MedicoTerapista(idPersona, especialidad) VALUES (?,?)";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idPersona);
            ps.setInt(2, especialidad);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public MedicoTerapista buscar(int idPersona) {

        String sql = "SELECT * FROM MedicoTerapista WHERE idPersona=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idPersona);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new MedicoTerapista(
                        idPersona,
                        "", "", "", "", "",
                        null, "",
                        rs.getInt("especialidad"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<MedicoTerapista> listar() {

        List<MedicoTerapista> lista = new ArrayList<>();

        String sql = "SELECT * FROM MedicoTerapista";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                lista.add(
                        new MedicoTerapista(
                                rs.getInt("idPersona"),
                                "", "", "", "", "",
                                null, "",
                                rs.getInt("especialidad")));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean eliminar(int idPersona) {

        String sql = "DELETE FROM MedicoTerapista WHERE idPersona=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idPersona);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}