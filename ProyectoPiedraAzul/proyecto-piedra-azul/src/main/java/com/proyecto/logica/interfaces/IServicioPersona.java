package com.proyecto.logica.interfaces;

import com.proyecto.logica.modelos.DominioEspecialidad;
import com.proyecto.logica.modelos.MedicoTerapista;
import com.proyecto.logica.modelos.Persona;
import java.util.List;

public interface IServicioPersona {

    int crearPersona(Persona prmPersona);

    boolean editarPersona(Persona prmPersona);

    boolean inactivarPersona(int prmIdPersona);

    List<Persona> listarPersonas();

    Persona buscarPorDocumento(String prmCedula);

    // Médicos activos (reutilizado en admin y agendador)
    List<MedicoTerapista> listarMedicosActivos();

    boolean asignarEspecialidad(MedicoTerapista prmMedico, int prmIdEspecialidad);

    List<DominioEspecialidad> listarEspecialidades();
}
