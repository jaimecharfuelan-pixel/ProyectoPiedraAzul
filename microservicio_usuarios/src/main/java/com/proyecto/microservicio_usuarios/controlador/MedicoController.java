package com.proyecto.microservicio_usuarios.controlador;

import com.proyecto.microservicio_usuarios.dto.MedicoResumenDTO;
import com.proyecto.microservicio_usuarios.servicio.ServicioPersona;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Médicos", description = "Consulta y configuración de médicos activos")
@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final ServicioPersona servicioPersona;

    public MedicoController(ServicioPersona servicioPersona) {
        this.servicioPersona = servicioPersona;
    }

    @Operation(summary = "Listar médicos activos", description = "Devuelve todos los médicos con estado activo. Usado por ms-configuracion y ms-agendamiento.")
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

    @Operation(summary = "Asignar especialidad a médico")
    @PutMapping("/{id}/especialidad")
    public ResponseEntity<String> asignarEspecialidad(@PathVariable int id,
                                                       @RequestParam int idEspecialidad) {
        if (servicioPersona.asignarEspecialidad(id, idEspecialidad)) {
            return ResponseEntity.ok("Especialidad asignada.");
        }
        return ResponseEntity.notFound().build();
    }
}
