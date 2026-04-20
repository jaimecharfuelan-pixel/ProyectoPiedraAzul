package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.dto.MedicoResumenDTO;
import com.proyecto.microservicio_usuarios.modelo.MedicoTerapista;
import com.proyecto.microservicio_usuarios.servicio.ServicioPersona;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final ServicioPersona servicioPersona;

    public MedicoController(ServicioPersona servicioPersona) {
        this.servicioPersona = servicioPersona;
    }

    /**
     * GET /api/medicos/activos
     * Usado por ms-configuracion y ms-agendamiento para listar médicos.
     * RF1, RF2, RF4.
     */
    @GetMapping("/activos")
    public ResponseEntity<List<MedicoResumenDTO>> listarActivos() {
        List<MedicoResumenDTO> medicos = servicioPersona.listarMedicosActivos().stream()
                .map(m -> new MedicoResumenDTO(
                        m.getIdPersona(),
                        m.getNombre(),
                        m.getApellido(),
                        m.getIdEspecialidad()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(medicos);
    }

    /**
     * PUT /api/medicos/{id}/especialidad?idEspecialidad=3
     * RF4: asignar especialidad a un médico.
     */
    @PutMapping("/{id}/especialidad")
    public ResponseEntity<String> asignarEspecialidad(@PathVariable int id,
                                                       @RequestParam int idEspecialidad) {
        if (servicioPersona.asignarEspecialidad(id, idEspecialidad)) {
            return ResponseEntity.ok("Especialidad asignada.");
        }
        return ResponseEntity.notFound().build();
    }
}
