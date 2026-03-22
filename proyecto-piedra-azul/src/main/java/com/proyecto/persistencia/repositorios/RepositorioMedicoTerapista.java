package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.MedicoTerapista;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioMedicoTerapista;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMedicoTerapista implements IRepositorioMedicoTerapista {

    @Override
    public boolean guardar(MedicoTerapista prmMedicoTerapista) {

        // Guardar en Persona primero
        RepositorioPersona repoPersona = new RepositorioPersona();
        int idPersona = repoPersona.guardar(prmMedicoTerapista);

        if (idPersona == -1) return false;

        String sql = "INSERT INTO Medico_Terapista (id_persona, id_especialidad) VALUES (?, ?)";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idPersona);
            stmt.setInt(2, prmMedicoTerapista.getIdEspecialidad());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar médico: " + e.getMessage());
            return false;
        }
    }

    @Override
    public MedicoTerapista buscar(int prmIdPersona) {

        String sql = "SELECT p.*, m.id_especialidad " +
                     "FROM Persona p " +
                     "JOIN Medico_Terapista m ON p.id_persona = m.id_persona " +
                     "WHERE p.id_persona = ?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdPersona);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAMedico(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<MedicoTerapista> listar() {

        List<MedicoTerapista> lista = new ArrayList<>();

        String sql = "SELECT p.*, m.id_especialidad " +
                     "FROM Persona p " +
                     "JOIN Medico_Terapista m ON p.id_persona = m.id_persona";

        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearResultSetAMedico(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<MedicoTerapista> listarActivos() {

        List<MedicoTerapista> lista = new ArrayList<>();

        String sql = "SELECT p.*, m.id_especialidad " +
                     "FROM Persona p " +
                     "JOIN Medico_Terapista m ON p.id_persona = m.id_persona " +
                     "WHERE p.id_estado = 2"; // 2 = Activo

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSetAMedico(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean actualizar(MedicoTerapista prmMedicoTerapista) {

        // Actualizar Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        boolean actualizadoPersona = repoPersona.actualizar(prmMedicoTerapista);

        if (!actualizadoPersona) return false;

        String sql = "UPDATE Medico_Terapista SET id_especialidad=? WHERE id_persona=?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmMedicoTerapista.getIdEspecialidad());
            stmt.setInt(2, prmMedicoTerapista.getIdPersona());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean inactivar(int prmIdPersona) {

        // Manejado desde Persona
        RepositorioPersona repoPersona = new RepositorioPersona();
        return repoPersona.inactivar(prmIdPersona);
    }

    // 🔥 Mapper CORRECTO (manejo de nulls)
    private MedicoTerapista mapearResultSetAMedico(ResultSet rs) throws SQLException {

        Date fecha = rs.getDate("fecha_nacimiento");
        LocalDate fechaNacimiento = (fecha != null) ? fecha.toLocalDate() : null;

        int idGen = rs.getInt("id_genero");
        Integer idGenero = rs.wasNull() ? null : idGen;

        int idUsu = rs.getInt("id_usuario");
        Integer idUsuario = rs.wasNull() ? null : idUsu;

        return new MedicoTerapista(
                rs.getInt("id_persona"),
                rs.getString("nombre"),
                rs.getString("cedula_ciudadania"),
                rs.getString("apellido"),
                rs.getString("celular"),
                idGenero,
                fechaNacimiento,
                rs.getString("correo"),
                idUsuario,
                rs.getInt("id_estado"),
                rs.getInt("id_especialidad")
        );
    }
}