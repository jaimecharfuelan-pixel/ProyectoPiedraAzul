package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.Agendador;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioAgendador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioAgendador implements IRepositorioAgendador {

    public int guardar(Agendador prmAgendador) {

        // Primero se guarda en Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        int idPersona = repoPersona.guardar(prmAgendador);

        if (idPersona == -1) return -1;

        String sql = "INSERT INTO Agendador (id_persona) VALUES (?)";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idPersona);
            stmt.executeUpdate();

            return idPersona;

        } catch (SQLException e) {
            System.err.println("Error al guardar agendador: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public Agendador buscarPorId(int prmIdPersona) {

        String sql = "SELECT p.* FROM Persona p " +
                     "JOIN Agendador a ON p.id_persona = a.id_persona " +
                     "WHERE p.id_persona = ?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdPersona);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAAgendador(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Agendador> listar() {

        List<Agendador> lista = new ArrayList<>();

        String sql = "SELECT p.* FROM Persona p " +
                     "JOIN Agendador a ON p.id_persona = a.id_persona";

        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearResultSetAAgendador(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean actualizar(Agendador prmAgendador) {

        // Solo se actualiza Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        return repoPersona.actualizar(prmAgendador);
    }

    @Override
    public boolean inactivar(int prmIdPersona) {

        // Se maneja desde Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        return repoPersona.inactivar(prmIdPersona);
    }

    private Agendador mapearResultSetAAgendador(ResultSet rs) throws SQLException {

        Agendador a = new Agendador();

        a.setIdPersona(rs.getInt("id_persona"));
        a.setNombre(rs.getString("nombre"));
        a.setCedulaCiudadania(rs.getString("cedula_ciudadania"));
        a.setApellido(rs.getString("apellido"));
        a.setCelular(rs.getString("celular"));

        int idGen = rs.getInt("id_genero");
        a.setIdGenero(rs.wasNull() ? null : idGen);

        Date fecha = rs.getDate("fecha_nacimiento");
        if (fecha != null) {
            a.setFechaNacimiento(fecha.toLocalDate());
        }

        a.setCorreo(rs.getString("correo"));

        int idUsu = rs.getInt("id_usuario");
        a.setIdUsuario(rs.wasNull() ? null : idUsu);

        a.setIdEstado(rs.getInt("id_estado"));

        return a;
    }
}