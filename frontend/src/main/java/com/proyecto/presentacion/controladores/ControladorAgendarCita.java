package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;
import com.proyecto.presentacion.facade.BackendFacade;

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
import java.util.Map;

public class ControladorAgendarCita {

    @FXML private TextField           txtNombre;
    @FXML private TextField           txtApellido;
    @FXML private TextField           txtCedula;
    @FXML private TextField           txtCorreo;
    @FXML private TextField           txtCelular;
    @FXML private ChoiceBox<String>   cbGenero;
    @FXML private ComboBox<MedicoDTO> cbMedico;
    @FXML private DatePicker          dpFecha;
    @FXML private DatePicker          dpFechaNac;
    @FXML private ComboBox<LocalTime> cbHora;
    @FXML private TextArea            txtMotivo;
    @FXML private Button              btnGuardar;
    @FXML private Label               lblErrorCedula;
    @FXML private Label               lblErrorNombre;
    @FXML private Label               lblErrorApellido;
    @FXML private Label               lblErrorCorreo;
    @FXML private Label               lblErrorCelular;
    @FXML private Label               lblErrorMotivo;

    private final BackendFacade backendFacade = new BackendFacade();

    // ─── Inicialización ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        cargarMedicos();
        cargarGenero();
        cbMedico.setOnAction(e -> { actualizarCalendario(); actualizarHorarios(); });
        dpFecha.setOnAction(e -> actualizarHorarios());
        iniciarValidaciones();
        iniciarListeners();
    }

    // ─── Listeners de validación en tiempo real ───────────────────────────────

    private void iniciarListeners() {
        // Cédula: solo números, máx 15
        txtCedula.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 15) { txtCedula.setText(old); return; }
            if (!nuevo.isEmpty() && !nuevo.matches("[0-9]*")) { txtCedula.setText(old); return; }
            aplicarEstilo(txtCedula, lblErrorCedula, validarNumerico(nuevo, "Cédula", 15));
        });

        // Celular: solo números, máx 15
        txtCelular.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 15) { txtCelular.setText(old); return; }
            if (!nuevo.isEmpty() && !nuevo.matches("[0-9]*")) { txtCelular.setText(old); return; }
            aplicarEstilo(txtCelular, lblErrorCelular, validarNumerico(nuevo, "Celular", 15));
        });

        // Nombre: texto, máx 20, sin caracteres especiales
        txtNombre.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 20) { txtNombre.setText(old); return; }
            aplicarEstilo(txtNombre, lblErrorNombre, validarTexto(nuevo, "Nombre"));
        });

        // Apellido: texto, máx 20, sin caracteres especiales
        txtApellido.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 20) { txtApellido.setText(old); return; }
            aplicarEstilo(txtApellido, lblErrorApellido, validarTexto(nuevo, "Apellido"));
        });

        // Correo: máx 50, sin espacios
        txtCorreo.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.contains(" ")) { txtCorreo.setText(old); return; }
            aplicarEstilo(txtCorreo, lblErrorCorreo, validarCorreo(nuevo));
        });

        // Motivo: máx 200
        txtMotivo.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.length() > 200) { txtMotivo.setText(old); return; }
            aplicarEstilo(txtMotivo, lblErrorMotivo,
                    nuevo.length() > 0 && nuevo.trim().isEmpty() ? "No puede ser solo espacios" : "");
        });
    }

    // ─── Reglas de validación ─────────────────────────────────────────────────

    /** Valida campos numéricos (cédula, celular). */
    private String validarNumerico(String valor, String campo, int max) {
        if (valor.isEmpty()) return "";
        if (valor.startsWith(" "))  return campo + " no puede iniciar con espacio";
        if (valor.contains("  "))   return "No se permiten espacios dobles";
        return "";
    }

    /** Valida campos de texto (nombre, apellido): sin especiales, sin espacios dobles. */
    private String validarTexto(String valor, String campo) {
        if (valor.isEmpty()) return "";
        if (valor.startsWith(" "))          return campo + " no puede iniciar con espacio";
        if (valor.contains("  "))           return "No se permiten espacios dobles";
        if (!valor.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]*"))
            return "Solo se permiten letras en " + campo;
        return "";
    }

    /** Valida correo: formato básico. */
    private String validarCorreo(String valor) {
        if (valor.isEmpty()) return "";
        if (valor.startsWith(" "))  return "Correo no puede iniciar con espacio";
        if (valor.contains("  "))   return "No se permiten espacios dobles";
        if (valor.length() > 50)    return "Correo demasiado largo";
        return "";
    }

    /** Aplica borde verde/rojo y muestra el mensaje de error en el label. */
    private void aplicarEstilo(Control campo, Label lblError, String error) {
        if (lblError == null) return;
        if (error == null || error.isEmpty()) {
            campo.setStyle("-fx-border-color: #43a047; -fx-border-width: 1.5; -fx-border-radius: 8;");
            lblError.setText("");
        } else {
            campo.setStyle("-fx-border-color: #e53935; -fx-border-width: 1.5; -fx-border-radius: 8;");
            lblError.setText(error);
        }
    }

    // ─── Carga de datos ───────────────────────────────────────────────────────

    private void cargarGenero() {
        cbGenero.getItems().addAll("Masculino", "Femenino", "Otro");
    }

    private void cargarMedicos() {
        try {
            List<MedicoDTO> lista = backendFacade.listarMedicosActivos();
            cbMedico.getItems().addAll(lista);
            cbMedico.setCellFactory(p -> new ListCell<>() {
                protected void updateItem(MedicoDTO m, boolean empty) {
                    super.updateItem(m, empty);
                    setText(empty || m == null ? "" : m.toString());
                }
            });
            cbMedico.setButtonCell(new ListCell<>() {
                protected void updateItem(MedicoDTO m, boolean empty) {
                    super.updateItem(m, empty);
                    setText(empty || m == null ? "" : m.toString());
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarHorarios() {
        cbHora.getItems().clear();
        if (cbMedico.getValue() == null || dpFecha.getValue() == null) return;
        try {
            String json = ClienteHttp.get("/api/citas/disponibilidad?idMedico="
                    + cbMedico.getValue().getIdMedico() + "&fecha=" + dpFecha.getValue());
            List<LocalTime> horarios = ClienteHttp.parsearLista(json, LocalTime.class);
            cbHora.getItems().addAll(horarios);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Deshabilita en el DatePicker los días sin jornada del médico seleccionado. */
    private void actualizarCalendario() {
        if (cbMedico.getValue() == null) return;
        try {
            String json = ClienteHttp.get(
                    "/api/jornadas/medico/" + cbMedico.getValue().getIdMedico() + "/dias");
            List<String> diasConJornada = ClienteHttp.parsearLista(json, String.class);
            dpFecha.setDayCellFactory(p -> new DateCell() {
                @Override
                public void updateItem(LocalDate d, boolean empty) {
                    super.updateItem(d, empty);
                    String nombreDia = traducirDia(d.getDayOfWeek().name());
                    boolean sinJornada = !diasConJornada.isEmpty()
                            && diasConJornada.stream().noneMatch(j -> j.equalsIgnoreCase(nombreDia));
                    setDisable(d.isBefore(LocalDate.now()) || sinJornada);
                    if (sinJornada && !d.isBefore(LocalDate.now()))
                        setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #aaa;");
                }
            });
            dpFecha.setValue(null);
            cbHora.getItems().clear();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String traducirDia(String dayOfWeekEn) {
        return switch (dayOfWeekEn) {
            case "MONDAY"    -> "Lunes";
            case "TUESDAY"   -> "Martes";
            case "WEDNESDAY" -> "Miércoles";
            case "THURSDAY"  -> "Jueves";
            case "FRIDAY"    -> "Viernes";
            case "SATURDAY"  -> "Sábado";
            case "SUNDAY"    -> "Domingo";
            default          -> dayOfWeekEn;
        };
    }

    // ─── Acciones ─────────────────────────────────────────────────────────────

    @FXML
    private void onBuscarPaciente() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) { mostrarError("Ingrese una cédula para buscar"); return; }
        try {
            PersonaDTO persona = backendFacade.buscarPacientePorDocumento(cedula);
            if (persona.getIdPersona() == 0) {
                mostrarError("No se encontró ninguna persona con esa cédula");
                return;
            }
            precargarPersona(persona);
            mostrarInfo("Paciente cargado correctamente");
        } catch (Exception e) {
            mostrarError("No se encontró ninguna persona con esa cédula");
        }
    }

    private void precargarPersona(PersonaDTO p) {
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        if (p.getCorreo()   != null) txtCorreo.setText(p.getCorreo());
        if (p.getCelular()  != null) txtCelular.setText(p.getCelular());
        if (p.getFechaNacimiento() != null) dpFechaNac.setValue(p.getFechaNacimiento());
        if (p.getIdGenero() != null) cbGenero.setValue(switch (p.getIdGenero()) {
            case 1 -> "Masculino"; case 2 -> "Femenino"; default -> "Otro";
        });
        txtNombre.setDisable(true);   txtApellido.setDisable(true);
        txtCorreo.setDisable(true);   txtCelular.setDisable(true);
        dpFechaNac.setDisable(true);  cbGenero.setDisable(true);
    }

    @FXML
    private void onGuardarCita() {
        if (!validarFormulario()) return;
        try {
            PersonaDTO persona = backendFacade.buscarPacientePorDocumento(txtCedula.getText().trim());
            int idPaciente;
            if (persona.getIdPersona() > 0) {
                idPaciente = persona.getIdPersona();
            } else {
                PersonaDTO nuevo = backendFacade.registrarPaciente(Map.of(
                        "paciente", Map.of(
                                "nombre",            txtNombre.getText().trim(),
                                "apellido",          txtApellido.getText().trim(),
                                "cedulaCiudadania",  txtCedula.getText().trim(),
                                "celular",           txtCelular.getText().trim(),
                                "correo",            txtCorreo.getText().trim(),
                                "idGenero",          generoAId(cbGenero.getValue()),
                                "idEstado",          2
                        ),
                        "usuario", Map.of(
                                "usuario",    txtCedula.getText().trim(),
                                "contrasena", txtCedula.getText().trim()
                        )
                ));
                idPaciente = nuevo.getIdPersona();
            }

            String respuesta = backendFacade.agendarCitaWeb(
                    idPaciente,
                    cbMedico.getValue().getIdMedico(),
                    dpFecha.getValue(),
                    cbHora.getValue()
            );

            if (respuesta.contains("agendada")) {
                mostrarInfo("Cita agendada correctamente");
                limpiar();
            } else {
                mostrarError("Ese horario ya no está disponible");
            }
        } catch (Exception e) { mostrarError("Error al agendar: " + e.getMessage()); }
    }

    /** Validación completa al guardar — muestra errores en los labels. */
    private boolean validarFormulario() {
        boolean ok = true;

        // Cédula
        String errCedula = txtCedula.getText().trim().isEmpty() ? "La cédula es obligatoria" : "";
        aplicarEstilo(txtCedula, lblErrorCedula, errCedula);
        if (!errCedula.isEmpty()) ok = false;

        // Nombre
        String errNombre = txtNombre.getText().trim().isEmpty() ? "El nombre es obligatorio"
                : validarTexto(txtNombre.getText(), "Nombre");
        aplicarEstilo(txtNombre, lblErrorNombre, errNombre);
        if (!errNombre.isEmpty()) ok = false;

        // Apellido
        String errApellido = txtApellido.getText().trim().isEmpty() ? "El apellido es obligatorio"
                : validarTexto(txtApellido.getText(), "Apellido");
        aplicarEstilo(txtApellido, lblErrorApellido, errApellido);
        if (!errApellido.isEmpty()) ok = false;

        // Médico
        if (cbMedico.getValue() == null) {
            mostrarError("Seleccione un médico");
            ok = false;
        }

        // Fecha
        if (dpFecha.getValue() == null) {
            mostrarError("Seleccione una fecha");
            ok = false;
        }

        // Hora
        if (cbHora.getValue() == null) {
            mostrarError("Seleccione una hora");
            ok = false;
        }

        return ok;
    }

    private int generoAId(String genero) {
        if (genero == null) return 4;
        return switch (genero) { case "Masculino" -> 1; case "Femenino" -> 2; default -> 3; };
    }

    private void limpiar() {
        txtNombre.clear(); txtApellido.clear(); txtCedula.clear();
        txtCorreo.clear(); txtCelular.clear(); txtMotivo.clear();
        cbMedico.setValue(null); cbHora.getItems().clear();
        dpFecha.setValue(null); cbGenero.setValue(null);
        txtNombre.setDisable(false);  txtApellido.setDisable(false);
        txtCorreo.setDisable(false);  txtCelular.setDisable(false);
        dpFechaNac.setDisable(false); cbGenero.setDisable(false);
        // Limpiar estilos de validación
        for (Control c : new Control[]{txtNombre, txtApellido, txtCedula, txtCorreo, txtCelular})
            c.setStyle("");
        for (Label l : new Label[]{lblErrorNombre, lblErrorApellido, lblErrorCedula,
                                    lblErrorCorreo, lblErrorCelular, lblErrorMotivo})
            if (l != null) l.setText("");
    }

    public void setModoPaciente(int idPaciente) {
        if (idPaciente <= 0) return;
        try {
            String json = ClienteHttp.get("/api/pacientes/" + idPaciente);
            PersonaDTO p = ClienteHttp.parsear(json, PersonaDTO.class);
            txtCedula.setText(p.getCedulaCiudadania());
            precargarPersona(p);
            txtCedula.setDisable(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        String rol = SesionUsuario.getInstancia().getRol();
        String vista = (rol != null && rol.equals("paciente"))
                ? "/com/presentacion/vistas/VistaPaciente.fxml"
                : "/com/presentacion/vistas/VistaAgendador.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void iniciarValidaciones() {
        dpFecha.setDayCellFactory(p -> new DateCell() {
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(d.isBefore(LocalDate.now()));
            }
        });
        dpFechaNac.setDayCellFactory(p -> new DateCell() {
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(d.isAfter(LocalDate.now()));
            }
        });
    }

    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
}
