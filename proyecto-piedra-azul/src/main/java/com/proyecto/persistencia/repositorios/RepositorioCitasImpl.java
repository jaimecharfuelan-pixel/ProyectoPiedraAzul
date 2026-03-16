package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.logica.modelos.Cita;
import com.proyecto.logica.modelos.Paciente;
import com.proyecto.logica.modelos.Medico;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCitasImpl implements IRepositorioCitas {

    @Override
    public boolean guardar(Cita prmCita) {

        String sql = "INSERT INTO cita(idpaciente,idmedico,fecha,horainicio,horafin) VALUES (?,?,?,?,?)";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, prmCita.getPaciente().getId());
            ps.setInt(2, prmCita.getMedico().getId());

            ps.setDate(3, java.sql.Date.valueOf(prmCita.getFechaHora().toLocalDate()));
            ps.setTime(4, java.sql.Time.valueOf(prmCita.getFechaHora().toLocalTime()));

            // ejemplo: cita de 1 hora
            ps.setTime(5, java.sql.Time.valueOf(prmCita.getFechaHora().toLocalTime().plusHours(1)));

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean eliminar(int prmIdCita) {

        String sql = "DELETE FROM Cita WHERE idcita=?";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, prmIdCita);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Cita> listar() {

        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT * FROM cita";

        try {

            Connection conn = ConexionBD.getInstance();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Paciente paciente = new Paciente(
                        rs.getInt("idpaciente"),
                        "", "", "", "", "",
                        null, "");

                Medico medico = new Medico(
                        rs.getInt("idmedico"),
                        "", "", "", "", "",
                        null, "", "");

                LocalDateTime fechaHora = rs.getDate("fecha").toLocalDate()
                        .atTime(rs.getTime("horainicio").toLocalTime());

                Cita cita = new Cita(
                        rs.getInt("idcita"),
                        paciente,
                        medico,
                        fechaHora,
                        "AGENDADA");

                lista.add(cita);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}