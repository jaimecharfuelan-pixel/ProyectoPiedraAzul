package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.Paciente;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioPaciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPaciente implements IRepositorioPaciente {

    @Override
    public int guardar(Paciente prmPaciente) {

        // Primero se guarda en Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        int idPersona = repoPersona.guardar(prmPaciente);

        if (idPersona == -1) return -1;

        String sql = "INSERT INTO Paciente (id_persona) VALUES (?)";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idPersona);
            stmt.executeUpdate();

            return idPersona;

        } catch (SQLException e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public Paciente buscarPorId(int prmIdPersona) {

        String sql = "SELECT p.* " +
                     "FROM Persona p " +
                     "JOIN Paciente pa ON p.id_persona = pa.id_persona " +
                     "WHERE p.id_persona = ?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdPersona);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAPaciente(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Paciente> listar() {

        List<Paciente> lista = new ArrayList<>();

        String sql = "SELECT p.* " +
                     "FROM Persona p " +
                     "JOIN Paciente pa ON p.id_persona = pa.id_persona";

        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearResultSetAPaciente(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean actualizar(Paciente prmPaciente) {

        // Solo se actualiza Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        return repoPersona.actualizar(prmPaciente);
    }

    @Override
    public boolean inactivar(int prmIdPersona) {

        // Soft delete desde Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        return repoPersona.inactivar(prmIdPersona);
    }

    private Paciente mapearResultSetAPaciente(ResultSet rs) throws SQLException {

        Paciente p = new Paciente();

        p.setIdPersona(rs.getInt("id_persona"));
        p.setNombre(rs.getString("nombre"));
        p.setCedulaCiudadania(rs.getString("cedula_ciudadania"));
        p.setApellido(rs.getString("apellido"));
        p.setCelular(rs.getString("celular"));

        int idGen = rs.getInt("id_genero");
        p.setIdGenero(rs.wasNull() ? null : idGen);

        Date fecha = rs.getDate("fecha_nacimiento");
        if (fecha != null) {
            p.setFechaNacimiento(fecha.toLocalDate());
        }

        p.setCorreo(rs.getString("correo"));

        int idUsu = rs.getInt("id_usuario");
        p.setIdUsuario(rs.wasNull() ? null : idUsu);

        p.setIdEstado(rs.getInt("id_estado"));

        return p;
    }
}