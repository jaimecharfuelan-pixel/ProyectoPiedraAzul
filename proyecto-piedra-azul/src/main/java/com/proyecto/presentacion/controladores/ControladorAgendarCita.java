package com.proyecto.presentacion.controladores;

import com.proyecto.logica.modelos.*;
import com.proyecto.logica.servicios.ServicioAgendamiento;
import com.proyecto.persistencia.repositorios.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ControladorAgendarCita {

    // UI
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCedula;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtCelular;
    @FXML
    private ChoiceBox<String> cbGenero;
    @FXML
    private ComboBox<MedicoTerapista> cbMedico;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private DatePicker dpFechaNac;
    @FXML
    private ComboBox<LocalTime> cbHora;
    @FXML
    private TextArea txtMotivo;
    @FXML
    private Button btnGuardar;
    @FXML
    private Label lblErrorCedula;
    @FXML
    private Label lblErrorNombre;
    @FXML
    private Label lblErrorApellido;
    @FXML
    private Label lblErrorCorreo;
    @FXML
    private Label lblErrorCelular;
    @FXML
    private Label lblErrorMotivo;

    // Repos y servicios
    private ServicioAgendamiento servicio;
    private RepositorioPaciente repoPaciente;
    private RepositorioPersona repoPersona;
    private RepositorioMedicoTerapista repoMedico;

    @FXML
    public void initialize() {

        servicio = new ServicioAgendamiento(
                new RepositorioCitas(),
                new RepositorioJornadaLaboral(),repoMedico=new RepositorioMedicoTerapista());

        repoPaciente = new RepositorioPaciente();
        repoPersona = new RepositorioPersona();
        repoMedico = new RepositorioMedicoTerapista();

        cargarMedicos();
        cargarGenero();
        cargarEventos();
        iniciarValidaciones();
    }

    private void iniciarValidaciones() {

        validarCedula();
        validarNombre(txtNombre, lblErrorNombre);
        validarNombre(txtApellido, lblErrorApellido);
        validarCelular();
        validarCorreo();
        validarMotivo();
        validarFechas();
    }

    private void cargarGenero() {
        cbGenero.getItems().addAll("Masculino", "Femenino", "Otro");
    }

    // =========================
    // MÉDICOS
    // =========================
    private void cargarMedicos() {

        List<MedicoTerapista> lista = repoMedico.listarActivos();

        System.out.println("Médicos encontrados: " + lista.size());

        if (lista.isEmpty()) {
            mostrarError("No hay médicos activos en la base de datos");
        }

        cbMedico.getItems().clear();
        cbMedico.getItems().addAll(lista);

        cbMedico.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MedicoTerapista item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre() + " " + item.getApellido());
            }
        });

        cbMedico.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MedicoTerapista item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre() + " " + item.getApellido());
            }
        });
    }

    // =========================
    // EVENTOS
    // =========================
    private void cargarEventos() {
        cbMedico.setOnAction(e -> actualizarHorarios());
        dpFecha.setOnAction(e -> actualizarHorarios());
    }

    // =========================
    // HORARIOS
    // =========================
    private void actualizarHorarios() {

        cbHora.getItems().clear();

        if (cbMedico.getValue() == null || dpFecha.getValue() == null)
            return;

        List<LocalTime> disponibles = servicio.consultarDisponibilidad(
                cbMedico.getValue().getIdPersona(),
                dpFecha.getValue());

        System.out.println("Horarios encontrados: " + disponibles.size());

        if (disponibles.isEmpty()) {
            mostrarError("El médico no tiene disponibilidad ese día");
        }

        cbHora.getItems().addAll(disponibles);
    }

    // =========================
    // VALIDACIÓN
    // =========================
    private boolean validar() {

        if (txtNombre.getText().isEmpty() ||
                txtApellido.getText().isEmpty() ||
                txtCedula.getText().isEmpty() ||
                cbMedico.getValue() == null ||
                dpFecha.getValue() == null ||
                cbHora.getValue() == null ||
                txtMotivo.getText().isEmpty()) {

            mostrarError("Completa todos los campos obligatorios");
            return false;
        }

        return true;
    }

    // =========================
    // GUARDAR
    // =========================
    @FXML
    private void onGuardarCita() {

        if (!validar())
            return;

        String cedula = txtCedula.getText();

        // 🔍 1. Buscar persona
        Persona persona = repoPersona.buscarPorDocumento(cedula);

        Paciente paciente;

        if (persona != null) {

            // 🔍 2. Ver si ya es paciente
            paciente = repoPaciente.buscarPorId(persona.getIdPersona());

            if (paciente == null) {
                // Existe como persona pero NO como paciente
                paciente = crearPacienteDesdeFormulario();
            }

        } else {
            // 🔥 3. No existe → crear paciente completo
            paciente = crearPacienteDesdeFormulario();
        }

        // 📅 4. Agendar cita
        boolean ok = servicio.agendarCitaWeb(
                paciente.getIdPersona(),
                cbMedico.getValue().getIdPersona(),
                dpFecha.getValue(),
                cbHora.getValue());

        if (ok) {
            mostrarInfo("Cita agendada correctamente");
            limpiar();
        } else {
            mostrarError("Ese horario ya no está disponible");
        }
    }

    @FXML
    private void onBuscarPaciente() {

        String cedula = txtCedula.getText();

        if (cedula == null || cedula.isEmpty()) {
            mostrarError("Ingrese una cédula para buscar");
            return;
        }

        Persona persona = repoPersona.buscarPorDocumento(cedula);

        if (persona == null) {
            mostrarError("No se encontró ninguna persona con esa cédula");
            return;
        }

        // Llenar campos automáticamente
        txtNombre.setText(persona.getNombre());
        txtApellido.setText(persona.getApellido());
        txtCorreo.setText(persona.getCorreo());
        txtCelular.setText(persona.getCelular());

        // Fecha nacimiento
        if (persona.getFechaNacimiento() != null) {
            dpFechaNac.setValue(persona.getFechaNacimiento());
        }

        // Género (si manejas IDs)
        if (persona.getIdGenero() != null) {
            switch (persona.getIdGenero()) {
                case 1 -> cbGenero.setValue("Masculino");
                case 2 -> cbGenero.setValue("Femenino");
                default -> cbGenero.setValue("Otro");
            }
        }

        mostrarInfo("Paciente cargado correctamente");
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtCorreo.setDisable(true);
        txtCelular.setDisable(true);
        dpFechaNac.setDisable(true);
        cbGenero.setDisable(true);
    }

    // =========================
    // CREAR PACIENTE
    // =========================
    private Paciente crearPacienteDesdeFormulario() {

        Paciente p = new Paciente();

        p.setNombre(txtNombre.getText());
        p.setApellido(txtApellido.getText());
        p.setCedulaCiudadania(txtCedula.getText());
        p.setCorreo(txtCorreo.getText());
        p.setCelular(txtCelular.getText());
        p.setFechaNacimiento(dpFechaNac.getValue());
        p.setIdEstado(1); // Activo

        int id = repoPaciente.guardar(p);
        p.setIdPersona(id);

        return p;
    }

    // =========================
    // LIMPIAR
    // =========================
    private void limpiar() {
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtCorreo.setDisable(true);
        txtCelular.setDisable(true);
        dpFechaNac.setDisable(true);
        cbGenero.setDisable(true);
        txtNombre.clear();
        txtApellido.clear();
        txtCedula.clear();
        txtCorreo.clear();
        txtCelular.clear();
        txtMotivo.clear();
        cbMedico.setValue(null);
        cbHora.getItems().clear();
        dpFecha.setValue(null);
        cbGenero.setValue(null);
    }

    // =========================
    // ALERTAS
    // =========================
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

    private void mostrarErrorCampo(Control campo, Label label, String msg) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        label.setText(msg);
    }

    private void mostrarCorrecto(Control campo, Label label) {
        campo.setStyle("-fx-border-color: green; -fx-border-width: 2;");
        label.setText("");
    }

    private void limpiarError(Control campo, Label label) {
        campo.setStyle("");
        label.setText("");
    }

    private void validarCedula() {
        txtCedula.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal.length() > 12) {
                txtCedula.setText(oldVal);
                return;
            }

            if (newVal.isEmpty()) {
                limpiarError(txtCedula, lblErrorCedula);
                return;
            }

            if (!newVal.matches("\\d+")) {
                mostrarErrorCampo(txtCedula, lblErrorCedula, "Solo números");
            } else if (newVal.startsWith(" ")) {
                mostrarErrorCampo(txtCedula, lblErrorCedula, "No iniciar con espacio");
            } else {
                mostrarCorrecto(txtCedula, lblErrorCedula);
            }
        });
    }

    private void validarNombre(TextField campo, Label errorLabel) {

        campo.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal.length() > 20) {
                campo.setText(oldVal);
                return;
            }

            if (newVal.isEmpty()) {
                limpiarError(campo, errorLabel);
                return;
            }

            if (newVal.matches(".*\\d.*")) {
                mostrarErrorCampo(campo, errorLabel, "No números");
            } else if (newVal.startsWith(" ")) {
                mostrarErrorCampo(campo, errorLabel, "No iniciar con espacio");
            } else {
                mostrarCorrecto(campo, errorLabel);
            }
        });
    }

    private void validarCelular() {

        txtCelular.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal.length() > 15) {
                txtCelular.setText(oldVal);
                return;
            }

            if (newVal.isEmpty()) {
                limpiarError(txtCelular, lblErrorCelular);
                return;
            }

            if (!newVal.matches("\\d+")) {
                mostrarErrorCampo(txtCelular, lblErrorCelular, "Solo números");
            } else {
                mostrarCorrecto(txtCelular, lblErrorCelular);
            }
        });
    }

    private void validarCorreo() {

        txtCorreo.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal.length() > 35) {
                txtCorreo.setText(oldVal);
                return;
            }

            if (newVal.contains(" ")) {
                mostrarErrorCampo(txtCorreo, lblErrorCorreo, "Sin espacios");
            } else {
                mostrarCorrecto(txtCorreo, lblErrorCorreo);
            }
        });
    }

    private void validarMotivo() {

        txtMotivo.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal.length() > 70) {
                txtMotivo.setText(oldVal);
                return;
            }

            if (newVal.contains("   ")) {
                mostrarErrorCampo(txtMotivo, lblErrorMotivo, "Máx 2 espacios seguidos");
            } else {
                mostrarCorrecto(txtMotivo, lblErrorMotivo);
            }
        });
    }

    private void validarFechas() {

        dpFecha.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });

        dpFechaNac.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now()));
            }
        });
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        String rol = com.proyecto.presentacion.SesionUsuario.getInstancia().getRol();
        String vista = (rol != null && rol.equals("paciente"))
                ? "/com/presentacion/vistas/VistaPaciente.fxml"
                : "/com/presentacion/vistas/VistaAgendador.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Llamado desde ControladorPaciente cuando el paciente agenda su propia cita.
     * Precarga sus datos y oculta la lupa de búsqueda.
     */
    public void setModoPaciente(int prmIdPaciente) {
        if (prmIdPaciente <= 0) return;

        Persona persona = repoPersona.buscarPorId(prmIdPaciente);
        if (persona == null) return;

        // Precargar campos con los datos del paciente logueado
        txtNombre.setText(persona.getNombre());
        txtApellido.setText(persona.getApellido());
        txtCedula.setText(persona.getCedulaCiudadania());
        if (persona.getCorreo() != null) txtCorreo.setText(persona.getCorreo());
        if (persona.getCelular() != null) txtCelular.setText(persona.getCelular());
        if (persona.getFechaNacimiento() != null) dpFechaNac.setValue(persona.getFechaNacimiento());
        if (persona.getIdGenero() != null) {
            cbGenero.setValue(switch (persona.getIdGenero()) {
                case 1 -> "Masculino"; case 2 -> "Femenino"; default -> "Otro";
            });
        }

        // Bloquear campos de identidad y ocultar lupa
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtCedula.setDisable(true);
        txtCorreo.setDisable(true);
        txtCelular.setDisable(true);
        dpFechaNac.setDisable(true);
        cbGenero.setDisable(true);

        // Al cancelar, volver a vista paciente (no agendador)
        btnGuardar.setOnAction(e -> {
            if (!validar()) return;
            boolean ok = servicio.agendarCitaWeb(
                    prmIdPaciente,
                    cbMedico.getValue().getIdPersona(),
                    dpFecha.getValue(),
                    cbHora.getValue());
            if (ok) {
                mostrarInfo("Cita agendada correctamente");
                volverAPaciente();
            } else {
                mostrarError("Ese horario ya no está disponible");
            }
        });
    }

    private void volverAPaciente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaPaciente.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
