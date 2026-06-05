package com.proyecto.presentacion.facade;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.dto.CitaDTO;
import com.proyecto.presentacion.dto.JornadaDTO;
import com.proyecto.presentacion.dto.LoginResponseDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;
import com.proyecto.presentacion.dto.RolDTO;
import com.proyecto.presentacion.dto.ErrorValidacionDTO;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.HistorialCitaDTO;

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

    private String token() {
        return SesionUsuario.getInstancia().getToken();
    }

    // ── Autenticación ─────────────────────────────────────────────────────────

    public LoginResponseDTO login(String usuario, String contrasena) throws Exception {
        String respuesta = ClienteHttp.post("/api/auth/login",
                Map.of("usuario", usuario, "contrasena", contrasena));
        return ClienteHttp.parsear(respuesta, LoginResponseDTO.class);
    }

    // ── Médicos ───────────────────────────────────────────────────────────────

    public List<MedicoDTO> listarMedicosActivos() throws Exception {
        String json = ClienteHttp.getConToken("/api/medicos/activos", token());
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
        String json = ClienteHttp.getConToken("/api/especialidades", token());
        return ClienteHttp.parsearLista(json, Map.class);
    }

    // ── Personas ──────────────────────────────────────────────────────────────

    public List<PersonaDTO> listarPersonas() throws Exception {
        String json = ClienteHttp.getConToken("/api/personas", token());
        return ClienteHttp.parsearLista(json, PersonaDTO.class);
    }

    public PersonaDTO buscarPacientePorDocumento(String cedula) throws Exception {
        try {
            String json = ClienteHttp.getConToken("/api/pacientes/documento/" + cedula, token());
            return ClienteHttp.parsear(json, PersonaDTO.class);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("HTTP 404")) return null;
            throw e;
        }
    }

    public PersonaDTO buscarPacientePorId(int idPaciente) throws Exception {
        String json = ClienteHttp.getConToken("/api/pacientes/" + idPaciente, token());
        return ClienteHttp.parsear(json, PersonaDTO.class);
    }

    public PersonaDTO registrarPaciente(Map<String, Object> body) throws Exception {
        // Endpoint público — no requiere token (el usuario aún no tiene sesión)
        String json = ClienteHttp.post("/api/pacientes", body);
        return ClienteHttp.parsear(json, PersonaDTO.class);
    }

    public void editarPersona(int idPersona, Map<String, Object> campos, String token) throws Exception {
        ClienteHttp.put("/api/personas/" + idPersona, campos, token);
    }

    public void crearPersonaAdmin(Map<String, Object> body) throws Exception {
        ClienteHttp.postConToken("/api/personas", body, token());
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
        String json = ClienteHttp.getConToken("/api/roles/usuario/" + idUsuario, token());
        return ClienteHttp.parsearLista(json, RolDTO.class);
    }

    public void eliminarRol(int idRol, String token) throws Exception {
        ClienteHttp.delete("/api/roles/" + idRol, token);
    }

    // ── Usuarios ──────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public List<Map> listarUsuarios() throws Exception {
        String json = ClienteHttp.getConToken("/api/usuarios", token());
        return ClienteHttp.parsearLista(json, Map.class);
    }

    public void editarUsuario(int idUsuario, Map<String, Object> body, String token) throws Exception {
        ClienteHttp.put("/api/usuarios/" + idUsuario, body, token);
    }

    // ── Citas ─────────────────────────────────────────────────────────────────

    public List<CitaDTO> listarTodasLasCitas() throws Exception {
        String json = ClienteHttp.getConToken("/api/citas/todas", token());
        return ClienteHttp.parsearLista(json, CitaDTO.class);
    }

    public List<CitaDTO> listarCitas(LocalDate fecha, Integer idMedico) throws Exception {
        if (fecha != null && idMedico != null) {
            // Médico + fecha: filtro exacto en backend
            String url = "/api/citas?fecha=" + fecha + "&idMedico=" + idMedico;
            return ClienteHttp.parsearLista(ClienteHttp.getConToken(url, token()), CitaDTO.class);
        } else if (fecha != null) {
            // Solo fecha
            String url = "/api/citas?fecha=" + fecha;
            return ClienteHttp.parsearLista(ClienteHttp.getConToken(url, token()), CitaDTO.class);
        } else if (idMedico != null) {
            // Solo médico: traer todas y filtrar en cliente para evitar el default "hoy" del backend
            final int id = idMedico;
            return listarTodasLasCitas().stream()
                    .filter(c -> c.getIdMedico() == id)
                    .toList();
        } else {
            // Sin filtros: todas
            return listarTodasLasCitas();
        }
    }

    public List<CitaDTO> listarCitasHoy() throws Exception {
        String json = ClienteHttp.getConToken("/api/citas?fecha=" + LocalDate.now(), token());
        return ClienteHttp.parsearLista(json, CitaDTO.class);
    }

    public List<CitaDTO> listarTodasLasCitasPaciente(int idPaciente) throws Exception {
        // Trae historial + futuras combinados para clasificar en el cliente
        List<CitaDTO> historial = historialCitasPaciente(idPaciente);
        List<CitaDTO> futuras   = citasFuturasPaciente(idPaciente);
        java.util.List<CitaDTO> todas = new java.util.ArrayList<>(historial);
        todas.addAll(futuras);
        return todas;
    }

    public List<CitaDTO> historialCitasPaciente(int idPaciente) throws Exception {
        String json = ClienteHttp.getConToken("/api/citas/paciente/" + idPaciente + "/historial", token());
        return ClienteHttp.parsearLista(json, CitaDTO.class);
    }

    public List<CitaDTO> citasFuturasPaciente(int idPaciente) throws Exception {
        String json = ClienteHttp.getConToken("/api/citas/paciente/" + idPaciente + "/futuras", token());
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

    public ErrorValidacionDTO reagendarCitaConValidacion(int idCita, LocalDate nuevaFecha, LocalTime nuevaHora,
                                                          String token) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("nuevaFecha", nuevaFecha.toString());
        body.put("nuevaHora", nuevaHora.toString());
        try {
            String respuesta = ClienteHttp.patch("/api/citas/" + idCita + "/reagendar", body, token);
            return new ErrorValidacionDTO(true, respuesta, List.of(), 0);
        } catch (Exception e) {
            ErrorValidacionDTO dto = parsearErrorValidacion(e);
            if (dto != null) return dto;
            throw e;
        }
    }

    public String agendarCitaWeb(int idPaciente, int idMedico, LocalDate fecha,
                                  LocalTime hora) throws Exception {
        return ClienteHttp.postConToken("/api/citas/web", Map.of(
                "idPaciente", String.valueOf(idPaciente),
                "idMedico",   String.valueOf(idMedico),
                "fecha",      fecha.toString(),
                "hora",       hora.toString()
        ), token());
    }

    public ErrorValidacionDTO agendarCitaWebConValidacion(int idPaciente, int idMedico, LocalDate fecha,
                                                           LocalTime hora) throws Exception {
        try {
            String respuesta = ClienteHttp.postConToken("/api/citas/web", Map.of(
                    "idPaciente", String.valueOf(idPaciente),
                    "idMedico",   String.valueOf(idMedico),
                    "fecha",      fecha.toString(),
                    "hora",       hora.toString()
            ), token());
            return new ErrorValidacionDTO(true, respuesta, List.of(), 0);
        } catch (Exception e) {
            ErrorValidacionDTO dto = parsearErrorValidacion(e);
            if (dto != null) return dto;
            throw e;
        }
    }

    public List<HistorialCitaDTO> obtenerHistorialCita(int idCita) throws Exception {
        String json = ClienteHttp.getConToken("/api/citas/" + idCita + "/historial", token());
        return ClienteHttp.parsearLista(json, HistorialCitaDTO.class);
    }

    // ── Disponibilidad ────────────────────────────────────────────────────────

    public List<LocalTime> consultarDisponibilidad(int idMedico, LocalDate fecha) throws Exception {
        return consultarDisponibilidad(idMedico, fecha, null);
    }

    public List<LocalTime> consultarDisponibilidad(int idMedico, LocalDate fecha, Integer excluirCitaId) throws Exception {
        String url = "/api/citas/disponibilidad?idMedico=" + idMedico + "&fecha=" + fecha;
        if (excluirCitaId != null) {
            url += "&excluirCitaId=" + excluirCitaId;
        }
        String json = ClienteHttp.getConToken(url, token());
        return ClienteHttp.parsearLista(json, LocalTime.class);
    }

    // ── Jornadas ──────────────────────────────────────────────────────────────

    public List<JornadaDTO> listarJornadas() throws Exception {
        String json = ClienteHttp.getConToken("/api/jornadas", token());
        return ClienteHttp.parsearLista(json, JornadaDTO.class);
    }

    public JornadaDTO crearJornada(JornadaDTO jornada) throws Exception {
        String json = ClienteHttp.postConToken("/api/jornadas", jornada, token());
        return ClienteHttp.parsear(json, JornadaDTO.class);
    }

    public JornadaDTO editarJornada(JornadaDTO jornada) throws Exception {
        String json = ClienteHttp.put("/api/jornadas/" + jornada.getIdJornada(), jornada, token());
        return ClienteHttp.parsear(json, JornadaDTO.class);
    }

    public void eliminarJornada(int idJornada) throws Exception {
        ClienteHttp.delete("/api/jornadas/" + idJornada, token());
    }

    public List<String> listarDiasConJornada(int idUsuario) throws Exception {
        String json = ClienteHttp.getConToken("/api/jornadas/medico/" + idUsuario + "/dias", token());
        return ClienteHttp.parsearLista(json, String.class);
    }

    /** Días con turno del médico (resuelve id_usuario desde MedicoDTO). */
    public List<String> listarDiasConJornada(MedicoDTO medico) throws Exception {
        if (medico == null) return listarDiasConJornadaTodos();
        int idUsuario = medico.getIdUsuario() != null && medico.getIdUsuario() > 0
                ? medico.getIdUsuario() : medico.getIdMedico();
        return listarDiasConJornada(idUsuario);
    }

    /** Días de la semana en que al menos un médico tiene jornada. */
    public List<String> listarDiasConJornadaTodos() throws Exception {
        List<JornadaDTO> jornadas = listarJornadas();
        return jornadas.stream()
                .map(JornadaDTO::getDiaSemana)
                .filter(d -> d != null && !d.isBlank())
                .distinct()
                .toList();
    }

    public List<PersonaDTO> listarPacientes() throws Exception {
        String json = ClienteHttp.getConToken("/api/pacientes", token());
        return ClienteHttp.parsearLista(json, PersonaDTO.class);
    }

    private ErrorValidacionDTO parsearErrorValidacion(Exception e) {
        String m = e.getMessage();
        if (m == null || !m.startsWith("HTTP 400:")) return null;
        String body = m.substring("HTTP 400:".length()).trim();
        try {
            return ClienteHttp.parsear(body, ErrorValidacionDTO.class);
        } catch (Exception ignore) {
            return null;
        }
    }
}
