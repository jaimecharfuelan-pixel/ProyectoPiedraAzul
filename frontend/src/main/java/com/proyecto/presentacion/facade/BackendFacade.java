package com.proyecto.presentacion.facade;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.dto.CitaDTO;
import com.proyecto.presentacion.dto.JornadaDTO;
import com.proyecto.presentacion.dto.LoginResponseDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;
import com.proyecto.presentacion.dto.RolDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fachada única de acceso al backend.
 * Todos los controladores JavaFX usan SOLO esta clase — nunca ClienteHttp directamente.
 *
 * Responsabilidades:
 * - Construir las URLs y los bodies de cada llamada
 * - Parsear las respuestas JSON a DTOs
 * - Propagar excepciones al controlador para que las muestre al usuario
 */
public class BackendFacade {

    // ── Autenticación ─────────────────────────────────────────────────────────

    public LoginResponseDTO login(String usuario, String contrasena) throws Exception {
        String respuesta = ClienteHttp.post("/api/auth/login",
                Map.of("usuario", usuario, "contrasena", contrasena));
        return ClienteHttp.parsear(respuesta, LoginResponseDTO.class);
    }

    // ── Médicos ───────────────────────────────────────────────────────────────

    public List<MedicoDTO> listarMedicosActivos() throws Exception {
        String json = ClienteHttp.get("/api/medicos/activos");
        return ClienteHttp.parsearLista(json, MedicoDTO.class);
    }

    public void asignarEspecialidad(int idMedico, int idEspecialidad, String token) throws Exception {
        ClienteHttp.putSinBody(
                "/api/medicos/" + idMedico + "/especialidad?idEspecialidad=" + idEspecialidad,
                token);
    }

    // ── Especialidades ────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public List<Map> listarEspecialidades() throws Exception {
        String json = ClienteHttp.get("/api/especialidades");
        return ClienteHttp.parsearLista(json, Map.class);
    }

    // ── Personas ──────────────────────────────────────────────────────────────

    public List<PersonaDTO> listarPersonas() throws Exception {
        String json = ClienteHttp.get("/api/personas");
        return ClienteHttp.parsearLista(json, PersonaDTO.class);
    }

    public PersonaDTO buscarPacientePorDocumento(String cedula) throws Exception {
        try {
            String json = ClienteHttp.get("/api/pacientes/documento/" + cedula);
            return ClienteHttp.parsear(json, PersonaDTO.class);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("HTTP 404")) return null;
            throw e;
        }
    }

    public PersonaDTO buscarPacientePorId(int idPaciente) throws Exception {
        String json = ClienteHttp.get("/api/pacientes/" + idPaciente);
        return ClienteHttp.parsear(json, PersonaDTO.class);
    }

    public PersonaDTO registrarPaciente(Map<String, Object> body) throws Exception {
        String json = ClienteHttp.post("/api/pacientes", body);
        return ClienteHttp.parsear(json, PersonaDTO.class);
    }

    public void editarPersona(int idPersona, Map<String, Object> campos, String token) throws Exception {
        ClienteHttp.put("/api/personas/" + idPersona, campos, token);
    }

    public void crearPersonaAdmin(Map<String, Object> body) throws Exception {
        ClienteHttp.post("/api/personas", body);
    }

    public void inactivarPersona(int idPersona, String token) throws Exception {
        ClienteHttp.delete("/api/personas/" + idPersona, token);
    }

    // ── Roles ─────────────────────────────────────────────────────────────────

    public void asignarRol(int idUsuario, String nombreRol, String token) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("nombre", nombreRol);
        body.put("idUsuario", idUsuario);
        ClienteHttp.postConToken("/api/roles", body, token);
    }

    public List<RolDTO> listarRolesDeUsuario(int idUsuario) throws Exception {
        String json = ClienteHttp.get("/api/roles/usuario/" + idUsuario);
        return ClienteHttp.parsearLista(json, RolDTO.class);
    }

    public void eliminarRol(int idRol, String token) throws Exception {
        ClienteHttp.delete("/api/roles/" + idRol, token);
    }

    // ── Usuarios ──────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public List<Map> listarUsuarios() throws Exception {
        String json = ClienteHttp.get("/api/usuarios");
        return ClienteHttp.parsearLista(json, Map.class);
    }

    public void editarUsuario(int idUsuario, Map<String, Object> body, String token) throws Exception {
        ClienteHttp.put("/api/usuarios/" + idUsuario, body, token);
    }

    // ── Citas ─────────────────────────────────────────────────────────────────

    public List<CitaDTO> listarTodasLasCitas() throws Exception {
        String json = ClienteHttp.get("/api/citas/todas");
        return ClienteHttp.parsearLista(json, CitaDTO.class);
    }

    public List<CitaDTO> listarCitas(LocalDate fecha, Integer idMedico) throws Exception {
        String url;
        if (fecha != null && idMedico != null) {
            url = "/api/citas?fecha=" + fecha + "&idMedico=" + idMedico;
        } else if (fecha != null) {
            url = "/api/citas?fecha=" + fecha;
        } else if (idMedico != null) {
            url = "/api/citas/todas";
        } else {
            url = "/api/citas/todas";
        }
        return ClienteHttp.parsearLista(ClienteHttp.get(url), CitaDTO.class);
    }

    public List<CitaDTO> listarCitasHoy() throws Exception {
        String json = ClienteHttp.get("/api/citas?fecha=" + LocalDate.now());
        return ClienteHttp.parsearLista(json, CitaDTO.class);
    }

    public List<CitaDTO> historialCitasPaciente(int idPaciente) throws Exception {
        String json = ClienteHttp.get("/api/citas/paciente/" + idPaciente + "/historial");
        return ClienteHttp.parsearLista(json, CitaDTO.class);
    }

    public List<CitaDTO> citasFuturasPaciente(int idPaciente) throws Exception {
        String json = ClienteHttp.get("/api/citas/paciente/" + idPaciente + "/futuras");
        return ClienteHttp.parsearLista(json, CitaDTO.class);
    }

    public void crearCitaManual(CitaDTO cita, String token) throws Exception {
        ClienteHttp.postConToken("/api/citas", cita, token);
    }

    public void editarCita(CitaDTO cita, String token) throws Exception {
        ClienteHttp.put("/api/citas/" + cita.getIdCita(), cita, token);
    }

    public void cancelarCita(int idCita, String token) throws Exception {
        ClienteHttp.delete("/api/citas/" + idCita, token);
    }

    public String reagendarCita(int idCita, LocalDate nuevaFecha, LocalTime nuevaHora,
                                 String token) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("nuevaFecha", nuevaFecha.toString());
        body.put("nuevaHora", nuevaHora.toString());
        return ClienteHttp.patch("/api/citas/" + idCita + "/reagendar", body, token);
    }

    public String agendarCitaWeb(int idPaciente, int idMedico, LocalDate fecha,
                                  LocalTime hora) throws Exception {
        return ClienteHttp.post("/api/citas/web", Map.of(
                "idPaciente", String.valueOf(idPaciente),
                "idMedico",   String.valueOf(idMedico),
                "fecha",      fecha.toString(),
                "hora",       hora.toString()
        ));
    }

    // ── Disponibilidad ────────────────────────────────────────────────────────

    public List<LocalTime> consultarDisponibilidad(int idMedico, LocalDate fecha) throws Exception {
        String json = ClienteHttp.get(
                "/api/citas/disponibilidad?idMedico=" + idMedico + "&fecha=" + fecha);
        return ClienteHttp.parsearLista(json, LocalTime.class);
    }

    // ── Jornadas ──────────────────────────────────────────────────────────────

    public List<JornadaDTO> listarJornadas() throws Exception {
        String json = ClienteHttp.get("/api/jornadas");
        return ClienteHttp.parsearLista(json, JornadaDTO.class);
    }

    public List<String> listarDiasConJornada(int idMedico) throws Exception {
        String json = ClienteHttp.get("/api/jornadas/medico/" + idMedico + "/dias");
        return ClienteHttp.parsearLista(json, String.class);
    }

    public List<PersonaDTO> listarPacientes() throws Exception {
        String json = ClienteHttp.get("/api/pacientes");
        return ClienteHttp.parsearLista(json, PersonaDTO.class);
    }
}
