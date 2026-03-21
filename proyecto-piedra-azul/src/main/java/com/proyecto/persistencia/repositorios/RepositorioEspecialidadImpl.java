package com.proyecto.persistencia.repositorios;

import com.proyecto.persistencia.interfaces.IRepositorioEspecialidad;
import com.proyecto.persistencia.ConexionBD;
import com.proyecto.logica.modelos.DominioEspecialidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioEspecialidadImpl implements IRepositorioEspecialidad {

    @Override
    public boolean guardar(DominioEspecialidad esp) {

        String sql = "INSERT INTO DominioEspecialidad(nombre) VALUES (?)";

        try {

            Connection conn = ConexionBD.getInstance();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, esp.getNombre());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    @Override
    public DominioEspecialidad buscarPorId(int id) {

        String sql = "SELECT * FROM DominioEspecialidad WHERE id=?";

        try {

            Connection conn = ConexionBD.getInstance();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                DominioEspecialidad esp = new DominioEspecialidad();

                esp.setId(rs.getInt("id"));
                esp.setNombre(rs.getString("nombre"));

                return esp;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<DominioEspecialidad> listar() {

        List<DominioEspecialidad> lista = new ArrayList<>();

        String sql = "SELECT * FROM DominioEspecialidad";

        try {

            Connection conn = ConexionBD.getInstance();
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                DominioEspecialidad esp = new DominioEspecialidad();

                esp.setId(rs.getInt("id"));
                esp.setNombre(rs.getString("nombre"));

                lista.add(esp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean actualizar(DominioEspecialidad esp) {

        String sql = "UPDATE DominioEspecialidad SET nombre=? WHERE id=?";

        try {

            Connection conn = ConexionBD.getInstance();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, esp.getNombre());
            ps.setInt(2, esp.getId());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {

        String sql = "DELETE FROM DominioEspecialidad WHERE id=?";

        try {

            Connection conn = ConexionBD.getInstance();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
}