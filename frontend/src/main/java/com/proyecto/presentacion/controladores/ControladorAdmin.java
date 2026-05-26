
package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.facade.BackendFacade;
import com.proyecto.presentacion.util.Conversiones;
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

    @FXML private VBox panelPrincipal;
    @FXML private VBox panelTurnos;
    @FXML private VBox panelPersonas;
    @FXML private VBox panelUsuarios;

    @FXML private ComboBox<PersonaDTO> cbPersonaRol;
    @FXML private ComboBox<String>     cbRol;
    @FXML private ComboBox<MedicoDTO>  cbMedicoEspecialidad;
    @FXML private ComboBox<String>     cbEspecialidad;

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

    @FXML private TableView<Map>           tblUsuarios;
    @FXML private TableColumn<Map, String> colUsuNombre;
    @FXML private TableColumn<Map, String> colUsuContrasena;
    @FXML private TextField txtUsuNombre;
    @FXML private TextField txtUsuContrasena;

    private PersonaDTO personaSeleccionada = null;
    private final Map<Integer, MedicoDTO> mapaMedicos = new HashMap<>();
    private List<CitaDTO> todosTurnos = new java.util.ArrayList<>();
    private final BackendFacade backend = new BackendFacade();

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
            List<MedicoDTO> medicos = backend.listarMedicosActivos();
            mapaMedicos.clear();
            for (MedicoDTO m : medicos) mapaMedicos.put(m.getIdMedico(), m);

            cbTurnoDoctor.setItems(FXCollections.observableArrayList(medicos));
            cbTurnoDoctor.setConverter(convMedico);
            cbMedicoEspecialidad.setItems(FXCollections.observableArrayList(medicos));
            cbMedicoEspecialidad.setConverter(convMedico);

            List<Map> especialidades = backend.listarEspecialidades();
            cbEspecialidad.setItems(FXCollections.observableArrayList(
                    especialidades.stream().map(esp -> esp.get("nombre").toString()).toList()));

            List<PersonaDTO> personas = backend.listarPersonas();
            cbPersonaRol.setItems(FXCollections.observableArrayList(personas));
            cbPersonaRol.setConverter(new StringConverter<>() {
                public String toString(PersonaDTO p)   { return p == null ? "" : p.toString(); }
                public PersonaDTO fromString(String s) { return null; }
            });

            cbRol.setItems(FXCollections.observableArrayList("Administrador", "Agendador", "Medico", "Paciente"));
            cbPerGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "No Binario", "Prefiero no decir"));

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
        try {
            List<Map> especialidades = backend.listarEspecialidades();
            int idEsp = especialidades.stream()
                    .filter(m -> esp.equals(m.get("nombre")))
                    .mapToInt(m -> ((Number) m.get("idEspecialidad")).intValue())
                    .findFirst().orElse(-1);
            if (idEsp == -1) { mostrarError("No se encontró la especialidad"); return; }
            backend.asignarEspecialidad(medico.getIdMedico(), idEsp, SesionUsuario.getInstancia().getToken());
            mostrarInfo("Especialidad '" + esp + "' asignada a " + medico);
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @FXML
    void onAsignarRol(ActionEvent e) {
        PersonaDTO persona = cbPersonaRol.getValue();
        String rol = cbRol.getValue();
        if (persona == null || rol == null) { mostrarError("Seleccione persona y rol"); return; }
        if (persona.getIdUsuario() == null) { mostrarError("La persona no tiene usuario asociado"); return; }
        try {
            backend.asignarRol(persona.getIdUsuario(), rol, SesionUsuario.getInstancia().getToken());
            mostrarInfo("Rol '" + rol + "' asignado a " + persona);
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    // ── Turnos ────────────────────────────────────────────────

    @FXML
    void onSeleccionarDoctor(ActionEvent e) {
        MedicoDTO sel = cbTurnoDoctor.getValue();
        if (sel != null) {
            txtTurnoCedMedico.setText(String.valueOf(sel.getIdMedico()));
            txtTurnoNomMedico.setText(sel.getNombre() + " " + sel.getApellido());
            tblTurnos.setItems(FXCollections.observableArrayList(
                    todosTurnos.stream().filter(c -> c.getIdMedico() == sel.getIdMedico()).toList()));
        } else {
            txtTurnoCedMedico.clear();
            txtTurnoNomMedico.clear();
            tblTurnos.setItems(FXCollections.observableArrayList(todosTurnos));
        }
        dpTurnoFecha.setValue(null);
        cbTurnoHoraInicio.setValue(null);
        cbTurnoHoraFin.setValue(null);
        chkTurnoActivo.setSelected(false);
    }

    private void configurarTablaTurnos() {
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
            return new SimpleStringProperty(estado == null ? "" : (estado == 1 ? "Activo" : "Inactivo"));
        });
        tblTurnos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblTurnos.getSelectionModel().selectedItemProperty().addListener((obs, old, cita) -> {
            if (cita == null) return;
            dpTurnoFecha.setValue(cita.getFecha());
            cbTurnoHoraInicio.setValue(cita.getHoraInicio());
            cbTurnoHoraFin.setValue(cita.getHoraFin());
            chkTurnoActivo.setSelected(cita.getIdEstadoCita() != null && cita.getIdEstadoCita() == 1);
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
        new Thread(() -> {
            try {
                List<CitaDTO> citas = backend.listarTodasLasCitas();
                todosTurnos = citas;
                MedicoDTO sel = cbTurnoDoctor.getValue();
                List<CitaDTO> mostrar = (sel != null)
                        ? citas.stream().filter(c -> c.getIdMedico() == sel.getIdMedico()).toList()
                        : citas;
                javafx.application.Platform.runLater(() -> {
                    tblTurnos.getItems().clear();
                    tblTurnos.getItems().addAll(mostrar);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> mostrarError("Error al cargar turnos: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    void onCrearTurno(ActionEvent e) {
        MedicoDTO doctor = cbTurnoDoctor.getValue();
        if (doctor == null || dpTurnoFecha.getValue() == null
                || cbTurnoHoraInicio.getValue() == null || cbTurnoHoraFin.getValue() == null) {
            mostrarError("Complete: Doctor, Fecha, Hora Inicio y Hora Final"); return;
        }
        try {
            CitaDTO nuevo = new CitaDTO();
            nuevo.setIdMedico(doctor.getIdMedico());
            nuevo.setFecha(dpTurnoFecha.getValue());
            nuevo.setHoraInicio(cbTurnoHoraInicio.getValue());
            nuevo.setHoraFin(cbTurnoHoraFin.getValue());
            nuevo.setIdEstadoCita(chkTurnoActivo.isSelected() ? 1 : 2);
            backend.crearCitaManual(nuevo, SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            limpiarFormularioTurno();
            mostrarInfo("Turno creado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @FXML
    void onEditarTurno(ActionEvent e) {
        CitaDTO cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno"); return; }
        try {
            MedicoDTO doctor = cbTurnoDoctor.getValue();
            if (doctor != null) cita.setIdMedico(doctor.getIdMedico());
            if (dpTurnoFecha.getValue() != null) cita.setFecha(dpTurnoFecha.getValue());
            if (cbTurnoHoraInicio.getValue() != null) cita.setHoraInicio(cbTurnoHoraInicio.getValue());
            if (cbTurnoHoraFin.getValue() != null) cita.setHoraFin(cbTurnoHoraFin.getValue());
            cita.setIdEstadoCita(chkTurnoActivo.isSelected() ? 1 : 2);
            backend.editarCita(cita, SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            mostrarInfo("Turno actualizado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @FXML
    void onEliminarTurno(ActionEvent e) {
        CitaDTO cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno"); return; }
        try {
            backend.cancelarCita(cita.getIdCita(), SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            limpiarFormularioTurno();
            mostrarInfo("Turno eliminado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    private void limpiarFormularioTurno() {
        cbTurnoDoctor.setValue(null);
        txtTurnoCedMedico.clear(); txtTurnoNomMedico.clear();
        dpTurnoFecha.setValue(null);
        cbTurnoHoraInicio.setValue(null); cbTurnoHoraFin.setValue(null);
        chkTurnoActivo.setSelected(false);
    }

    // ── Personas ──────────────────────────────────────────────

    private void configurarTablaPersonas() {
        tblPersonas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblPersonas.setItems(FXCollections.observableArrayList());
        colPerCedula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCedulaCiudadania()));
        colPerNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPerApellido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellido()));
        colPerCelular.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCelular() != null ? c.getValue().getCelular() : ""));
        colPerFechaNac.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaNacimiento() != null ? c.getValue().getFechaNacimiento().toString() : ""));
        colPerCorreo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCorreo() != null ? c.getValue().getCorreo() : ""));
        colPerGenero.setCellValueFactory(c -> new SimpleStringProperty(
                Conversiones.idAGenero(c.getValue().getIdGenero())));
        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            personaSeleccionada = p;
            if (p == null) return;
            txtPerCedula.setText(p.getCedulaCiudadania());
            txtPerNombre.setText(p.getNombre());
            txtPerApellido.setText(p.getApellido());
            txtPerCelular.setText(p.getCelular() != null ? p.getCelular() : "");
            txtPerCorreo.setText(p.getCorreo() != null ? p.getCorreo() : "");
            dpPerFechaNac.setValue(p.getFechaNacimiento());
            cbPerGenero.setValue(Conversiones.idAGenero(p.getIdGenero()));
        });
    }

    private void cargarTablaPersonas() {
        new Thread(() -> {
            try {
                List<PersonaDTO> personas = backend.listarPersonas();
                javafx.application.Platform.runLater(() -> tblPersonas.getItems().setAll(personas));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> mostrarError("Error al cargar personas: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    void onEditarPersona(ActionEvent e) {
        if (personaSeleccionada == null) { mostrarError("Seleccione una persona"); return; }
        String nombre   = txtPerNombre.getText().trim();
        String apellido = txtPerApellido.getText().trim();
        if (nombre.isEmpty() || apellido.isEmpty()) { mostrarError("Nombre y Apellido son obligatorios"); return; }

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("nombre", nombre);
        body.put("apellido", apellido);
        body.put("cedulaCiudadania", personaSeleccionada.getCedulaCiudadania());
        body.put("celular", txtPerCelular.getText().trim());
        body.put("correo", txtPerCorreo.getText().trim());
        if (dpPerFechaNac.getValue() != null)
            body.put("fechaNacimiento", dpPerFechaNac.getValue().toString());
        String generoStr = cbPerGenero.getValue();
        if (generoStr != null) body.put("idGenero", Conversiones.generoAId(generoStr));

        final int idPersona = personaSeleccionada.getIdPersona();
        new Thread(() -> {
            try {
                backend.editarPersona(idPersona, body, SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarTablaPersonas();
                    mostrarInfo("Persona actualizada correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void onCrearPersona(ActionEvent e) {
        String cedula   = txtPerCedula.getText().trim();
        String nombre   = txtPerNombre.getText().trim();
        String apellido = txtPerApellido.getText().trim();
        if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            mostrarError("Cédula, Nombre y Apellido son obligatorios"); return;
        }
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("nombre", nombre);
        body.put("apellido", apellido);
        body.put("cedulaCiudadania", cedula);
        body.put("celular", txtPerCelular.getText().trim());
        body.put("correo", txtPerCorreo.getText().trim());
        body.put("usuarioLogin", cedula);
        body.put("contrasena", cedula);
        body.put("rol", "Paciente");
        String generoStr = cbPerGenero.getValue();
        if (generoStr != null) body.put("idGenero", Conversiones.generoAId(generoStr));
        if (dpPerFechaNac.getValue() != null)
            body.put("fechaNacimiento", dpPerFechaNac.getValue().toString());

        new Thread(() -> {
            try {
                backend.crearPersonaAdmin(body);
                javafx.application.Platform.runLater(() -> {
                    cargarTablaPersonas();
                    limpiarFormularioPersona();
                    mostrarInfo("Persona creada.\nUsuario: " + cedula + "\nContraseña: " + cedula);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void onEliminarPersona(ActionEvent e) {
        String cedula = txtPerCedula.getText().trim();
        if (cedula.isEmpty()) { mostrarError("Seleccione una persona primero"); return; }
        PersonaDTO target = tblPersonas.getItems().stream()
                .filter(p -> cedula.equals(p.getCedulaCiudadania()))
                .findFirst().orElse(null);
        if (target == null) { mostrarError("No se encontró la persona con cédula: " + cedula); return; }
        final int id = target.getIdPersona();
        new Thread(() -> {
            try {
                String token = SesionUsuario.getInstancia().getToken();
                backend.inactivarPersona(id, token != null ? token : "");
                javafx.application.Platform.runLater(() -> {
                    limpiarFormularioPersona();
                    cargarTablaPersonas();
                    mostrarInfo("Persona inactivada correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    private void limpiarFormularioPersona() {
        personaSeleccionada = null;
        txtPerCedula.clear(); txtPerNombre.clear(); txtPerApellido.clear();
        txtPerCelular.clear(); txtPerCorreo.clear();
        dpPerFechaNac.setValue(null); cbPerGenero.setValue(null);
        tblPersonas.getSelectionModel().clearSelection();
    }

    // ── Usuarios ──────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void configurarTablaUsuarios() {
        tblUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblUsuarios.setItems(FXCollections.observableArrayList());
        colUsuNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().get("usuario") != null ? c.getValue().get("usuario").toString() : ""));
        colUsuContrasena.setCellValueFactory(c -> new SimpleStringProperty("••••••••"));
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> {
            if (u == null) return;
            txtUsuNombre.setText(u.get("usuario") != null ? u.get("usuario").toString() : "");
            txtUsuContrasena.clear();
        });
    }

    @SuppressWarnings("unchecked")
    private void cargarTablaUsuarios() {
        new Thread(() -> {
            try {
                List<Map> usuarios = backend.listarUsuarios();
                javafx.application.Platform.runLater(() -> tblUsuarios.getItems().setAll(usuarios));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> mostrarError("Error al cargar usuarios: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    void onEditarUsuario(ActionEvent e) {
        Map seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { mostrarError("Seleccione un usuario"); return; }
        String nuevoNombre = txtUsuNombre.getText().trim();
        String nuevaClave  = txtUsuContrasena.getText().trim();
        if (nuevoNombre.isEmpty()) { mostrarError("El nombre de usuario es obligatorio"); return; }
        Object idObj = seleccionado.get("idUsuario");
        if (idObj == null) { mostrarError("No se pudo obtener el ID"); return; }
        int idUsuario = ((Number) idObj).intValue();
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("usuario", nuevoNombre);
        body.put("contrasena", nuevaClave.isEmpty()
                ? seleccionado.getOrDefault("contrasena", "").toString() : nuevaClave);
        new Thread(() -> {
            try {
                backend.editarUsuario(idUsuario, body, SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarTablaUsuarios();
                    txtUsuNombre.clear(); txtUsuContrasena.clear();
                    mostrarInfo("Usuario actualizado correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void onCrearUsuario(ActionEvent e) {
        String nombre = txtUsuNombre.getText().trim();
        String clave  = txtUsuContrasena.getText().trim();
        if (nombre.isEmpty() || clave.isEmpty()) { mostrarError("Usuario y Contraseña son obligatorios"); return; }
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("usuario", nombre);
        body.put("contrasena", clave);
        new Thread(() -> {
            try {
                backend.editarUsuario(0, body, SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarTablaUsuarios();
                    txtUsuNombre.clear(); txtUsuContrasena.clear();
                    mostrarInfo("Usuario creado correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void onEliminarUsuario(ActionEvent e) {
        Map seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { mostrarError("Seleccione un usuario"); return; }
        Object idObj = seleccionado.get("idUsuario");
        if (idObj == null) { mostrarError("No se pudo obtener el ID"); return; }
        int idUsuario = ((Number) idObj).intValue();
        new Thread(() -> {
            try {
                backend.editarUsuario(idUsuario, java.util.Map.of("activo", false),
                        SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarTablaUsuarios();
                    txtUsuNombre.clear(); txtUsuContrasena.clear();
                    mostrarInfo("Usuario eliminado correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}
