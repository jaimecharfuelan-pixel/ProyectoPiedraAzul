package com.proyecto.presentacion.controladores;

import com.proyecto.logica.modelos.*;
import com.proyecto.logica.servicios.ServicioConfiguracion;
import com.proyecto.logica.servicios.ServicioUsuarios;
import com.proyecto.persistencia.repositorios.*;
import com.proyecto.presentacion.SesionUsuario;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class ControladorAdmin implements Initializable {

    // ── Paneles ──────────────────────────────────────────────
    @FXML private AnchorPane panelPrincipal;
    @FXML private AnchorPane panelTurnos;
    @FXML private AnchorPane panelPersonas;
    @FXML private AnchorPane panelUsuarios;

    // ── Panel principal – combos ──────────────────────────────
    @FXML private ComboBox<MedicoTerapista> cbDoctorTurno;
    @FXML private ComboBox<Persona>         cbPersonaRol;
    @FXML private ComboBox<String>          cbRol;
    @FXML private ComboBox<MedicoTerapista> cbMedicoEspecialidad;
    @FXML private ComboBox<DominioEspecialidad> cbEspecialidad;

    // ── Panel Turnos ──────────────────────────────────────────
    @FXML private TableView<Cita>           tblTurnos;
    @FXML private TableColumn<Cita,String>  colTurnoCedPaciente;
    @FXML private TableColumn<Cita,String>  colTurnoNomPaciente;
    @FXML private TableColumn<Cita,String>  colTurnoCedMedico;
    @FXML private TableColumn<Cita,String>  colTurnoNomMedico;
    @FXML private TableColumn<Cita,String>  colTurnoFecha;
    @FXML private TableColumn<Cita,String>  colTurnoHoraInicio;
    @FXML private TableColumn<Cita,String>  colTurnoHoraFin;
    @FXML private TableColumn<Cita,String>  colTurnoEstado;
    @FXML private TextField                 txtTurnoCedPaciente;
    @FXML private TextField                 txtTurnoNomPaciente;
    @FXML private TextField                 txtTurnoCedMedico;
    @FXML private TextField                 txtTurnoNomMedico;
    @FXML private DatePicker                dpTurnoFecha;
    @FXML private ComboBox<LocalTime>       cbTurnoHoraInicio;
    @FXML private ComboBox<LocalTime>       cbTurnoHoraFin;
    @FXML private CheckBox                  chkTurnoActivo;

    // ── Panel Personas ────────────────────────────────────────
    @FXML private TableView<Persona>        tblPersonas;
    @FXML private TableColumn<Persona,String> colPerCedula;
    @FXML private TableColumn<Persona,String> colPerNombre;
    @FXML private TableColumn<Persona,String> colPerApellido;
    @FXML private TableColumn<Persona,String> colPerCelular;
    @FXML private TableColumn<Persona,String> colPerFechaNac;
    @FXML private TableColumn<Persona,String> colPerCorreo;
    @FXML private TableColumn<Persona,String> colPerGenero;
    @FXML private TextField                 txtPerCedula;
    @FXML private TextField                 txtPerNombre;
    @FXML private TextField                 txtPerApellido;
    @FXML private TextField                 txtPerCelular;
    @FXML private DatePicker                dpPerFechaNac;
    @FXML private TextField                 txtPerCorreo;
    @FXML private ChoiceBox<String>         cbPerGenero;

    // ── Panel Usuarios ────────────────────────────────────────
    @FXML private TableView<Usuario>        tblUsuarios;
    @FXML private TableColumn<Usuario,String> colUsuNombre;
    @FXML private TableColumn<Usuario,String> colUsuContrasena;
    @FXML private TextField                 txtUsuNombre;
    @FXML private TextField                 txtUsuContrasena;

    // ── Repositorios / Servicios ──────────────────────────────
    private RepositorioMedicoTerapista repoMedico;
    private RepositorioPersona         repoPersona;
    private RepositorioUsuario         repoUsuario;
    private RepositorioCitas           repoCitas;
    private RepositorioDominioEspecialidad repoEspecialidad;
    private ServicioUsuarios           servicioUsuarios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        repoMedico       = new RepositorioMedicoTerapista();
        repoPersona      = new RepositorioPersona();
        repoUsuario      = new RepositorioUsuario();
        repoCitas        = new RepositorioCitas();
        repoEspecialidad = new RepositorioDominioEspecialidad();
        servicioUsuarios = new ServicioUsuarios(repoUsuario);

        cargarCombosPrincipal();
        configurarTablaTurnos();
        configurarTablaPersonas();
        configurarTablaUsuarios();
        cargarTablaTurnos();
        cargarTablaPersonas();
        cargarTablaUsuarios();
    }

    // ══════════════════════════════════════════════════════════
    // NAVEGACIÓN ENTRE PANELES
    // ══════════════════════════════════════════════════════════

    private void mostrarPanel(AnchorPane panel) {
        panelPrincipal.setVisible(false);
        panelTurnos.setVisible(false);
        panelPersonas.setVisible(false);
        panelUsuarios.setVisible(false);
        panel.setVisible(true);
        panel.toFront();
    }

    @FXML void onAbrirTurnos(ActionEvent e)   { mostrarPanel(panelTurnos);   cargarTablaTurnos(); }
    @FXML void onAbrirPersonas(ActionEvent e) { mostrarPanel(panelPersonas); cargarTablaPersonas(); }
    @FXML void onAbrirUsuarios(ActionEvent e) { mostrarPanel(panelUsuarios); cargarTablaUsuarios(); }

    @FXML
    void onVolverDesdePanel(ActionEvent e) {
        mostrarPanel(panelPrincipal);
    }

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

    // ══════════════════════════════════════════════════════════
    // COMBOS PANEL PRINCIPAL
    // ══════════════════════════════════════════════════════════

    private void cargarCombosPrincipal() {
        // Médicos activos en combo de Turnos
        List<MedicoTerapista> activos = repoMedico.listarActivos();
        StringConverter<MedicoTerapista> convMedico = new StringConverter<>() {
            public String toString(MedicoTerapista m) { return m == null ? "" : m.getNombre() + " " + m.getApellido(); }
            public MedicoTerapista fromString(String s) { return null; }
        };
        cbDoctorTurno.setItems(FXCollections.observableArrayList(activos));
        cbDoctorTurno.setConverter(convMedico);

        // Médicos activos en combo de Especialidad
        cbMedicoEspecialidad.setItems(FXCollections.observableArrayList(activos));
        cbMedicoEspecialidad.setConverter(convMedico);

        // Especialidades
        cbEspecialidad.setItems(FXCollections.observableArrayList(repoEspecialidad.listar()));
        cbEspecialidad.setConverter(new StringConverter<>() {
            public String toString(DominioEspecialidad d) { return d == null ? "" : d.getNombre(); }
            public DominioEspecialidad fromString(String s) { return null; }
        });

        // Personas para asignar rol
        cbPersonaRol.setItems(FXCollections.observableArrayList(repoPersona.listar()));
        cbPersonaRol.setConverter(new StringConverter<>() {
            public String toString(Persona p) { return p == null ? "" : p.getNombre() + " " + p.getApellido(); }
            public Persona fromString(String s) { return null; }
        });

        // Roles disponibles
        cbRol.setItems(FXCollections.observableArrayList("Administrador", "Agendador", "Medico", "Paciente"));

        // Géneros en form persona
        cbPerGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "No Binario", "Prefiero no decir"));
    }

    @FXML
    void onAsignarRol(ActionEvent e) {
        Persona persona = cbPersonaRol.getValue();
        String rol = cbRol.getValue();
        if (persona == null || rol == null) { mostrarError("Seleccione persona y rol"); return; }
        RepositorioRol repoRol = new RepositorioRol();
        repoRol.eliminarPorUsuario(persona.getIdUsuario() != null ? persona.getIdUsuario() : 0);
        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(rol);
        nuevoRol.setIdUsuario(persona.getIdUsuario() != null ? persona.getIdUsuario() : 0);
        repoRol.guardar(nuevoRol);
        mostrarInfo("Rol asignado correctamente");
    }

    @FXML
    void onAsignarEspecialidad(ActionEvent e) {
        MedicoTerapista medico = cbMedicoEspecialidad.getValue();
        DominioEspecialidad esp = cbEspecialidad.getValue();
        if (medico == null || esp == null) { mostrarError("Seleccione médico y especialidad"); return; }
        medico.setIdEspecialidad(esp.getIdEspecialidad());
        repoMedico.actualizar(medico);
        mostrarInfo("Especialidad asignada correctamente");
        cargarCombosPrincipal();
    }

    // ══════════════════════════════════════════════════════════
    // TURNOS (CITAS)
    // ══════════════════════════════════════════════════════════

    private void configurarTablaTurnos() {
        colTurnoCedPaciente.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdPaciente())));
        colTurnoNomPaciente.setCellValueFactory(c -> {
            Persona p = repoPersona.buscarPorId(c.getValue().getIdPaciente());
            return new SimpleStringProperty(p != null ? p.getNombre() + " " + p.getApellido() : "");
        });
        colTurnoCedMedico.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdMedico())));
        colTurnoNomMedico.setCellValueFactory(c -> {
            Persona p = repoPersona.buscarPorId(c.getValue().getIdMedico());
            return new SimpleStringProperty(p != null ? p.getNombre() + " " + p.getApellido() : "");
        });
        colTurnoFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha() != null ? c.getValue().getFecha().toString() : ""));
        colTurnoHoraInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraInicio() != null ? c.getValue().getHoraInicio().toString() : ""));
        colTurnoHoraFin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraFin() != null ? c.getValue().getHoraFin().toString() : ""));
        colTurnoEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdEstadoCita() != null ? String.valueOf(c.getValue().getIdEstadoCita()) : ""));

        // Al seleccionar fila, llenar formulario
        tblTurnos.getSelectionModel().selectedItemProperty().addListener((obs, old, cita) -> {
            if (cita != null) llenarFormTurno(cita);
        });
    }

    private void cargarTablaTurnos() {
        tblTurnos.setItems(FXCollections.observableArrayList(repoCitas.listar()));
    }

    private void llenarFormTurno(Cita c) {
        Persona pac = repoPersona.buscarPorId(c.getIdPaciente());
        Persona med = repoPersona.buscarPorId(c.getIdMedico());
        txtTurnoCedPaciente.setText(pac != null ? pac.getCedulaCiudadania() : "");
        txtTurnoNomPaciente.setText(pac != null ? pac.getNombre() + " " + pac.getApellido() : "");
        txtTurnoCedMedico.setText(med != null ? med.getCedulaCiudadania() : "");
        txtTurnoNomMedico.setText(med != null ? med.getNombre() + " " + med.getApellido() : "");
        dpTurnoFecha.setValue(c.getFecha());
    }

    @FXML
    void onEditarTurno(ActionEvent e) {
        Cita cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno de la lista"); return; }
        if (dpTurnoFecha.getValue() != null) cita.setFecha(dpTurnoFecha.getValue());
        if (cbTurnoHoraInicio.getValue() != null) cita.setHoraInicio(cbTurnoHoraInicio.getValue());
        if (cbTurnoHoraFin.getValue() != null) cita.setHoraFin(cbTurnoHoraFin.getValue());
        repoCitas.actualizar(cita);
        cargarTablaTurnos();
        mostrarInfo("Turno actualizado");
    }

    @FXML
    void onCrearTurno(ActionEvent e) {
        mostrarInfo("Use la vista de Agendar Cita para crear nuevos turnos");
    }

    @FXML
    void onEliminarTurno(ActionEvent e) {
        Cita cita = tblTurnos.getSelectionModel().getSelectedItem();
        if (cita == null) { mostrarError("Seleccione un turno de la lista"); return; }
        repoCitas.inactivar(cita.getIdCita());
        cargarTablaTurnos();
        mostrarInfo("Turno cancelado");
    }

    // ══════════════════════════════════════════════════════════
    // PERSONAS
    // ══════════════════════════════════════════════════════════

    private void configurarTablaPersonas() {
        colPerCedula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCedulaCiudadania()));
        colPerNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPerApellido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getApellido()));
        colPerCelular.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCelular() != null ? c.getValue().getCelular() : ""));
        colPerFechaNac.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaNacimiento() != null ? c.getValue().getFechaNacimiento().toString() : ""));
        colPerCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo() != null ? c.getValue().getCorreo() : ""));
        colPerGenero.setCellValueFactory(c -> {
            Integer g = c.getValue().getIdGenero();
            String nombre = g == null ? "" : switch (g) { case 1 -> "Masculino"; case 2 -> "Femenino"; case 3 -> "No Binario"; default -> "Otro"; };
            return new SimpleStringProperty(nombre);
        });

        tblPersonas.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) llenarFormPersona(p);
        });
    }

    private void cargarTablaPersonas() {
        tblPersonas.setItems(FXCollections.observableArrayList(repoPersona.listar()));
    }

    private void llenarFormPersona(Persona p) {
        txtPerCedula.setText(p.getCedulaCiudadania());
        txtPerNombre.setText(p.getNombre());
        txtPerApellido.setText(p.getApellido());
        txtPerCelular.setText(p.getCelular() != null ? p.getCelular() : "");
        txtPerCorreo.setText(p.getCorreo() != null ? p.getCorreo() : "");
        dpPerFechaNac.setValue(p.getFechaNacimiento());
        if (p.getIdGenero() != null) {
            cbPerGenero.setValue(switch (p.getIdGenero()) {
                case 1 -> "Masculino"; case 2 -> "Femenino"; case 3 -> "No Binario"; default -> "Prefiero no decir";
            });
        }
    }

    @FXML
    void onEditarPersona(ActionEvent e) {
        Persona p = tblPersonas.getSelectionModel().getSelectedItem();
        if (p == null) { mostrarError("Seleccione una persona de la lista"); return; }
        p.setNombre(txtPerNombre.getText());
        p.setApellido(txtPerApellido.getText());
        p.setCelular(txtPerCelular.getText());
        p.setCorreo(txtPerCorreo.getText());
        if (dpPerFechaNac.getValue() != null) p.setFechaNacimiento(dpPerFechaNac.getValue());
        repoPersona.actualizar(p);
        cargarTablaPersonas();
        mostrarInfo("Persona actualizada");
    }

    @FXML
    void onCrearPersona(ActionEvent e) {
        if (txtPerCedula.getText().isEmpty() || txtPerNombre.getText().isEmpty() || txtPerApellido.getText().isEmpty()) {
            mostrarError("Cédula, nombre y apellido son obligatorios"); return;
        }
        Persona nueva = new Persona() {};
        nueva.setCedulaCiudadania(txtPerCedula.getText());
        nueva.setNombre(txtPerNombre.getText());
        nueva.setApellido(txtPerApellido.getText());
        nueva.setCelular(txtPerCelular.getText());
        nueva.setCorreo(txtPerCorreo.getText());
        nueva.setFechaNacimiento(dpPerFechaNac.getValue());
        nueva.setIdEstado(2);
        String gen = cbPerGenero.getValue();
        if (gen != null) nueva.setIdGenero(switch (gen) { case "Masculino" -> 1; case "Femenino" -> 2; case "No Binario" -> 3; default -> 4; });
        repoPersona.guardar(nueva);
        cargarTablaPersonas();
        limpiarFormPersona();
        mostrarInfo("Persona creada");
    }

    @FXML
    void onEliminarPersona(ActionEvent e) {
        Persona p = tblPersonas.getSelectionModel().getSelectedItem();
        if (p == null) { mostrarError("Seleccione una persona de la lista"); return; }
        repoPersona.inactivar(p.getIdPersona());
        cargarTablaPersonas();
        mostrarInfo("Persona inactivada");
    }

    private void limpiarFormPersona() {
        txtPerCedula.clear(); txtPerNombre.clear(); txtPerApellido.clear();
        txtPerCelular.clear(); txtPerCorreo.clear(); dpPerFechaNac.setValue(null); cbPerGenero.setValue(null);
    }

    // ══════════════════════════════════════════════════════════
    // USUARIOS
    // ══════════════════════════════════════════════════════════

    private void configurarTablaUsuarios() {
        colUsuNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario()));
        colUsuContrasena.setCellValueFactory(c -> new SimpleStringProperty("••••••••"));

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> {
            if (u != null) { txtUsuNombre.setText(u.getUsuario()); txtUsuContrasena.clear(); }
        });
    }

    private void cargarTablaUsuarios() {
        tblUsuarios.setItems(FXCollections.observableArrayList(repoUsuario.listar()));
    }

    @FXML
    void onEditarUsuario(ActionEvent e) {
        Usuario u = tblUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) { mostrarError("Seleccione un usuario de la lista"); return; }
        if (!txtUsuNombre.getText().isEmpty()) u.setUsuario(txtUsuNombre.getText());
        if (!txtUsuContrasena.getText().isEmpty()) u.setContrasena(txtUsuContrasena.getText());
        repoUsuario.actualizar(u);
        cargarTablaUsuarios();
        mostrarInfo("Usuario actualizado");
    }

    @FXML
    void onCrearUsuario(ActionEvent e) {
        if (txtUsuNombre.getText().isEmpty() || txtUsuContrasena.getText().isEmpty()) {
            mostrarError("Usuario y contraseña son obligatorios"); return;
        }
        Usuario nuevo = new Usuario();
        nuevo.setUsuario(txtUsuNombre.getText());
        nuevo.setContrasena(txtUsuContrasena.getText());
        servicioUsuarios.registrarUsuario(nuevo);
        cargarTablaUsuarios();
        txtUsuNombre.clear(); txtUsuContrasena.clear();
        mostrarInfo("Usuario creado");
    }

    @FXML
    void onEliminarUsuario(ActionEvent e) {
        Usuario u = tblUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) { mostrarError("Seleccione un usuario de la lista"); return; }
        repoUsuario.inactivar(u.getIdUsuario());
        cargarTablaUsuarios();
        mostrarInfo("Usuario eliminado");
    }

    // ══════════════════════════════════════════════════════════
    // UTILIDADES
    // ══════════════════════════════════════════════════════════

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
}
