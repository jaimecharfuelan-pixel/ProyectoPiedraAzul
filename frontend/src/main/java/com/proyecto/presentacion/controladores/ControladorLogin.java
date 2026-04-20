package com.proyecto.presentacion.controladores;

import com.proyecto.presentacion.ClienteHttp;
import com.proyecto.presentacion.SesionUsuario;
import com.proyecto.presentacion.dto.LoginResponseDTO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Map;

public class ControladorLogin {

    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label         lblErrorUsuario;
    @FXML private Button        btnLogin;

    // ─── Validación en vivo ───────────────────────────────────────────────────

    @FXML
    public void initialize() {
        lblErrorUsuario.setText("");
        btnLogin.setDisable(true);

        txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 15) { txtUsuario.setText(oldVal); return; }
            if (newVal.isEmpty()) {
                txtUsuario.setStyle(""); lblErrorUsuario.setText(""); btnLogin.setDisable(true); return;
            }
            String error = validarUsuario(newVal);
            if (!error.isEmpty()) {
                txtUsuario.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                lblErrorUsuario.setText(error);
                btnLogin.setDisable(true);
            } else {
                txtUsuario.setStyle("-fx-border-color: green; -fx-border-width: 2;");
                lblErrorUsuario.setText("");
                btnLogin.setDisable(false);
            }
        });
    }

    private String validarUsuario(String user) {
        if (user.length() > 25)    return "Máximo 25 caracteres";
        if (user.startsWith(" "))  return "No puede iniciar con espacio";
        if (user.endsWith(" "))    return "No puede terminar con espacio";
        if (user.contains("  "))   return "No se permiten espacios dobles";
        return "";
    }

    // ─── Login via API Gateway ────────────────────────────────────────────────

    @FXML
    public void onBtnIniciarSesionClicked() {
        String user = txtUsuario.getText();
        String pass = txtContrasena.getText();

        try {
            // POST http://localhost:8080/api/auth/login
            String respuesta = ClienteHttp.post("/api/auth/login",
                    Map.of("usuario", user, "contrasena", pass));

            LoginResponseDTO dto = ClienteHttp.parsear(respuesta, LoginResponseDTO.class);

            if (dto.getToken() == null) {
                mostrarError("Acceso Denegado", "Usuario o contraseña incorrectos.");
                return;
            }

            // Guardar en sesión
            SesionUsuario.getInstancia().setToken(dto.getToken());
            SesionUsuario.getInstancia().setIdUsuario(dto.getIdUsuario());
            SesionUsuario.getInstancia().setRol(dto.getRol().toLowerCase());

            navegarSegunRol(dto.getRol().toLowerCase());

        } catch (Exception e) {
            mostrarError("Error de conexión", "No se pudo conectar al servidor.\n" + e.getMessage());
        }
    }

    private void navegarSegunRol(String rol) {
        String vista = switch (rol) {
            case "administrador" -> "/com/presentacion/vistas/VistaAdmin.fxml";
            case "agendador"     -> "/com/presentacion/vistas/VistaAgendador.fxml";
            case "paciente"      -> "/com/presentacion/vistas/VistaPaciente.fxml";
            default              -> "/com/presentacion/vistas/VistaAgendador.fxml";
        };
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
