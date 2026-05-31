
package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.facade.BackendFacade;
import com.proyecto.presentacion.util.Conversiones;
import com.proyecto.presentacion.dto.JornadaDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;
import com.proyecto.presentacion.dto.RolDTO;

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
    @FXML private VBox panelRoles;

    // ── Gestión de Roles (Panel de Administración) ────────────────────────────
    @FXML private ComboBox<PersonaDTO> cbPersonaRol;     // DEPRECATED: usar tabla en su lugar
    @FXML private ComboBox<String>     cbRol;            // DEPRECATED: usar tabla en su lugar
    
    // Nueva tabla para gestión de roles por usuario
    @FXML private TableView<PersonaDTO>           tblPersonasRoles;
    @FXML private TableColumn<PersonaDTO, String> colPerRolCedula;
    @FXML private TableColumn<PersonaDTO, String> colPerRolNombre;
    @FXML private TableColumn<PersonaDTO, String> colPerRolApellido;
    
    @FXML private TableView<RolDTO>              tblRolesUsuario;
    @FXML private TableColumn<RolDTO, String>    colRolNombre;
    
    @FXML private ComboBox<String>   cbRolesDisponibles;
    @FXML private Button             btnAgregarRol;
    @FXML private Button             btnQuitarRol;
    @FXML private ComboBox<MedicoDTO>  cbMedicoEspecialidad;
    @FXML private ComboBox<String>     cbEspecialidad;

    @FXML private ComboBox<MedicoDTO>         cbTurnoDoctor;
    @FXML private TextField                   txtTurnoCedMedico;
    @FXML private TextField                   txtTurnoNomMedico;
    @FXML private TableView<JornadaDTO>           tblTurnos;
    @FXML private TableColumn<JornadaDTO, String> colTurnoCedMedico;
    @FXML private TableColumn<JornadaDTO, String> colTurnoNomMedico;
    @FXML private TableColumn<JornadaDTO, String> colTurnoDiaSemana;
    @FXML private TableColumn<JornadaDTO, String> colTurnoHoraInicio;
    @FXML private TableColumn<JornadaDTO, String> colTurnoHoraFin;
    @FXML private TableColumn<JornadaDTO, String> colTurnoEstado;
    @FXML private ComboBox<String>        cbTurnoDiaSemana;
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
    private PersonaDTO personaSeleccionadaParaRoles = null;
    private final Map<Integer, MedicoDTO> mapaMedicos = new HashMap<>();
    private final Map<Integer, PersonaDTO> mapaPersonasPorIdUsuario = new HashMap<>();
    private final Map<Integer, PersonaDTO> mapaPersonasPorIdPersona = new HashMap<>();
    private List<JornadaDTO> todosTurnos = new java.util.ArrayList<>();
    private List<RolDTO> rolesActualesUsuario = new java.util.ArrayList<>();
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
        configurarTablaPersonasRoles();
        configurarTablaRolesUsuario();
        cargarTablaTurnos();
        cargarTablaPersonas();
        cargarTablaUsuarios();
        cargarTablaPersonasRoles();
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
            mapaPersonasPorIdUsuario.clear();
            mapaPersonasPorIdPersona.clear();
            for (PersonaDTO p : personas) {
                mapaPersonasPorIdPersona.put(p.getIdPersona(), p);
                if (p.getIdUsuario() != null) {
                    mapaPersonasPorIdUsuario.put(p.getIdUsuario(), p);
                }
            }
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
            cbTurnoDiaSemana.setItems(FXCollections.observableArrayList(
                    "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"));
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

    // ── Gestión de Roles (Nueva Tabla) ────────────────────────────────────────

    private void configurarTablaPersonasRoles() {
        tblPersonasRoles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblPersonasRoles.setItems(FXCollections.observableArrayList());
        
        colPerRolCedula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCedulaCiudadania()));
        colPerRolNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPerRolApellido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellido()));
        
        tblPersonasRoles.getSelectionModel().selectedItemProperty().addListener((obs, old, persona) -> {
            personaSeleccionadaParaRoles = persona;
            if (persona == null) {
                tblRolesUsuario.getItems().clear();
                cbRolesDisponibles.setValue(null);
                btnAgregarRol.setDisable(true);
                btnQuitarRol.setDisable(true);
            } else if (persona.getIdUsuario() != null) {
                btnAgregarRol.setDisable(false);
                btnQuitarRol.setDisable(false);
                cargarRolesDeUsuario(persona.getIdUsuario());
            } else {
                tblRolesUsuario.getItems().clear();
                cbRolesDisponibles.setValue(null);
                mostrarError("Esta persona no tiene usuario asociado");
            }
        });
    }

    private void cargarTablaPersonasRoles() {
        new Thread(() -> {
            try {
                List<PersonaDTO> personas = backend.listarPersonas();
                javafx.application.Platform.runLater(() -> tblPersonasRoles.getItems().setAll(personas));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> mostrarError("Error al cargar personas para roles: " + e.getMessage()));
            }
        }).start();
    }

    private void configurarTablaRolesUsuario() {
        tblRolesUsuario.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblRolesUsuario.setItems(FXCollections.observableArrayList());
        colRolNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        
        cbRolesDisponibles.setItems(FXCollections.observableArrayList("Administrador", "Agendador", "Medico", "Paciente"));
    }

    private void cargarRolesDeUsuario(int idUsuario) {
        new Thread(() -> {
            try {
                List<RolDTO> roles = backend.listarRolesDeUsuario(idUsuario);
                rolesActualesUsuario = roles;
                javafx.application.Platform.runLater(() -> {
                    tblRolesUsuario.getItems().setAll(roles);
                    // Actualizar combo de roles disponibles mostrando solo los no asignados
                    List<String> rolesDisponibles = new java.util.ArrayList<>(java.util.Arrays.asList("Administrador", "Agendador", "Medico", "Paciente"));
                    for (RolDTO rol : roles) {
                        rolesDisponibles.remove(rol.getNombre());
                    }
                    cbRolesDisponibles.setItems(FXCollections.observableArrayList(rolesDisponibles));
                    cbRolesDisponibles.setValue(null);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> mostrarError("Error al cargar roles: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    void onAgregarRol(ActionEvent e) {
        if (personaSeleccionadaParaRoles == null) { mostrarError("Seleccione una persona"); return; }
        if (personaSeleccionadaParaRoles.getIdUsuario() == null) { mostrarError("La persona no tiene usuario asociado"); return; }
        
        String rolSeleccionado = cbRolesDisponibles.getValue();
        if (rolSeleccionado == null || rolSeleccionado.isEmpty()) { mostrarError("Seleccione un rol"); return; }
        
        // Validar que el rol no esté duplicado
        boolean rolYaAsignado = rolesActualesUsuario.stream()
                .anyMatch(r -> r.getNombre().equals(rolSeleccionado));
        if (rolYaAsignado) { mostrarError("Este rol ya está asignado al usuario"); return; }
        
        new Thread(() -> {
            try {
                backend.asignarRol(personaSeleccionadaParaRoles.getIdUsuario(), rolSeleccionado, 
                                  SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarRolesDeUsuario(personaSeleccionadaParaRoles.getIdUsuario());
                    mostrarInfo("Rol '" + rolSeleccionado + "' asignado correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void onQuitarRol(ActionEvent e) {
        if (personaSeleccionadaParaRoles == null) { mostrarError("Seleccione una persona"); return; }
        
        RolDTO rolSeleccionado = tblRolesUsuario.getSelectionModel().getSelectedItem();
        if (rolSeleccionado == null) { mostrarError("Seleccione un rol de la tabla para eliminar"); return; }
        
        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de que desea quitar el rol '" + rolSeleccionado.getNombre() + "'?",
                ButtonType.YES, ButtonType.NO);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) return;
        
        new Thread(() -> {
            try {
                backend.eliminarRol(rolSeleccionado.getIdRol(), SesionUsuario.getInstancia().getToken());
                javafx.application.Platform.runLater(() -> {
                    cargarRolesDeUsuario(personaSeleccionadaParaRoles.getIdUsuario());
                    mostrarInfo("Rol eliminado correctamente");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> mostrarError("Error: " + ex.getMessage()));
            }
        }).start();
    }

    // ── Turnos (jornadas laborales) ───────────────────────────

    private PersonaDTO personaDeMedico(MedicoDTO medico) {
        return medico == null ? null : mapaPersonasPorIdPersona.get(medico.getIdMedico());
    }

    private MedicoDTO medicoDeJornada(JornadaDTO jornada) {
        PersonaDTO persona = mapaPersonasPorIdUsuario.get(jornada.getIdUsuario());
        return persona == null ? null : mapaMedicos.get(persona.getIdPersona());
    }

    private Integer idUsuarioDeMedico(MedicoDTO medico) {
        PersonaDTO persona = personaDeMedico(medico);
        return persona != null ? persona.getIdUsuario() : null;
    }

    @FXML
    void onSeleccionarDoctor(ActionEvent e) {
        MedicoDTO sel = cbTurnoDoctor.getValue();
        if (sel != null) {
            PersonaDTO persona = personaDeMedico(sel);
            if (persona != null) {
                txtTurnoCedMedico.setText(persona.getCedulaCiudadania());
                txtTurnoNomMedico.setText(persona.getNombre() + " " + persona.getApellido());
            } else {
                txtTurnoCedMedico.clear();
                txtTurnoNomMedico.setText(sel.getNombre() + " " + sel.getApellido());
            }
            Integer idUsuario = idUsuarioDeMedico(sel);
            if (idUsuario != null) {
                tblTurnos.setItems(FXCollections.observableArrayList(
                        todosTurnos.stream().filter(j -> j.getIdUsuario() == idUsuario).toList()));
            } else {
                tblTurnos.setItems(FXCollections.observableArrayList());
            }
        } else {
            txtTurnoCedMedico.clear();
            txtTurnoNomMedico.clear();
            tblTurnos.setItems(FXCollections.observableArrayList(todosTurnos));
        }
        cbTurnoDiaSemana.setValue(null);
        cbTurnoHoraInicio.setValue(null);
        cbTurnoHoraFin.setValue(null);
        chkTurnoActivo.setSelected(false);
    }

    private void configurarTablaTurnos() {
        colTurnoCedMedico.setCellValueFactory(c -> {
            PersonaDTO p = mapaPersonasPorIdUsuario.get(c.getValue().getIdUsuario());
            return new SimpleStringProperty(p != null ? p.getCedulaCiudadania() : "—");
        });
        colTurnoNomMedico.setCellValueFactory(c -> {
            PersonaDTO p = mapaPersonasPorIdUsuario.get(c.getValue().getIdUsuario());
            return new SimpleStringProperty(p != null ? p.getNombre() + " " + p.getApellido() : "—");
        });
        colTurnoDiaSemana.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDiaSemana() != null ? c.getValue().getDiaSemana() : ""));
        colTurnoHoraInicio.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraInicio() != null
                        ? c.getValue().getHoraInicio().toString().substring(0, 5) : ""));
        colTurnoHoraFin.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraFin() != null
                        ? c.getValue().getHoraFin().toString().substring(0, 5) : ""));
        colTurnoEstado.setCellValueFactory(c -> {
            int estado = c.getValue().getIdEstado();
            return new SimpleStringProperty(estado == 1 ? "Activo" : "Inactivo");
        });
        tblTurnos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblTurnos.getSelectionModel().selectedItemProperty().addListener((obs, old, jornada) -> {
            if (jornada == null) return;
            cbTurnoDiaSemana.setValue(jornada.getDiaSemana());
            cbTurnoHoraInicio.setValue(jornada.getHoraInicio());
            cbTurnoHoraFin.setValue(jornada.getHoraFin());
            chkTurnoActivo.setSelected(jornada.getIdEstado() == 1);
            MedicoDTO medico = medicoDeJornada(jornada);
            PersonaDTO persona = mapaPersonasPorIdUsuario.get(jornada.getIdUsuario());
            if (medico != null) {
                cbTurnoDoctor.setValue(medico);
            } else {
                cbTurnoDoctor.setValue(null);
            }
            if (persona != null) {
                txtTurnoCedMedico.setText(persona.getCedulaCiudadania());
                txtTurnoNomMedico.setText(persona.getNombre() + " " + persona.getApellido());
            } else {
                txtTurnoCedMedico.setText("Usuario: " + jornada.getIdUsuario());
                txtTurnoNomMedico.clear();
            }
        });
    }

    private void cargarTablaTurnos() {
        new Thread(() -> {
            try {
                List<JornadaDTO> jornadas = backend.listarJornadas();
                List<PersonaDTO> personas = backend.listarPersonas();
                todosTurnos = jornadas;
                javafx.application.Platform.runLater(() -> {
                    mapaPersonasPorIdUsuario.clear();
                    mapaPersonasPorIdPersona.clear();
                    for (PersonaDTO p : personas) {
                        mapaPersonasPorIdPersona.put(p.getIdPersona(), p);
                        if (p.getIdUsuario() != null) {
                            mapaPersonasPorIdUsuario.put(p.getIdUsuario(), p);
                        }
                    }
                    MedicoDTO sel = cbTurnoDoctor.getValue();
                    Integer idUsuario = idUsuarioDeMedico(sel);
                    List<JornadaDTO> mostrar = (idUsuario != null)
                            ? jornadas.stream().filter(j -> j.getIdUsuario() == idUsuario).toList()
                            : jornadas;
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
        if (doctor == null || cbTurnoDiaSemana.getValue() == null
                || cbTurnoHoraInicio.getValue() == null || cbTurnoHoraFin.getValue() == null) {
            mostrarError("Complete: Doctor, Día de la Semana, Hora Inicio y Hora Final"); return;
        }
        Integer idUsuario = idUsuarioDeMedico(doctor);
        if (idUsuario == null) {
            mostrarError("El doctor seleccionado no tiene usuario asociado"); return;
        }
        try {
            JornadaDTO nuevo = new JornadaDTO();
            nuevo.setIdUsuario(idUsuario);
            nuevo.setDiaSemana(cbTurnoDiaSemana.getValue());
            nuevo.setHoraInicio(cbTurnoHoraInicio.getValue());
            nuevo.setHoraFin(cbTurnoHoraFin.getValue());
            nuevo.setIdEstado(chkTurnoActivo.isSelected() ? 1 : 2);
            backend.crearJornada(nuevo);
            cargarTablaTurnos();
            limpiarFormularioTurno();
            mostrarInfo("Turno creado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @FXML
    void onEditarTurno(ActionEvent e) {
        JornadaDTO jornada = tblTurnos.getSelectionModel().getSelectedItem();
        if (jornada == null) { mostrarError("Seleccione un turno"); return; }
        MedicoDTO doctor = cbTurnoDoctor.getValue();
        if (doctor == null) { mostrarError("Seleccione un doctor"); return; }
        Integer idUsuario = idUsuarioDeMedico(doctor);
        if (idUsuario == null) {
            mostrarError("El doctor seleccionado no tiene usuario asociado"); return;
        }
        try {
            jornada.setIdUsuario(idUsuario);
            if (cbTurnoDiaSemana.getValue() != null) jornada.setDiaSemana(cbTurnoDiaSemana.getValue());
            if (cbTurnoHoraInicio.getValue() != null) jornada.setHoraInicio(cbTurnoHoraInicio.getValue());
            if (cbTurnoHoraFin.getValue() != null) jornada.setHoraFin(cbTurnoHoraFin.getValue());
            jornada.setIdEstado(chkTurnoActivo.isSelected() ? 1 : 2);
            backend.editarJornada(jornada);
            cargarTablaTurnos();
            mostrarInfo("Turno actualizado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    @FXML
    void onEliminarTurno(ActionEvent e) {
        JornadaDTO jornada = tblTurnos.getSelectionModel().getSelectedItem();
        if (jornada == null) { mostrarError("Seleccione un turno"); return; }
        try {
            backend.eliminarJornada(jornada.getIdJornada());
            cargarTablaTurnos();
            limpiarFormularioTurno();
            mostrarInfo("Turno eliminado correctamente");
        } catch (Exception ex) { mostrarError("Error: " + ex.getMessage()); }
    }

    private void limpiarFormularioTurno() {
        cbTurnoDoctor.setValue(null);
        txtTurnoCedMedico.clear(); txtTurnoNomMedico.clear();
        cbTurnoDiaSemana.setValue(null);
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
