package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioPersona;
import com.proyecto.logica.modelos.Persona;
import com.proyecto.logica.modelos.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositorioPersonaPostgres implements IRepositorioPersona {
    private Connection attConexion;

    public RepositorioPersonaPostgres(Connection prmConexion) {
        this.attConexion = prmConexion;
    }

    @Override
    public boolean guardar(Persona prmPersona) {
        String sql = "INSERT INTO persona (documento, nombre, apellido, celular, genero, fecha_nacimiento, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = attConexion.prepareStatement(sql)) {
            ps.setString(1, prmPersona.getDocumento());
            ps.setString(2, prmPersona.getNombre());
            ps.setString(3, prmPersona.getApellido());
            ps.setString(4, prmPersona.getCelular());
            ps.setString(5, prmPersona.getGenero());
            ps.setObject(6, prmPersona.getFechaNacimiento());
            ps.setString(7, prmPersona.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Persona buscarPorDoc(String prmDocumento) {
        String sql = "SELECT * FROM persona WHERE documento = ?";
        try (PreparedStatement ps = attConexion.prepareStatement(sql)) {
            ps.setString(1, prmDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Paciente(
                            rs.getString("documento"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("celular"),
                            rs.getString("genero"),
                            rs.getDate("fecha_nacimiento").toLocalDate(),
                            rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
