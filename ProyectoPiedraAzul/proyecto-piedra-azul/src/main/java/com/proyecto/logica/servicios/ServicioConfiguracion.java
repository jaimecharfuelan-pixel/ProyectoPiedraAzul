package com.proyecto.logica.servicios;
import com.proyecto.logica.interfaces.IServicioConfiguracion;
import com.proyecto.logica.modelos.JornadaLaboral;
import com.proyecto.persistencia.interfaces.IRepositorioJornadaLaboral;
import java.util.List;


public class ServicioConfiguracion implements IServicioConfiguracion {

    private final IRepositorioJornadaLaboral repoJornada;

    public ServicioConfiguracion(IRepositorioJornadaLaboral prmRepoJornada) {
        this.repoJornada = prmRepoJornada;
    }

    /**
     * Crea o asigna un nuevo turno/jornada a un médico
     */
    @Override
    public boolean configurarDisponibilidadMedico(JornadaLaboral prmJornada) {
        // Validación de negocio: Que la hora de inicio sea antes que la de fin
        if (prmJornada.getHoraInicio().isAfter(prmJornada.getHoraFin())) {
            System.err.println("Error: La hora de inicio no puede ser posterior a la de fin.");
            return false;
        }
        
        // Si el ID es 0 o -1, es una jornada nueva, si no, es una edición
        if (prmJornada.getIdJornada() <= 0) {
            return repoJornada.guardar(prmJornada);
        } else {
            return repoJornada.actualizar(prmJornada);
        }
    }

    /**
     * Permite al admin ver todas las configuraciones actuales de los médicos
     */
    public List<JornadaLaboral> obtenerTodasLasJornadas() {
        return repoJornada.listar();
    }

    /**
     * Permite editar un turno específico (buscándolo primero)
     */
    public boolean editarTurno(JornadaLaboral prmJornada) {
        return repoJornada.actualizar(prmJornada);
    }
    
    /**
     * Eliminar un turno (Inactivar)
     */
    public boolean eliminarTurno(int prmIdJornada) {
        return repoJornada.inactivar(prmIdJornada);
    }

    
}
