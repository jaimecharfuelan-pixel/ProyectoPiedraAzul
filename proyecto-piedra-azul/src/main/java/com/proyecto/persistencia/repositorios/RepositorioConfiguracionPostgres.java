package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioConfiguracion;
import com.proyecto.logica.modelos.ConfiguracionAgenda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositorioConfiguracionPostgres implements IRepositorioConfiguracion {
    private Connection attConexion;

    public RepositorioConfiguracionPostgres(Connection prmConexion) {
        this.attConexion = prmConexion;
    }

    @Override
    public boolean guardar(ConfiguracionAgenda prmConfiguracion) {
        String sql = "INSERT INTO configuracion_agenda (id_medico, ventana_semanas, dias_atencion, hora_inicio, hora_fin, intervalo_minutos) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = attConexion.prepareStatement(sql)) {
            ps.setString(1, prmConfiguracion.getIdMedico());
            ps.setInt(2, prmConfiguracion.getVentanaSemanas());
            ps.setString(3, prmConfiguracion.getDiasAtencion());
            ps.setObject(4, prmConfiguracion.getHoraInicio());
            ps.setObject(5, prmConfiguracion.getHoraFin());
            ps.setInt(6, prmConfiguracion.getIntervaloMinutos());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ConfiguracionAgenda buscarPorMedico(String prmIdMedico) {
        String sql = "SELECT * FROM configuracion_agenda WHERE id_medico = ?";
        try (PreparedStatement ps = attConexion.prepareStatement(sql)) {
            ps.setString(1, prmIdMedico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ConfiguracionAgenda(
                            rs.getString("id_medico"),
                            rs.getInt("ventana_semanas"),
                            rs.getString("dias_atencion"),
                            rs.getTime("hora_inicio").toLocalTime(),
                            rs.getTime("hora_fin").toLocalTime(),
                            rs.getInt("intervalo_minutos"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
