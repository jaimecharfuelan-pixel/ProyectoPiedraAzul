package com.proyecto.presentacion.controladores;

import com.proyecto.logica.modelos.*;
import com.proyecto.logica.servicios.ServicioAgendamiento;
import com.proyecto.persistencia.repositorios.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private ComboBox<LocalTime> cbHora;
    @FXML
    private TextArea txtMotivo;
    @FXML
    private Button btnGuardar;

    // Repos y servicios
    private ServicioAgendamiento servicio;
    private RepositorioPaciente repoPaciente;
    private RepositorioPersona repoPersona;
    private RepositorioMedicoTerapista repoMedico;

    @FXML
    public void initialize() {

        servicio = new ServicioAgendamiento(
                new RepositorioCitas(),
                new RepositorioJornadaLaboral());

        repoPaciente = new RepositorioPaciente();
        repoPersona = new RepositorioPersona();
        repoMedico = new RepositorioMedicoTerapista();

        cargarMedicos();
        cargarEventos();
    }

    // =========================
    // MÉDICOS
    // =========================
    private void cargarMedicos() {

        List<MedicoTerapista> lista = repoMedico.listarActivos();
        cbMedico.getItems().addAll(lista);

        cbMedico.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MedicoTerapista item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre() + " " + item.getApellido());
            }
        });

        cbMedico.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MedicoTerapista item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre() + " " + item.getApellido());
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
        p.setIdEstado(2); // Activo

        int id = repoPaciente.guardar(p);
        p.setIdPersona(id);

        return p;
    }

    // =========================
    // LIMPIAR
    // =========================
    private void limpiar() {
        txtNombre.clear();
        txtApellido.clear();
        txtCedula.clear();
        txtCorreo.clear();
        txtCelular.clear();
        txtMotivo.clear();
        cbMedico.setValue(null);
        cbHora.getItems().clear();
        dpFecha.setValue(null);
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
}