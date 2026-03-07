package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioCitas;
import com.proyecto.logica.modelos.Cita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCitasPostgres implements IRepositorioCitas {
    private Connection attConexion;

    public RepositorioCitasPostgres(Connection prmConexion) {
        this.attConexion = prmConexion;
    }

    @Override
    public boolean guardar(Cita prmCita) {
        String sql = "INSERT INTO cita (fecha, hora, id_medico, id_paciente) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = attConexion.prepareStatement(sql)) {
            ps.setObject(1, prmCita.getFecha());
            ps.setObject(2, prmCita.getHora());
            ps.setString(3, prmCita.getMedico().getDocumento());
            ps.setString(4, prmCita.getPaciente().getDocumento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Cita> buscarPorMedicoFecha(String prmIdMedico, LocalDate prmFecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM cita WHERE id_medico = ? AND fecha = ?";
        try (PreparedStatement ps = attConexion.prepareStatement(sql)) {
            ps.setString(1, prmIdMedico);
            ps.setObject(2, prmFecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Creación incompleta dada la necesidad de instancias complejas, basta para el
                    // esqueleto JDBC
                    citas.add(
                            new Cita(rs.getDate("fecha").toLocalDate(), rs.getTime("hora").toLocalTime(), null, null));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
}
