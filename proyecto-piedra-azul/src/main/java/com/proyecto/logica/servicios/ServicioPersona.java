package com.proyecto.logica.servicios;

import com.proyecto.logica.interfaces.IServicioPersona;
import com.proyecto.logica.modelos.DominioEspecialidad;
import com.proyecto.logica.modelos.MedicoTerapista;
import com.proyecto.logica.modelos.Persona;
import com.proyecto.persistencia.interfaces.IRepositorioDominioEspecialidad;
import com.proyecto.persistencia.interfaces.IRepositorioMedicoTerapista;
import com.proyecto.persistencia.interfaces.IRepositorioPersona;
import java.util.List;

public class ServicioPersona implements IServicioPersona {

    private final IRepositorioPersona repoPersona;
    private final IRepositorioMedicoTerapista repoMedico;
    private final IRepositorioDominioEspecialidad repoEspecialidad;

    public ServicioPersona(IRepositorioPersona prmRepoPersona,
                           IRepositorioMedicoTerapista prmRepoMedico,
                           IRepositorioDominioEspecialidad prmRepoEspecialidad) {
        this.repoPersona      = prmRepoPersona;
        this.repoMedico       = prmRepoMedico;
        this.repoEspecialidad = prmRepoEspecialidad;
    }

    @Override
    public int crearPersona(Persona prmPersona) {
        if (prmPersona.getCedulaCiudadania() == null || prmPersona.getCedulaCiudadania().isBlank()
                || prmPersona.getNombre() == null || prmPersona.getNombre().isBlank()
                || prmPersona.getApellido() == null || prmPersona.getApellido().isBlank()) {
            System.err.println("Error: cédula, nombre y apellido son obligatorios.");
            return -1;
        }
        return repoPersona.guardar(prmPersona);
    }

    @Override
    public boolean editarPersona(Persona prmPersona) {
        return repoPersona.actualizar(prmPersona);
    }

    @Override
    public boolean inactivarPersona(int prmIdPersona) {
        return repoPersona.inactivar(prmIdPersona);
    }

    @Override
    public List<Persona> listarPersonas() {
        return repoPersona.listar();
    }

    @Override
    public Persona buscarPorDocumento(String prmCedula) {
        return repoPersona.buscarPorDocumento(prmCedula);
    }

    @Override
    public List<MedicoTerapista> listarMedicosActivos() {
        return repoMedico.listarActivos();
    }

    @Override
    public boolean asignarEspecialidad(MedicoTerapista prmMedico, int prmIdEspecialidad) {
        prmMedico.setIdEspecialidad(prmIdEspecialidad);
        return repoMedico.actualizar(prmMedico);
    }

    @Override
    public List<DominioEspecialidad> listarEspecialidades() {
        return repoEspecialidad.listar();
    }
}
