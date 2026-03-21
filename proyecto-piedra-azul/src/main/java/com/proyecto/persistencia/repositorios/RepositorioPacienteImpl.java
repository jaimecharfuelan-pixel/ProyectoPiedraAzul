package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioPaciente;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.logica.modelos.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPacienteImpl implements IRepositorioPaciente {

    @Override
    public boolean guardar(int idPersona) {

        String sql = "INSERT INTO Paciente(idPersona) VALUES (?)";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idPersona);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Paciente buscar(int idPersona) {

        String sql = "SELECT * FROM Paciente WHERE idPersona=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idPersona);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Paciente(
                        idPersona,
                        "", "", "", "", "",
                        null, "");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Paciente> listar() {

        List<Paciente> lista = new ArrayList<>();

        String sql = "SELECT * FROM Paciente";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                lista.add(
                        new Paciente(
                                rs.getInt("idPersona"),
                                "", "", "", "", "",
                                null, ""));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean eliminar(int idPersona) {

        String sql = "DELETE FROM Paciente WHERE idPersona=?";

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