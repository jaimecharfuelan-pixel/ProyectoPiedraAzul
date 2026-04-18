package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.Cita;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioCitas;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCitas implements IRepositorioCitas {

    @Override
    public int guardar(Cita prmCita) {

        String sql = "INSERT INTO Cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id_cita";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmCita.getIdPaciente());
            stmt.setInt(2, prmCita.getIdMedico());
            stmt.setDate(3, Date.valueOf(prmCita.getFecha()));
            stmt.setTime(4, Time.valueOf(prmCita.getHoraInicio()));
            stmt.setTime(5, Time.valueOf(prmCita.getHoraFin()));

            if (prmCita.getIdEstadoCita() != null)
                stmt.setInt(6, prmCita.getIdEstadoCita());
            else
                stmt.setNull(6, Types.INTEGER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_cita");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar cita: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public Cita buscarPorId(int prmIdCita) {

        String sql = "SELECT * FROM Cita WHERE id_cita = ?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdCita);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetACita(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Cita> listar() {

        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT * FROM Cita";

        try (Connection con = ConexionBD.getInstance();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearResultSetACita(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean actualizar(Cita prmCita) {

        String sql = "UPDATE Cita SET id_paciente=?, id_medico=?, fecha=?, hora_inicio=?, hora_fin=?, id_estado_cita=? WHERE id_cita=?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmCita.getIdPaciente());
            stmt.setInt(2, prmCita.getIdMedico());
            stmt.setDate(3, Date.valueOf(prmCita.getFecha()));
            stmt.setTime(4, Time.valueOf(prmCita.getHoraInicio()));
            stmt.setTime(5, Time.valueOf(prmCita.getHoraFin()));

            if (prmCita.getIdEstadoCita() != null)
                stmt.setInt(6, prmCita.getIdEstadoCita());
            else
                stmt.setNull(6, Types.INTEGER);

            stmt.setInt(7, prmCita.getIdCita());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean inactivar(int prmIdCita) {

        // 2 = Cancelada
        String sql = "UPDATE Cita SET id_estado_cita = 2 WHERE id_cita = ?";

        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, prmIdCita);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Cita mapearResultSetACita(ResultSet rs) throws SQLException {

        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        LocalTime horaInicio = rs.getTime("hora_inicio").toLocalTime();
        LocalTime horaFin = rs.getTime("hora_fin").toLocalTime();

        return new Cita(
                rs.getInt("id_cita"),
                rs.getInt("id_paciente"),
                rs.getInt("id_medico"),
                fecha,
                horaInicio,
                horaFin,
                rs.getInt("id_estado_cita")
        );
    }
}