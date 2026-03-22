
package com.proyecto.persistencia.repositorios;

import com.proyecto.logica.modelos.JornadaLaboral;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.persistencia.interfaces.IRepositorioJornadaLaboral;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioJornadaLaboral implements IRepositorioJornadaLaboral {

    @Override
    public boolean guardar(JornadaLaboral prmJornada) {
        String sql = "INSERT INTO jornada_laboral (id_medico, hora_inicio, hora_fin, dias_semana, duracion_estimada_atencion) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmJornada.getIdUsuario());
            stmt.setTime(2, Time.valueOf(prmJornada.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(prmJornada.getHoraFin()));
            stmt.setString(4, prmJornada.getDiaSemana());
            // Agregamos la duración que está en tu tabla SQL
            stmt.setInt(5, 30); // Valor por defecto o prmJornada.getDuracion()

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar jornada: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<JornadaLaboral> listar() {
        List<JornadaLaboral> lista = new ArrayList<>();
        // Agregamos el esquema 'esquemaprueba' y usamos minúsculas
        String sql = "SELECT id_jornada, id_medico, hora_inicio, hora_fin, dias_semana FROM esquemaprueba.jornada_laboral";
        
        try (Connection con = ConexionBD.getInstance();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapearResultSetAJornada(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en RepositorioJornadaLaboral: " + e.getMessage());
        }
        return lista;
    }

    // ... (buscar y actualizar seguirían la misma lógica de comillas si es necesario)

    private JornadaLaboral mapearResultSetAJornada(ResultSet rs) throws SQLException {
        JornadaLaboral jornada = new JornadaLaboral();
        jornada.setIdJornada(rs.getInt("id_jornada"));
        jornada.setIdUsuario(rs.getInt("id_medico")); 
        jornada.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
        jornada.setHoraFin(rs.getTime("hora_fin").toLocalTime());
        // Ajustamos al nombre exacto de la columna en tu script SQL: dias_semana
        jornada.setDiaSemana(rs.getString("dias_semana"));
        
        return jornada;
    }
    
    // Implementación de buscar necesaria para completar la interfaz
    @Override
    public JornadaLaboral buscar(int prmIdJornada) {
        String sql = "SELECT * FROM \"Jornada_Laboral\" WHERE id_jornada = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, prmIdJornada);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapearResultSetAJornada(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean actualizar(JornadaLaboral prmJornada) {
        String sql = "UPDATE \"Jornada_Laboral\" SET id_medico=?, hora_inicio=?, hora_fin=?, dias_semana=? WHERE id_jornada=?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, prmJornada.getIdUsuario());
            stmt.setTime(2, Time.valueOf(prmJornada.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(prmJornada.getHoraFin()));
            stmt.setString(4, prmJornada.getDiaSemana());
            stmt.setInt(5, prmJornada.getIdJornada());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    @Override
    public boolean inactivar(int prmIdJornada) {
        // Dado que en tu tabla SQL de Jornada_Laboral no hay una columna id_estado,
        // la inactivación se maneja generalmente eliminando el registro o 
        // podrías agregar la columna id_estado a la tabla en Postgres.
        // Por ahora, lo implementaremos como una eliminación:
        String sql = "DELETE FROM Jornada_Laboral WHERE id_jornada = ?";
        try (Connection con = ConexionBD.getInstance();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, prmIdJornada);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
