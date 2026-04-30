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
import java.util.HashMap;
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
    @FXML private ComboBox<PersonaDTO> cbPersonaRol;
    @FXML private ComboBox<String>     cbRol;
    @FXML private ComboBox<MedicoDTO>  cbMedicoEspecialidad;
    @FXML private ComboBox<String>     cbEspecialidad;

    // ── Panel Turnos ──────────────────────────────────────────
    @FXML private ComboBox<MedicoDTO>         cbTurnoDoctor;
    @FXML private TextField                   txtTurnoCedMedico;
    @FXML private TextField                   txtTurnoNomMedico;
    @FXML private TableView<CitaDTO>           tblTurnos;
    @FXML private TableColumn<CitaDTO, String> colTurnoCedMedico;
    @FXML private TableColumn<CitaDTO, String> colTurnoNomMedico;
    @FXML private TableColumn<CitaDTO, String> colTurnoFecha;
    @FXML private TableColumn<CitaDTO, String> colTurnoHoraInicio;
    @FXML private TableColumn<CitaDTO, String> colTurnoHoraFin;
    @FXML private TableColumn<CitaDTO, String> colTurnoEstado;
    @FXML private DatePicker              dpTurnoFecha;
    @FXML private ComboBox<LocalTime>     cbTurnoHoraInicio;
    @FXML private ComboBox<LocalTime>     cbTurnoHoraFin;
    @FXML private CheckBox                chkTurnoActivo;

    // ── Panel Personas ────────────────────────────────────────
    @FXML private TableView<PersonaDTO>           tblPersonas;
    @FXML private TableColumn<PersonaDTO, String> colPerCedula;
    @FXML private TableColumn<PersonaDTO, String> colPerNombre;
    @FXML private TableColumn<PersonaDTO, String> colPerApellido;
    @FXML private TableColumn<PersonaDTO, String> colPerCelular;
    @FXML private TableColumn<PersonaDTO, String> colPerFechaNac;
    @FXML private TableColumn<PersonaDTO, String> colPerCorreo;
    @FXML private TableColumn<PersonaDTO, String> colPerGenero;
    @FXML private TextField         txtPerCedula;
    @FXML private TextField         txtPerNombre;
    @FXML private TextField         txtPerApellido;
    @FXML private TextField         txtPerCelular;
    @FXML private DatePicker        dpPerFechaNac;
    @FXML private TextField         txtPerCorreo;
    @FXML private ChoiceBox<String> cbPerGenero;

    // ── Panel Usuarios ────────────────────────────────────────
    @FXML private TableView<Map>           tblUsuarios;
    @FXML private TableColumn<Map, String> colUsuNombre;
    @FXML private TableColumn<Map, String> colUsuContrasena;
    @FXML private TextField txtUsuNombre;
    @FXML private TextField txtUsuContrasena;

    // Mapa idMedico → MedicoDTO para resolver nombre/cédula en la tabla
    private final Map<Integer, MedicoDTO> mapaMedicos = new HashMap<>();
    // Lista completa de turnos para filtrado local
    private List<CitaDTO> todosTurnos = new java.util.ArrayList<>();

    // Converter reutilizable para MedicoDTO
    private final StringConverter<MedicoDTO> convMedico = new StringConverter<>() {
        public String toString(MedicoDTO m)   { return m == null ? "" : m.toString(); }
        public MedicoDTO fromString(String s) { return null; }
    };

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

    @FXML void onAbrirTurnos(ActionEvent e)      { mostrarPanel(panelTurnos);   cargarTablaTurnos(); }
    @FXML void onAbrirPersonas(ActionEvent e)    { mostrarPanel(panelPersonas); cargarTablaPersonas(); }
    @FXML void onAbrirUsuarios(ActionEvent e)    { mostrarPanel(panelUsuarios); cargarTablaUsuarios(); }
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

            // Poblar mapa para resolver en tabla
            mapaMedicos.clear();
            for (MedicoDTO m : medicos) mapaMedicos.put(m.getIdMedico(), m);

            // Combo doctor en formulario de turno
            cbTurnoDoctor.setItems(FXCollections.observableArrayList(medicos));
            cbTurnoDoctor.setConverter(convMedico);
            // El onAction está declarado en el FXML como #onSeleccionarDoctor

            // Combo médico para especialidad
            cbMedicoEspecialidad.setItems(FXCollections.observableArrayList(medicos));
            cbMedicoEspecialidad.setConverter(convMedico);

            // Especialidades
            String jsonEsp = ClienteHttp.get("/api/especialidades");
            List<Map> especialidades = ClienteHttp.parsearLista(jsonEsp, Map.class);
            cbEspecialidad.setItems(FXCollections.observableArrayList(
                    especialidades.stream().map(e -> e.get("nombre").toString()).toList()));

            // Personas para asignar rol
            String jsonPersonas = ClienteHttp.get("/api/personas");
            List<PersonaDTO> personas = ClienteHttp.parsearLista(jsonPersonas, PersonaDTO.class);
            cbPersonaRol.setItems(FXCollections.observableArrayList(personas));
            cbPersonaRol.setConverter(new StringConverter<>() {
                public String toString(PersonaDTO p)   { return p == null ? "" : p.toString(); }
                public PersonaDTO fromString(String s) { return null; }
            });

            cbRol.setItems(FXCollections.observableArrayList("Administrador", "Agendador", "Medico", "Paciente"));
            cbPerGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "No Binario", "Prefiero no decir"));

            // Horas disponibles para turnos (07:00 – 20:00 cada 30 min)
            List<LocalTime> horas = new java.util.ArrayList<>();
            for (int h = 7; h <= 20; h++) {
                horas.add(LocalTime.of(h, 0));
                if (h < 20) horas.add(LocalTime.of(h, 30));
            }
            cbTurnoHoraInicio.setItems(FXCollections.observableArrayList(horas));
            cbTurnoHoraFin.setItems(FXCollections.observableArrayList(horas));

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

    /** Llamado desde FXML cuando cambia el doctor en el combo.
     *  Filtra la tabla y autocompleta cédula/nombre. */
    @FXML
    void onSeleccionarDoctor(ActionEvent e) {
        MedicoDTO sel = cbTurnoDoctor.getValue();
        if (sel != null) {
            txtTurnoCedMedico.setText(String.valueOf(sel.getIdMedico()));
            txtTurnoNomMedico.setText(sel.getNombre() + " " + sel.getApellido());
            // Filtrar tabla para mostrar solo turnos de este doctor
            List<CitaDTO> filtrados = todosTurnos.stream()
                    .filter(c -> c.getIdMedico() == sel.getIdMedico())
                    .toList();
            tblTurnos.setItems(FXCollections.observableArrayList(filtrados));
        } else {
            txtTurnoCedMedico.clear();
            txtTurnoNomMedico.clear();
            tblTurnos.setItems(FXCollections.observableArrayList(todosTurnos));
        }
        // Limpiar campos de horario al cambiar de doctor
        dpTurnoFecha.setValue(null);
        cbTurnoHoraInicio.setValue(null);
        cbTurnoHoraFin.setValue(null);
        chkTurnoActivo.setSelected(false);
    }

    private void configurarTablaTurnos() {
        // Resolver nombre y cédula del médico desde el mapa
        colTurnoCedMedico.setCellValueFactory(c -> {
            MedicoDTO m = mapaMedicos.get(c.getValue().getIdMedico());
            return new SimpleStringProperty(m != null ? String.valueOf(m.getIdMedico()) : "ID: " + c.getValue().getIdMedico());
        });
        colTurnoNomMedico.setCellValueFactory(c -> {
            MedicoDTO m = mapaMedicos.get(c.getValue().getIdMedico());
            return new SimpleStringProperty(m != null ? m.getNombre() + " " + m.getApellido() : "—");
        });
        colTurnoFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFecha() != null ? c.getValue().getFecha().toString() : ""));
        colTurnoHoraInicio.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraInicio() != null ? c.getValue().getHoraInicio().toString() : ""));
        colTurnoHoraFin.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraFin() != null ? c.getValue().getHoraFin().toString() : ""));
        colTurnoEstado.setCellValueFactory(c -> {
            Integer estado = c.getValue().getIdEstadoCita();
            String texto = estado == null ? "" : (estado == 1 ? "Activo" : "Inactivo");
            return new SimpleStringProperty(texto);
        });

        // Ajustar columnas para que llenen el ancho disponible
        tblTurnos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Al seleccionar un turno, poblar el formulario completo
        tblTurnos.getSelectionModel().selectedItemProperty().addListener((obs, old, cita) -> {
            if (cita == null) return;
            dpTurnoFecha.setValue(cita.getFecha());
            cbTurnoHoraInicio.setValue(cita.getHoraInicio());
            cbTurnoHoraFin.setValue(cita.getHoraFin());
            chkTurnoActivo.setSelected(cita.getIdEstadoCita() != null && cita.getIdEstadoCita() == 1);

            // Seleccionar el doctor en el combo y autocompletar campos
            MedicoDTO medico = mapaMedicos.get(cita.getIdMedico());
            if (medico != null) {
                cbTurnoDoctor.setValue(medico);
                txtTurnoCedMedico.setText(String.valueOf(medico.getIdMedico()));
                txtTurnoNomMedico.setText(medico.getNombre() + " " + medico.getApellido());
            } else {
                cbTurnoDoctor.setValue(null);
                txtTurnoCedMedico.setText("ID: " + cita.getIdMedico());
                txtTurnoNomMedico.clear();
            }
        });
    }

    private void cargarTablaTurnos() {
        try {
            String json = ClienteHttp.get("/api/citas/todas");
            todosTurnos = ClienteHttp.parsearLista(json, CitaDTO.class);
            // Si hay un doctor seleccionado, mantener el filtro; si no, mostrar todos
            MedicoDTO sel = cbTurnoDoctor.getValue();
            if (sel != null) {
                List<CitaDTO> filtrados = todosTurnos.stream()
                        .filter(c -> c.getIdMedico() == sel.getIdMedico())
                        .toList();
                tblTurnos.setItems(FXCollections.observableArrayList(filtrados));
            } else {
                tblTurnos.setItems(FXCollections.observableArrayList(todosTurnos));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void onCrearTurno(ActionEvent e) {
        MedicoDTO doctor = cbTurnoDoctor.getValue();
        if (doctor == null || dpTurnoFecha.getValue() == null
                || cbTurnoHoraInicio.getValue() == null || cbTurnoHoraFin.getValue() == null) {
            mostrarError("Complete todos los campos obligatorios: Doctor, Fecha, Hora Inicio y Hora Final");
            return;
        }
        try {
            CitaDTO nuevo = new CitaDTO();
            nuevo.setIdMedico(doctor.getIdMedico());
            nuevo.setFecha(dpTurnoFecha.getValue());
            nuevo.setHoraInicio(cbTurnoHoraInicio.getValue());
            nuevo.setHoraFin(cbTurnoHoraFin.getValue());
            nuevo.setIdEstadoCita(chkTurnoActivo.isSelected() ? 1 : 2);
            ClienteHttp.postConToken("/api/citas", nuevo, SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            limpiarFormularioTurno();
            mostrarInfo("Turno creado correctamente");
        } catch (Exception ex) { mostrarError("Error al crear turno: " + ex.getMessage()); }
    }

    @FXML
    void onEditarTurno(ActionEvent e) {
        CitaDTO cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno de la lista"); return; }
        try {
            MedicoDTO doctor = cbTurnoDoctor.getValue();
            if (doctor != null) cita.setIdMedico(doctor.getIdMedico());
            if (dpTurnoFecha.getValue() != null) cita.setFecha(dpTurnoFecha.getValue());
            if (cbTurnoHoraInicio.getValue() != null) cita.setHoraInicio(cbTurnoHoraInicio.getValue());
            if (cbTurnoHoraFin.getValue() != null) cita.setHoraFin(cbTurnoHoraFin.getValue());
            cita.setIdEstadoCita(chkTurnoActivo.isSelected() ? 1 : 2);
            ClienteHttp.put("/api/citas/" + cita.getIdCita(), cita, SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            mostrarInfo("Turno actualizado correctamente");
        } catch (Exception ex) { mostrarError("Error al editar turno: " + ex.getMessage()); }
    }

    @FXML
    void onEliminarTurno(ActionEvent e) {
        CitaDTO cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno de la lista"); return; }
        try {
            ClienteHttp.delete("/api/citas/" + cita.getIdCita(), SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            limpiarFormularioTurno();
            mostrarInfo("Turno eliminado correctamente");
        } catch (Exception ex) { mostrarError("Error al eliminar turno: " + ex.getMessage()); }
    }

    private void limpiarFormularioTurno() {
        cbTurnoDoctor.setValue(null);
        txtTurnoCedMedico.clear();
        txtTurnoNomMedico.clear();
        dpTurnoFecha.setValue(null);
        cbTurnoHoraInicio.setValue(null);
        cbTurnoHoraFin.setValue(null);
        chkTurnoActivo.setSelected(false);
    }

    // ── Personas ──────────────────────────────────────────────

    private void configurarTablaPersonas() {
        tblPersonas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colPerCedula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCedulaCiudadania()));
        colPerNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPerApellido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellido()));
        colPerCelular.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCelular() != null ? c.getValue().getCelular() : ""));
        colPerFechaNac.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaNacimiento() != null ? c.getValue().getFechaNacimiento().toString() : ""));
        colPerCorreo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCorreo() != null ? c.getValue().getCorreo() : ""));
        colPerGenero.setCellValueFactory(c -> {
            Integer g = c.getValue().getIdGenero();
            return new SimpleStringProperty(g == null ? "" : switch (g) {
                case 1 -> "Masculino"; case 2 -> "Femenino"; case 3 -> "No Binario"; default -> "Otro";
            });
        });
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p == null) return;
            txtPerCedula.setText(p.getCedulaCiudadania());
            txtPerNombre.setText(p.getNombre());
            txtPerApellido.setText(p.getApellido());
            txtPerCelular.setText(p.getCelular() != null ? p.getCelular() : "");
            txtPerCorreo.setText(p.getCorreo() != null ? p.getCorreo() : "");
            dpPerFechaNac.setValue(p.getFechaNacimiento());
            // Poblar género
            Integer g = p.getIdGenero();
            if (g != null) {
                String generoStr = switch (g) {
                    case 1 -> "Masculino";
                    case 2 -> "Femenino";
                    case 3 -> "No Binario";
                    default -> "Prefiero no decir";
                };
                cbPerGenero.setValue(generoStr);
            } else {
                cbPerGenero.setValue(null);
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
        p.setNombre(txtPerNombre.getText());
        p.setApellido(txtPerApellido.getText());
        p.setCelular(txtPerCelular.getText());
        p.setCorreo(txtPerCorreo.getText());
        if (dpPerFechaNac.getValue() != null) p.setFechaNacimiento(dpPerFechaNac.getValue());
        try {
            ClienteHttp.put("/api/personas/" + p.getIdPersona(), p, SesionUsuario.getInstancia().getToken());
            cargarTablaPersonas();
            mostrarInfo("Persona actualizada correctamente");
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
            cargarTablaPersonas();
            mostrarInfo("Persona inactivada correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    // ── Usuarios ──────────────────────────────────────────────

    private void configurarTablaUsuarios() {
        tblUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colUsuNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().get("usuario") != null ? c.getValue().get("usuario").toString() : ""));
        colUsuContrasena.setCellValueFactory(c -> new SimpleStringProperty("••••••••"));

        // Al seleccionar un usuario, poblar el formulario
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> {
            if (u == null) return;
            txtUsuNombre.setText(u.get("usuario") != null ? u.get("usuario").toString() : "");
            txtUsuContrasena.clear(); // no mostrar contraseña real
        });
    }

    @SuppressWarnings("unchecked")
    private void cargarTablaUsuarios() {
        try {
            String json = ClienteHttp.get("/api/usuarios");
            List<Map> usuarios = ClienteHttp.parsearLista(json, Map.class);
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void onEditarUsuario(ActionEvent e)   { mostrarInfo("Edición de usuario pendiente"); }
    @FXML void onCrearUsuario(ActionEvent e)    { mostrarInfo("Creación de usuario pendiente"); }
    @FXML void onEliminarUsuario(ActionEvent e) { mostrarInfo("Eliminación de usuario pendiente"); }

    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}
