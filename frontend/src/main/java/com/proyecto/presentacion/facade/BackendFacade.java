package com.proyecto.presentacion.facade;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.dto.LoginResponseDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class BackendFacade {

    public LoginResponseDTO login(String usuario, String contrasena) throws Exception {
        String respuesta = ClienteHttp.post("/api/auth/login",
                Map.of("usuario", usuario, "contrasena", contrasena));
        return ClienteHttp.parsear(respuesta, LoginResponseDTO.class);
    }

    public List<MedicoDTO> listarMedicosActivos() throws Exception {
        String json = ClienteHttp.get("/api/medicos/activos");
        return ClienteHttp.parsearLista(json, MedicoDTO.class);
    }

    public PersonaDTO buscarPacientePorDocumento(String cedula) throws Exception {
        try {
            String json = ClienteHttp.get("/api/pacientes/documento/" + cedula);
            return ClienteHttp.parsear(json, PersonaDTO.class);
        } catch (Exception e) {
            // 404 = paciente no existe, devolvemos null para que el llamador lo maneje
            if (e.getMessage() != null && e.getMessage().contains("HTTP 404")) {
                return null;
            }
            throw e;
        }
    }

    public PersonaDTO registrarPaciente(Map<String, Object> body) throws Exception {
        String json = ClienteHttp.post("/api/pacientes", body);
        return ClienteHttp.parsear(json, PersonaDTO.class);
    }

    public String agendarCitaWeb(int idPaciente, int idMedico, LocalDate fecha, LocalTime hora) throws Exception {
        return ClienteHttp.post("/api/citas/web", Map.of(
                "idPaciente", String.valueOf(idPaciente),
                "idMedico", String.valueOf(idMedico),
                "fecha", fecha.toString(),
                "hora", hora.toString()
        ));
    }
}
