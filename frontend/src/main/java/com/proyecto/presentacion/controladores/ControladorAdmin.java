package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.CitaDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ControladorAdmin implements Initializable {

    // ── Paneles ──────────────────────────────────────────────
    @FXML private VBox panelPrincipal;
    @FXML private VBox panelTurnos;
    @FXML private VBox panelPersonas;
    @FXML private VBox panelUsuarios;

    // ── Panel principal ───────────────────────────────────────
    @FXML private ComboBox<MedicoDTO>  cbDoctorTurno;
    @FXML private ComboBox<PersonaDTO> cbPersonaRol;
    @FXML private ComboBox<String>     cbRol;
    @FXML private ComboBox<MedicoDTO>  cbMedicoEspecialidad;
    @FXML private ComboBox<String>     cbEspecialidad;

    // ── Panel Turnos ──────────────────────────────────────────
    @FXML private TableView<CitaDTO>           tblTurnos;
    @FXML private TableColumn<CitaDTO, String> colTurnoCedPaciente;
    @FXML private TableColumn<CitaDTO, String> colTurnoNomPaciente;
    @FXML private TableColumn<CitaDTO, String> colTurnoCedMedico;
    @FXML private TableColumn<CitaDTO, String> colTurnoNomMedico;
    @FXML private TableColumn<CitaDTO, String> colTurnoFecha;
    @FXML private TableColumn<CitaDTO, String> colTurnoHoraInicio;
    @FXML private TableColumn<CitaDTO, String> colTurnoHoraFin;
    @FXML private TableColumn<CitaDTO, String> colTurnoEstado;
    @FXML private TextField   txtTurnoCedPaciente;
    @FXML private TextField   txtTurnoNomPaciente;
    @FXML private TextField   txtTurnoCedMedico;
    @FXML private TextField   txtTurnoNomMedico;
    @FXML private DatePicker  dpTurnoFecha;
    @FXML private ComboBox<LocalTime> cbTurnoHoraInicio;
    @FXML private ComboBox<LocalTime> cbTurnoHoraFin;

    // ── Panel Personas ────────────────────────────────────────
    @FXML private TableView<PersonaDTO>           tblPersonas;
    @FXML private TableColumn<PersonaDTO, String> colPerCedula;
    @FXML private TableColumn<PersonaDTO, String> colPerNombre;
    @FXML private TableColumn<PersonaDTO, String> colPerApellido;
    @FXML private TableColumn<PersonaDTO, String> colPerCelular;
    @FXML private TableColumn<PersonaDTO, String> colPerFechaNac;
    @FXML private TableColumn<PersonaDTO, String> colPerCorreo;
    @FXML private TableColumn<PersonaDTO, String> colPerGenero;
    @FXML private TextField   txtPerCedula;
    @FXML private TextField   txtPerNombre;
    @FXML private TextField   txtPerApellido;
    @FXML private TextField   txtPerCelular;
    @FXML private DatePicker  dpPerFechaNac;
    @FXML private TextField   txtPerCorreo;
    @FXML private ChoiceBox<String> cbPerGenero;

    // ── Panel Usuarios ────────────────────────────────────────
    @FXML private TableView<Map>           tblUsuarios;
    @FXML private TableColumn<Map, String> colUsuNombre;
    @FXML private TableColumn<Map, String> colUsuContrasena;
    @FXML private TextField txtUsuNombre;
    @FXML private TextField txtUsuContrasena;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombosPrincipal();
        configurarTablaTurnos();
        configurarTablaPersonas();
        configurarTablaUsuarios();
        cargarTablaTurnos();
        cargarTablaPersonas();
        cargarTablaUsuarios();
    }

    // ── Navegación ────────────────────────────────────────────

    private void mostrarPanel(VBox panel) {
        panelPrincipal.setVisible(false); panelTurnos.setVisible(false);
        panelPersonas.setVisible(false);  panelUsuarios.setVisible(false);
        panel.setVisible(true); panel.toFront();
    }

    @FXML void onAbrirTurnos(ActionEvent e)   { mostrarPanel(panelTurnos);   cargarTablaTurnos(); }
    @FXML void onAbrirPersonas(ActionEvent e) { mostrarPanel(panelPersonas); cargarTablaPersonas(); }
    @FXML void onAbrirUsuarios(ActionEvent e) { mostrarPanel(panelUsuarios); cargarTablaUsuarios(); }
    @FXML void onVolverDesdePanel(ActionEvent e) { mostrarPanel(panelPrincipal); }

    @FXML
    void onCerrarSesion(ActionEvent e) {
        SesionUsuario.getInstancia().limpiarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) panelPrincipal.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ── Combos principal ──────────────────────────────────────

    private void cargarCombosPrincipal() {
        try {
            // Médicos activos
            String jsonMedicos = ClienteHttp.get("/api/medicos/activos");
            List<MedicoDTO> medicos = ClienteHttp.parsearLista(jsonMedicos, MedicoDTO.class);
            StringConverter<MedicoDTO> convMedico = new StringConverter<>() {
                public String toString(MedicoDTO m)   { return m == null ? "" : m.toString(); }
                public MedicoDTO fromString(String s) { return null; }
            };
            cbDoctorTurno.setItems(FXCollections.observableArrayList(medicos));
            cbDoctorTurno.setConverter(convMedico);
            cbMedicoEspecialidad.setItems(FXCollections.observableArrayList(medicos));
            cbMedicoEspecialidad.setConverter(convMedico);

            // Especialidades
            String jsonEsp = ClienteHttp.get("/api/especialidades");
            List<Map> especialidades = ClienteHttp.parsearLista(jsonEsp, Map.class);
            cbEspecialidad.setItems(FXCollections.observableArrayList(
                    especialidades.stream().map(e -> e.get("nombre").toString()).toList()));

            // Personas
            String jsonPersonas = ClienteHttp.get("/api/personas");
            List<PersonaDTO> personas = ClienteHttp.parsearLista(jsonPersonas, PersonaDTO.class);
            cbPersonaRol.setItems(FXCollections.observableArrayList(personas));
            cbPersonaRol.setConverter(new StringConverter<>() {
                public String toString(PersonaDTO p)   { return p == null ? "" : p.toString(); }
                public PersonaDTO fromString(String s) { return null; }
            });

            cbRol.setItems(FXCollections.observableArrayList("Administrador", "Agendador", "Medico", "Paciente"));
            cbPerGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "No Binario", "Prefiero no decir"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void onAsignarEspecialidad(ActionEvent e) {
        MedicoDTO medico = cbMedicoEspecialidad.getValue();
        String esp = cbEspecialidad.getValue();
        if (medico == null || esp == null) { mostrarError("Seleccione médico y especialidad"); return; }
        mostrarInfo("Especialidad asignada (funcionalidad pendiente de mapeo de ID)");
    }

    @FXML
    void onAsignarRol(ActionEvent e) {
        mostrarInfo("Asignación de rol pendiente de implementación completa");
    }

    // ── Turnos ────────────────────────────────────────────────

    private void configurarTablaTurnos() {
        colTurnoCedPaciente.setCellValueFactory(c -> new SimpleStringProperty("Pac #" + c.getValue().getIdPaciente()));
        colTurnoNomPaciente.setCellValueFactory(c -> new SimpleStringProperty("Pac #" + c.getValue().getIdPaciente()));
        colTurnoCedMedico.setCellValueFactory(c -> new SimpleStringProperty("Med #" + c.getValue().getIdMedico()));
        colTurnoNomMedico.setCellValueFactory(c -> new SimpleStringProperty("Med #" + c.getValue().getIdMedico()));
        colTurnoFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha() != null ? c.getValue().getFecha().toString() : ""));
        colTurnoHoraInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraInicio() != null ? c.getValue().getHoraInicio().toString() : ""));
        colTurnoHoraFin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraFin() != null ? c.getValue().getHoraFin().toString() : ""));
        colTurnoEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdEstadoCita() != null ? String.valueOf(c.getValue().getIdEstadoCita()) : ""));
        tblTurnos.getSelectionModel().selectedItemProperty().addListener((obs, old, cita) -> {
            if (cita != null) dpTurnoFecha.setValue(cita.getFecha());
        });
    }

    private void cargarTablaTurnos() {
        try {
            String json = ClienteHttp.get("/api/citas/todas");
            List<CitaDTO> citas = ClienteHttp.parsearLista(json, CitaDTO.class);
            tblTurnos.setItems(FXCollections.observableArrayList(citas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void onCrearTurno(ActionEvent e)   { mostrarInfo("Use la vista de Agendar Cita para crear nuevos turnos"); }

    @FXML
    void onEditarTurno(ActionEvent e) {
        CitaDTO cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno de la lista"); return; }
        try {
            if (dpTurnoFecha.getValue() != null) cita.setFecha(dpTurnoFecha.getValue());
            // PUT http://localhost:8080/api/citas/{id}
            ClienteHttp.put("/api/citas/" + cita.getIdCita(), cita, SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            mostrarInfo("Turno actualizado");
        } catch (Exception ex) { mostrarError("Error al editar: " + ex.getMessage()); }
    }

    @FXML
    void onEliminarTurno(ActionEvent e) {
        CitaDTO cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno de la lista"); return; }
        try {
            // DELETE http://localhost:8080/api/citas/{id}
            ClienteHttp.delete("/api/citas/" + cita.getIdCita(), SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            mostrarInfo("Turno cancelado");
        } catch (Exception ex) { mostrarError("Error al eliminar: " + ex.getMessage()); }
    }

    // ── Personas ──────────────────────────────────────────────

    private void configurarTablaPersonas() {
        colPerCedula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCedulaCiudadania()));
        colPerNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPerApellido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellido()));
        colPerCelular.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCelular() != null ? c.getValue().getCelular() : ""));
        colPerFechaNac.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaNacimiento() != null ? c.getValue().getFechaNacimiento().toString() : ""));
        colPerCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo() != null ? c.getValue().getCorreo() : ""));
        colPerGenero.setCellValueFactory(c -> {
            Integer g = c.getValue().getIdGenero();
            return new SimpleStringProperty(g == null ? "" : switch (g) { case 1 -> "Masculino"; case 2 -> "Femenino"; case 3 -> "No Binario"; default -> "Otro"; });
        });
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) {
                txtPerCedula.setText(p.getCedulaCiudadania()); txtPerNombre.setText(p.getNombre());
                txtPerApellido.setText(p.getApellido()); txtPerCelular.setText(p.getCelular() != null ? p.getCelular() : "");
                txtPerCorreo.setText(p.getCorreo() != null ? p.getCorreo() : "");
                dpPerFechaNac.setValue(p.getFechaNacimiento());
            }
        });
    }

    private void cargarTablaPersonas() {
        try {
            String json = ClienteHttp.get("/api/personas");
            List<PersonaDTO> personas = ClienteHttp.parsearLista(json, PersonaDTO.class);
            tblPersonas.setItems(FXCollections.observableArrayList(personas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void onEditarPersona(ActionEvent e) {
        PersonaDTO p = tblPersonas.getSelectionModel().getSelectedItem();
        if (p == null) { mostrarError("Seleccione una persona de la lista"); return; }
        p.setNombre(txtPerNombre.getText()); p.setApellido(txtPerApellido.getText());
        p.setCelular(txtPerCelular.getText()); p.setCorreo(txtPerCorreo.getText());
        if (dpPerFechaNac.getValue() != null) p.setFechaNacimiento(dpPerFechaNac.getValue());
        try {
            ClienteHttp.put("/api/personas/" + p.getIdPersona(), p, SesionUsuario.getInstancia().getToken());
            cargarTablaPersonas(); mostrarInfo("Persona actualizada");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @FXML
    void onCrearPersona(ActionEvent e) { mostrarInfo("Para crear personas use el registro de pacientes."); }

    @FXML
    void onEliminarPersona(ActionEvent e) {
        PersonaDTO p = tblPersonas.getSelectionModel().getSelectedItem();
        if (p == null) { mostrarError("Seleccione una persona de la lista"); return; }
        try {
            ClienteHttp.delete("/api/personas/" + p.getIdPersona(), SesionUsuario.getInstancia().getToken());
            cargarTablaPersonas(); mostrarInfo("Persona inactivada");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    // ── Usuarios ──────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void configurarTablaUsuarios() {
        colUsuNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().get("usuario") != null ? c.getValue().get("usuario").toString() : ""));
        colUsuContrasena.setCellValueFactory(c -> new SimpleStringProperty("••••••••"));
    }

    @SuppressWarnings("unchecked")
    private void cargarTablaUsuarios() {
        try {
            String json = ClienteHttp.get("/api/usuarios");
            List<Map> usuarios = ClienteHttp.parsearLista(json, Map.class);
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void onEditarUsuario(ActionEvent e)  { mostrarInfo("Edición de usuario pendiente"); }
    @FXML void onCrearUsuario(ActionEvent e)   { mostrarInfo("Creación de usuario pendiente"); }
    @FXML void onEliminarUsuario(ActionEvent e) { mostrarInfo("Eliminación de usuario pendiente"); }

    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}
