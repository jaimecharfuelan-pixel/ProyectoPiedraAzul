package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioAgendadorPersona;
import com.proyecto.logica.modelos.Agendador;
import com.proyecto.persistencia.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioAgendadorPersonaImpl implements IRepositorioAgendadorPersona {

    @Override
    public boolean guardar(int idPersona) {

        String sql = "INSERT INTO Agendador(idPersona) VALUES (?)";

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

    @Override
    public boolean eliminar(int idPersona) {

        String sql = "DELETE FROM Agendador WHERE idPersona=?";

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

    @Override
    public List<Agendador> listar() {

        List<Agendador> lista = new ArrayList<>();

        String sql = "SELECT * FROM Agendador";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                lista.add(
                        new Agendador(
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
    public Agendador buscar(int idPersona) {

        String sql = "SELECT * FROM Agendador WHERE idPersona=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idPersona);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Agendador(
                        idPersona,
                        "", "", "", "", "",
                        null, "");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}