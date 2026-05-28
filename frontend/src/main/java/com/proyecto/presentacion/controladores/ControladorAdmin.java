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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ControladorAdmin implements Initializable {

    // ── Paneles ───────────────────────────────────────────────
    @FXML private VBox panelPrincipal;
    @FXML private VBox panelTurnos;
    @FXML private VBox panelPersonas;
    @FXML private VBox panelUsuarios;
    @FXML private VBox panelRoles;

    // ── Panel Especialidad ────────────────────────────────────
    @FXML private ComboBox<MedicoDTO> cbMedicoEspecialidad;
    @FXML private ComboBox<String>    cbEspecialidad;

    // ── Panel Turnos ──────────────────────────────────────────
    @FXML private ComboBox<MedicoDTO>          cbTurnoDoctor;
    @FXML private TextField                    txtTurnoCedMedico;
    @FXML private TextField                    txtTurnoNomMedico;
    @FXML private TableView<CitaDTO>            tblTurnos;
    @FXML private TableColumn<CitaDTO, String>  colTurnoCedMedico;
    @FXML private TableColumn<CitaDTO, String>  colTurnoNomMedico;
    @FXML private TableColumn<CitaDTO, String>  colTurnoFecha;
    @FXML private TableColumn<CitaDTO, String>  colTurnoHoraInicio;
    @FXML private TableColumn<CitaDTO, String>  colTurnoHoraFin;
    @FXML private TableColumn<CitaDTO, String>  colTurnoEstado;
    @FXML private ComboBox<String>     cbTurnoDia;
    @FXML private ComboBox<LocalTime>  cbTurnoHoraInicio;
    @FXML private ComboBox<LocalTime>  cbTurnoHoraFin;
    @FXML private CheckBox             chkTurnoActivo;

    // ── Panel Personas ────────────────────────────────────────
    @FXML private TableView<PersonaDTO>            tblPersonas;
    @FXML private TableColumn<PersonaDTO, String>  colPerCedula;
    @FXML private TableColumn<PersonaDTO, String>  colPerNombre;
    @FXML private TableColumn<PersonaDTO, String>  colPerApellido;
    @FXML private TableColumn<PersonaDTO, String>  colPerCelular;
    @FXML private TableColumn<PersonaDTO, String>  colPerFechaNac;
    @FXML private TableColumn<PersonaDTO, String>  colPerCorreo;
    @FXML private TableColumn<PersonaDTO, String>  colPerGenero;
    @FXML private TextField          txtPerCedula;
    @FXML private TextField          txtPerNombre;
    @FXML private TextField          txtPerApellido;
    @FXML private TextField          txtPerCelular;
    @FXML private DatePicker         dpPerFechaNac;
    @FXML private TextField          txtPerCorreo;
    @FXML private ChoiceBox<String>  cbPerGenero;

    // ── Panel Usuarios ────────────────────────────────────────
    @FXML private TableView<Map>            tblUsuarios;
    @FXML private TableColumn<Map, String>  colUsuNombre;
    @FXML private TableColumn<Map, String>  colUsuContrasena;
    @FXML private TextField txtUsuNombre;
    @FXML private TextField txtUsuContrasena;

    // ── Panel Roles ───────────────────────────────────────────
    @FXML private TableView<PersonaDTO>            tblPersonasRoles;
    @FXML private TableColumn<PersonaDTO, String>  colPerRolCedula;
    @FXML private TableColumn<PersonaDTO, String>  colPerRolNombre;
    @FXML private TableColumn<PersonaDTO, String>  colPerRolApellido;
    @FXML private TableView<Map>                   tblRolesUsuario;
    @FXML private TableColumn<Map, String>         colRolNombre;
    @FXML private ComboBox<String>                 cbRolesDisponibles;
    @FXML private Button                           btnAgregarRol;
    @FXML private Button                           btnQuitarRol;

    // ── Estado interno ────────────────────────────────────────
    private PersonaDTO personaSeleccionada    = null;
    private PersonaDTO personaRolSeleccionada = null;
    private final Map<Integer, MedicoDTO> mapaMedicos = new HashMap<>();
    private List<CitaDTO> todosTurnos = new java.util.ArrayList<>();
    private final BackendFacade backend = new BackendFacade();

    private static final List<String> DIAS_SEMANA =
            List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

    private static final List<String> ROLES_DISPONIBLES =
            List.of("Administrador", "Agendador", "Medico", "Paciente");

    private final StringConverter<MedicoDTO> convMedico = new StringConverter<>() {
        public String toString(MedicoDTO m)   { return m == null ? "" : m.toString(); }
        public MedicoDTO fromString(String s) { return null; }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombosIniciales();
        configurarTablaTurnos();
        configurarTablaPersonas();
        configurarTablaUsuarios();
        configurarTablaRoles();
        cargarTablaTurnos();
        cargarTablaPersonas();
        cargarTablaUsuarios();
    }

    // ── Navegación ────────────────────────────────────────────

    private void mostrarPanel(VBox panel) {
        panelPrincipal.setVisible(false); panelTurnos.setVisible(false);
        panelPersonas.setVisible(false);  panelUsuarios.setVisible(false);
        panelRoles.setVisible(false);
        panel.setVisible(true); panel.toFront();
    }

    @FXML void onAbrirTurnos(ActionEvent e)      { mostrarPanel(panelTurnos);   cargarTablaTurnos(); }
    @FXML void onAbrirPersonas(ActionEvent e)    { mostrarPanel(panelPersonas); cargarTablaPersonas(); }
    @FXML void onAbrirUsuarios(ActionEvent e)    { mostrarPanel(panelUsuarios); cargarTablaUsuarios(); }
    @FXML void onAbrirRoles(ActionEvent e)       { mostrarPanel(panelRoles);    cargarTablaPersonasRoles(); }
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

    // ── Carga inicial ─────────────────────────────────────────

    private void cargarCombosIniciales() {
        try {
            List<MedicoDTO> medicos = backend.listarMedicosActivos();
            mapaMedicos.clear();
            for (MedicoDTO m : medicos) mapaMedicos.put(m.getIdMedico(), m);

            cbMedicoEspecialidad.setItems(FXCollections.observableArrayList(medicos));
            cbMedicoEspecialidad.setConverter(convMedico);

            List<Map> especialidades = backend.listarEspecialidades();
            cbEspecialidad.setItems(FXCollections.observableArrayList(
                    especialidades.stream().map(esp -> esp.get("nombre").toString()).toList()));

            cbTurnoDoctor.setItems(FXCollections.observableArrayList(medicos));
            cbTurnoDoctor.setConverter(convMedico);

            // Días de la semana en lugar de DatePicker
            cbTurnoDia.setItems(FXCollections.observableArrayList(DIAS_SEMANA));

            cbPerGenero.setItems(FXCollections.observableArrayList(
                    "Masculino", "Femenino", "No Binario", "Prefiero no decir"));

            List<LocalTime> horas = new java.util.ArrayList<>();
            for (int h = 7; h <= 20; h++) {
                horas.add(LocalTime.of(h, 0));
                if (h < 20) horas.add(LocalTime.of(h, 30));
            }
            cbTurnoHoraInicio.setItems(FXCollections.observableArrayList(horas));
            cbTurnoHoraFin.setItems(FXCollections.observableArrayList(horas));
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Especialidad ──────────────────────────────────────────

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
            if (medico.getIdEspecialidad() == idEsp) {
                mostrarAdvertencia("El médico " + medico.getNombre() + " " + medico.getApellido()
                        + " ya tiene asignada esa especialidad.");
                return;
            }
            backend.asignarEspecialidad(medico.getIdMedico(), idEsp, SesionUsuario.getInstancia().getToken());
            medico.setIdEspecialidad(idEsp);
            mostrarInfo("Especialidad '" + esp + "' asignada a "
                    + medico.getNombre() + " " + medico.getApellido() + " correctamente.");
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
            txtTurnoCedMedico.clear(); txtTurnoNomMedico.clear();
            tblTurnos.setItems(FXCollections.observableArrayList(todosTurnos));
        }
        cbTurnoDia.setValue(null);
        cbTurnoHoraInicio.setValue(null); cbTurnoHoraFin.setValue(null);
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
        // Mostrar día de la semana derivado de la fecha almacenada
        colTurnoFecha.setCellValueFactory(c -> {
            if (c.getValue().getFecha() == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(
                    Conversiones.traducirDia(c.getValue().getFecha().getDayOfWeek()));
        });
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
            // Mostrar el día de la semana de la fecha almacenada
            if (cita.getFecha() != null) {
                cbTurnoDia.setValue(Conversiones.traducirDia(cita.getFecha().getDayOfWeek()));
            }
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

    /**
     * Convierte el día de la semana en español al próximo LocalDate de ese día.
     * Ej: "Martes" → el próximo martes desde hoy.
     */
    private LocalDate proximaFechaDeDia(String diaSemana) {
        java.time.DayOfWeek dow = switch (diaSemana) {
            case "Lunes"     -> java.time.DayOfWeek.MONDAY;
            case "Martes"    -> java.time.DayOfWeek.TUESDAY;
            case "Miércoles" -> java.time.DayOfWeek.WEDNESDAY;
            case "Jueves"    -> java.time.DayOfWeek.THURSDAY;
            case "Viernes"   -> java.time.DayOfWeek.FRIDAY;
            case "Sábado"    -> java.time.DayOfWeek.SATURDAY;
            default          -> java.time.DayOfWeek.SUNDAY;
        };
        LocalDate hoy = LocalDate.now();
        int diasHasta = (dow.getValue() - hoy.getDayOfWeek().getValue() + 7) % 7;
        return hoy.plusDays(diasHasta == 0 ? 7 : diasHasta);
    }

    @FXML
    void onCrearTurno(ActionEvent e) {
        MedicoDTO doctor = cbTurnoDoctor.getValue();
        String dia = cbTurnoDia.getValue();
        if (doctor == null || dia == null
                || cbTurnoHoraInicio.getValue() == null || cbTurnoHoraFin.getValue() == null) {
            mostrarError("Complete: Doctor, Día, Hora Inicio y Hora Final"); return;
        }
        try {
            CitaDTO nuevo = new CitaDTO();
            nuevo.setIdMedico(doctor.getIdMedico());
            // Usar el próximo día de la semana seleccionado como fecha representativa
            nuevo.setFecha(proximaFechaDeDia(dia));
            nuevo.setHoraInicio(cbTurnoHoraInicio.getValue());
            nuevo.setHoraFin(cbTurnoHoraFin.getValue());
            nuevo.setIdEstadoCita(chkTurnoActivo.isSelected() ? 1 : 2);
            backend.crearCitaManual(nuevo, SesionUsuario.getInstancia().getToken());
            cargarTablaTurnos();
            limpiarFormularioTurno();
            mostrarInfo("Turno del " + dia + " creado correctamente.");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @FXML
    void onEditarTurno(ActionEvent e) {
        CitaDTO cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno"); return; }
        try {
            MedicoDTO doctor = cbTurnoDoctor.getValue();
            if (doctor != null) cita.setIdMedico(doctor.getIdMedico());
            String dia = cbTurnoDia.getValue();
            if (dia != null) cita.setFecha(proximaFechaDeDia(dia));
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
        cbTurnoDia.setValue(null);
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
                javafx.application.Platform.runLater(() -> { cargarTablaPersonas(); mostrarInfo("Persona actualizada"); });
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
            body.put("fechaNacimiento", dpPerFechaNac.getValue());
        new Thread(() -> {
            try {
                backend.crearPersonaAdmin(body);
                javafx.application.Platform.runLater(() -> {
                    cargarTablaPersonas(); limpiarFormularioPersona();
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
                .filter(p -> cedula.equals(p.getCedulaCiudadania())).findFirst().orElse(null);
        if (target == null) { mostrarError("No se encontró la persona con cédula: " + cedula); return; }
        final int id = target.getIdPersona();
        new Thread(() -> {
            try {
                String token = SesionUsuario.getInstancia().getToken();
                backend.inactivarPersona(id, token != null ? token : "");
                javafx.application.Platform.runLater(() -> {
                    limpiarFormularioPersona(); cargarTablaPersonas();
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
                    cargarTablaUsuarios(); txtUsuNombre.clear(); txtUsuContrasena.clear();
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
        new Thread(() -> {
            try {
                backend.crearUsuario(nombre, clave, SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarTablaUsuarios(); txtUsuNombre.clear(); txtUsuContrasena.clear();
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
        if (seleccionado == null) { mostrarError("Seleccione un usuario de la lista"); return; }
        Object idObj = seleccionado.get("idUsuario");
        if (idObj == null) { mostrarError("No se pudo obtener el ID del usuario"); return; }
        int idUsuario = ((Number) idObj).intValue();
        new Thread(() -> {
            try {
                backend.eliminarUsuario(idUsuario, SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarTablaUsuarios(); txtUsuNombre.clear(); txtUsuContrasena.clear();
                    mostrarInfo("Usuario eliminado correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    // ── Roles ─────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void configurarTablaRoles() {
        tblPersonasRoles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblPersonasRoles.setItems(FXCollections.observableArrayList());
        colPerRolCedula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCedulaCiudadania()));
        colPerRolNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPerRolApellido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellido()));

        tblRolesUsuario.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblRolesUsuario.setItems(FXCollections.observableArrayList());
        colRolNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().get("nombre") != null ? c.getValue().get("nombre").toString() : ""));

        tblPersonasRoles.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            personaRolSeleccionada = p;
            btnAgregarRol.setDisable(p == null);
            if (p != null) cargarRolesDePersona(p);
            else { tblRolesUsuario.getItems().clear(); cbRolesDisponibles.getItems().setAll(ROLES_DISPONIBLES); }
        });

        tblRolesUsuario.getSelectionModel().selectedItemProperty().addListener((obs, old, r) ->
                btnQuitarRol.setDisable(r == null));
    }

    private void cargarTablaPersonasRoles() {
        new Thread(() -> {
            try {
                List<PersonaDTO> personas = backend.listarPersonas();
                javafx.application.Platform.runLater(() -> {
                    tblPersonasRoles.getItems().setAll(personas);
                    cbRolesDisponibles.setItems(FXCollections.observableArrayList(ROLES_DISPONIBLES));
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> mostrarError("Error al cargar personas: " + e.getMessage()));
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    private void cargarRolesDePersona(PersonaDTO persona) {
        if (persona.getIdUsuario() == null) {
            tblRolesUsuario.getItems().clear();
            cbRolesDisponibles.getItems().setAll(ROLES_DISPONIBLES);
            return;
        }
        new Thread(() -> {
            try {
                String json = com.proyecto.presentacion.ClienteHttp.get(
                        "/api/roles/usuario/" + persona.getIdUsuario());
                List<Map> roles = com.proyecto.presentacion.ClienteHttp.parsearLista(json, Map.class);
                List<String> rolesYaAsignados = roles.stream()
                        .filter(r -> r.get("nombre") != null)
                        .map(r -> r.get("nombre").toString().toLowerCase())
                        .toList();
                List<String> disponibles = ROLES_DISPONIBLES.stream()
                        .filter(r -> !rolesYaAsignados.contains(r.toLowerCase()))
                        .toList();
                javafx.application.Platform.runLater(() -> {
                    tblRolesUsuario.getItems().setAll(roles);
                    cbRolesDisponibles.getItems().setAll(disponibles);
                    cbRolesDisponibles.setValue(null);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    tblRolesUsuario.getItems().clear();
                    cbRolesDisponibles.getItems().setAll(ROLES_DISPONIBLES);
                });
            }
        }).start();
    }

    @FXML
    void onAgregarRol(ActionEvent e) {
        if (personaRolSeleccionada == null) { mostrarError("Seleccione una persona"); return; }
        if (personaRolSeleccionada.getIdUsuario() == null) { mostrarError("La persona no tiene usuario asociado"); return; }
        String rol = cbRolesDisponibles.getValue();
        if (rol == null) { mostrarError("Seleccione un rol"); return; }
        try {
            backend.asignarRol(personaRolSeleccionada.getIdUsuario(), rol, SesionUsuario.getInstancia().getToken());
            cargarRolesDePersona(personaRolSeleccionada);
            mostrarInfo("Rol '" + rol + "' asignado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    @FXML
    void onQuitarRol(ActionEvent e) {
        Map rolSeleccionado = tblRolesUsuario.getSelectionModel().getSelectedItem();
        if (rolSeleccionado == null) { mostrarError("Seleccione un rol de la lista"); return; }
        Object idRolObj = rolSeleccionado.get("idRol");
        if (idRolObj == null) { mostrarError("No se pudo obtener el ID del rol"); return; }
        int idRol = ((Number) idRolObj).intValue();
        try {
            com.proyecto.presentacion.ClienteHttp.delete("/api/roles/" + idRol, SesionUsuario.getInstancia().getToken());
            if (personaRolSeleccionada != null) cargarRolesDePersona(personaRolSeleccionada);
            mostrarInfo("Rol eliminado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    // ── Utilidades ────────────────────────────────────────────

    private void mostrarError(String msg)       { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg)        { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
    private void mostrarAdvertencia(String msg) { new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
}
