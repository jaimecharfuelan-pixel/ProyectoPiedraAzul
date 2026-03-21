package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioPersona;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.logica.modelos.Persona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPersonaImpl implements IRepositorioPersona {

    @Override
    public int guardar(Persona persona) {

        String sql = """
                INSERT INTO Persona(nombre, apellido, CC, celular, genero, fechaNacimiento, correo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellido());
            ps.setString(3, persona.getDocumento());
            ps.setString(4, persona.getCelular());
            ps.setString(5, persona.getGenero());
            ps.setDate(6, Date.valueOf(persona.getFechaNacimiento()));
            ps.setString(7, persona.getEmail());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public Persona buscarPorDocumento(String documento) {

        String sql = "SELECT * FROM Persona WHERE CC=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, documento);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Persona(
                        rs.getInt("id"),
                        rs.getString("CC"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("celular"),
                        rs.getString("genero"),
                        rs.getDate("fechaNacimiento").toLocalDate(),
                        rs.getString("correo")) {
                };
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Persona buscarPorId(int id) {

        String sql = "SELECT * FROM Persona WHERE id=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Persona(
                        rs.getInt("id"),
                        rs.getString("CC"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("celular"),
                        rs.getString("genero"),
                        rs.getDate("fechaNacimiento").toLocalDate(),
                        rs.getString("correo")) {
                };
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

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("CC"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("celular"),
                        rs.getString("genero"),
                        rs.getDate("fechaNacimiento").toLocalDate(),
                        rs.getString("correo")) {
                };

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean actualizar(Persona persona) {

        String sql = """
                UPDATE Persona
                SET nombre=?, apellido=?, celular=?, correo=?
                WHERE id=?
                """;

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellido());
            ps.setString(3, persona.getCelular());
            ps.setString(4, persona.getEmail());
            ps.setInt(5, persona.getId());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean eliminar(int id) {

        String sql = "DELETE FROM Persona WHERE id=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}