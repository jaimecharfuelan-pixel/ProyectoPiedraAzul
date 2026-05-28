package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.facade.BackendFacade;
import com.proyecto.presentacion.util.Conversiones;
import com.proyecto.presentacion.dto.ErrorValidacionDTO;
import com.proyecto.presentacion.dto.MedicoDTO;
import com.proyecto.presentacion.dto.PersonaDTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<LocalDate, Boolean> cacheDisponibilidad = new ConcurrentHashMap<>();
    private final Set<LocalDate> fechasEnCarga = ConcurrentHashMap.newKeySet();

    @FXML
    public void initialize() {
        cargarMedicos();
        cargarGenero();
        cbMedico.setOnAction(e -> { actualizarCalendario(); });
        dpFecha.setOnAction(e -> verificarDisponibilidadFecha(dpFecha.getValue()));
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

    private void actualizarCalendario() {
        if (cbMedico.getValue() == null) return;
        int idMedico = cbMedico.getValue().getIdMedico();
        cacheDisponibilidad.clear();
        fechasEnCarga.clear();
        dpFecha.setDisable(true);
        dpFecha.setValue(null);
        cbHora.getItems().clear();
        dpFecha.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setDisable(true);
                    return;
                }
                setDisable(true);
                setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #cccccc;");
            }
        });

        new Thread(() -> {
            try {
                List<String> diasConJornada = backendFacade.listarDiasConJornada(idMedico);
                if (diasConJornada.isEmpty()) {
                    javafx.application.Platform.runLater(() -> {
                        mostrarAdvertencia("El médico seleccionado no tiene jornadas configuradas.\n"
                                + "Contacte al administrador para configurar los turnos.");
                        dpFecha.setDayCellFactory(null);
                        dpFecha.setDisable(true);
                    });
                    return;
                }

                List<LocalDate> fechasCandidatas = obtenerFechasConJornada(diasConJornada, 90);
                Map<LocalDate, Boolean> disponibilidad = new java.util.HashMap<>();
                for (LocalDate fecha : fechasCandidatas) {
                    boolean libre = !backendFacade.consultarDisponibilidad(idMedico, fecha).isEmpty();
                    disponibilidad.put(fecha, libre);
                }

                javafx.application.Platform.runLater(() -> {
                    cacheDisponibilidad.putAll(disponibilidad);
                    establecerDayCellFactory(idMedico, diasConJornada);
                    dpFecha.setDisable(false);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        mostrarError("No se pudieron cargar las jornadas del médico: " + e.getMessage()));
            }
        }).start();
    }

    private void establecerDayCellFactory(int idMedico, List<String> diasConJornada) {
        dpFecha.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setDisable(true);
                    return;
                }

                if (d.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #cccccc;");
                    return;
                }

                String nombreDia = Conversiones.traducirDia(d.getDayOfWeek().name());
                if (!tieneJornada(nombreDia, diasConJornada)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #aaaaaa;");
                    return;
                }

                Boolean disponible = cacheDisponibilidad.get(d);
                if (disponible == null) {
                    setDisable(true);
                    setStyle("-fx-background-color: #fff9c4; -fx-text-fill: #555;");
                } else if (!disponible) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #aaaaaa;");
                } else {
                    setDisable(false);
                    setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #1b5e20; -fx-font-weight: bold;");
                }
            }
        });
    }

    private boolean tieneJornada(String nombreDia, List<String> diasConJornada) {
        return diasConJornada.stream()
                .anyMatch(j -> normalizarDia(j).equalsIgnoreCase(normalizarDia(nombreDia)));
    }

    private String normalizarDia(String dia) {
        if (dia == null) return "";
        return dia.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .trim();
    }

    private List<LocalDate> obtenerFechasConJornada(List<String> diasConJornada, int diasAdelante) {
        java.util.Set<DayOfWeek> diasSemana = new HashSet<>();
        for (String dia : diasConJornada) {
            DayOfWeek dow = mapearDiaSemana(dia);
            if (dow != null) diasSemana.add(dow);
        }

        List<LocalDate> fechas = new ArrayList<>();
        LocalDate actual = LocalDate.now();
        LocalDate limite = actual.plusDays(diasAdelante);
        while (!actual.isAfter(limite)) {
            if (diasSemana.contains(actual.getDayOfWeek())) {
                fechas.add(actual);
            }
            actual = actual.plusDays(1);
        }
        return fechas;
    }

    private DayOfWeek mapearDiaSemana(String diaSemana) {
        if (diaSemana == null) return null;
        return switch (normalizarDia(diaSemana)) {
            case "lunes"     -> DayOfWeek.MONDAY;
            case "martes"    -> DayOfWeek.TUESDAY;
            case "miercoles" -> DayOfWeek.WEDNESDAY;
            case "jueves"    -> DayOfWeek.THURSDAY;
            case "viernes"   -> DayOfWeek.FRIDAY;
            case "sabado"    -> DayOfWeek.SATURDAY;
            case "domingo"   -> DayOfWeek.SUNDAY;
            default           -> null;
        };
    }

    private void verificarDisponibilidadFecha(LocalDate fecha) {
        if (cbMedico.getValue() == null || fecha == null) return;
        int idMedico = cbMedico.getValue().getIdMedico();

        cbHora.getItems().clear();
        cbHora.setPromptText("Cargando horarios...");

        new Thread(() -> {
            try {
                List<LocalTime> slots = backendFacade.consultarDisponibilidad(idMedico, fecha);

                javafx.application.Platform.runLater(() -> {
                    cbHora.getItems().clear();
                    if (slots.isEmpty()) {
                        cbHora.setPromptText("Sin horarios disponibles");
                        mostrarAdvertencia("El día " + fecha.getDayOfWeek().name()
                                + " (" + fecha + ") no tiene horarios disponibles.\n"
                                + "Todos los turnos están ocupados o no hay jornada configurada.");
                        dpFecha.setValue(null);
                    } else {
                        cbHora.getItems().addAll(slots);
                        cbHora.setPromptText("Seleccione una hora");
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    cbHora.setPromptText("Error al cargar horarios");
                    mostrarError("Error al consultar disponibilidad: " + e.getMessage());
                });
            }
        }).start();
    }

    // ─── Acciones ─────────────────────────────────────────────────────────────

    @FXML
    private void onBuscarPaciente() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) { mostrarError("Ingrese una cédula para buscar"); return; }
        try {
            PersonaDTO persona = backendFacade.buscarPacientePorDocumento(cedula);
            if (persona == null || persona.getIdPersona() == 0) {
                mostrarError("No se encontró ninguna persona con esa cédula"); return;
            }
            precargarPersona(persona);
            mostrarInfo("Paciente cargado correctamente");
        } catch (Exception e) { mostrarError("No se encontró ninguna persona con esa cédula"); }
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
            if (persona != null && persona.getIdPersona() > 0) {
                idPaciente = persona.getIdPersona();
            } else {
                // Construir el body con HashMap para evitar problemas de serialización
                // con Map.of() anidado que Jackson no deserializa correctamente
                java.util.Map<String, Object> pacienteBody = new java.util.HashMap<>();
                pacienteBody.put("nombre",           txtNombre.getText().trim());
                pacienteBody.put("apellido",         txtApellido.getText().trim());
                pacienteBody.put("cedulaCiudadania", txtCedula.getText().trim());
                pacienteBody.put("celular",          txtCelular.getText().trim());
                pacienteBody.put("correo",           txtCorreo.getText().trim());
                pacienteBody.put("idGenero",         generoAId(cbGenero.getValue()));
                pacienteBody.put("idEstado",         2);

                java.util.Map<String, Object> usuarioBody = new java.util.HashMap<>();
                usuarioBody.put("usuario",    txtCedula.getText().trim());
                usuarioBody.put("contrasena", txtCedula.getText().trim());

                java.util.Map<String, Object> body = new java.util.HashMap<>();
                body.put("paciente", pacienteBody);
                body.put("usuario",  usuarioBody);

                PersonaDTO nuevo = backendFacade.registrarPaciente(body);
                idPaciente = nuevo.getIdPersona();
            }

            ErrorValidacionDTO resultado = backendFacade.agendarCitaWebConValidacion(
                    idPaciente,
                    cbMedico.getValue().getIdMedico(),
                    dpFecha.getValue(),
                    cbHora.getValue()
            );

            if (resultado.isExitosa()) {
                mostrarInfo("✅ Cita agendada correctamente");
                limpiar();
            } else {
                mostrarError(construirMensajeValidacion(resultado));
            }
        } catch (Exception e) { mostrarError("Error al agendar: " + e.getMessage()); }
    }

    private String construirMensajeValidacion(ErrorValidacionDTO dto) {
        if (dto == null) return "❌ Error de validación desconocido.";
        if (dto.getErrores() == null || dto.getErrores().isEmpty()) {
            return "⚠️ " + (dto.getMensajePrincipal() != null ? dto.getMensajePrincipal() : "No se pudo agendar la cita.");
        }
        return "⚠️ " + (dto.getMensajePrincipal() != null ? dto.getMensajePrincipal() : "No se pudo agendar la cita.")
                + "\n\n• " + String.join("\n• ", dto.getErrores());
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
        return Conversiones.generoAId(genero);
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
            PersonaDTO p = backendFacade.buscarPacientePorId(idPaciente);
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

    private void mostrarError(String msg)       { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarInfo(String msg)        { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
    private void mostrarAdvertencia(String msg) { new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
}
