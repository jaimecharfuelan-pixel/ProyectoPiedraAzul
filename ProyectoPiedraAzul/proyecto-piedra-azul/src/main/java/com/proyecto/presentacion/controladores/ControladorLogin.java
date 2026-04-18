package com.proyecto.presentacion.controladores;

import com.proyecto.logica.modelos.Rol;
import com.proyecto.logica.servicios.ServicioUsuarios;
import com.proyecto.logica.servicios.ServicioAuth;
import com.proyecto.persistencia.repositorios.RepositorioRol;
import com.proyecto.persistencia.repositorios.RepositorioUsuario;
import com.proyecto.presentacion.SesionUsuario;
import io.jsonwebtoken.Claims;
import com.proyecto.seguridad.JwtUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.List;

public class ControladorLogin {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Label lblErrorUsuario;
    @FXML
    private Button btnLogin;

    private ServicioAuth servicioAuth;

    public ControladorLogin() {
        ServicioUsuarios servicioUsuarios = new ServicioUsuarios(new RepositorioUsuario());
        this.servicioAuth = new ServicioAuth(servicioUsuarios);
    }

    // MENSAJES ESPECÍFICOS
    private String obtenerErrorUsuario(String user) {

        if (user.length() > 25) {
            return "Máximo 25 caracteres";
        }

        if (user.startsWith(" ")) {
            return "No puede iniciar con espacio";
        }

        if (user.endsWith(" ")) {
            return "No puede terminar con espacio";
        }

        if (user.contains("  ")) {
            return "No se permiten espacios dobles";
        }

        return "";
    }

    // VALIDACIÓN EN VIVO
    @FXML
    public void initialize() {

        lblErrorUsuario.setText("");
        btnLogin.setDisable(true); // bloqueado al inicio

        txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> {

            // Limitar longitud
            if (newVal.length() > 15) {
                txtUsuario.setText(oldVal);
                return;
            }

            // Campo vacío
            if (newVal.isEmpty()) {
                txtUsuario.setStyle("");
                lblErrorUsuario.setText("");
                btnLogin.setDisable(true);
                return;
            }

            String error = obtenerErrorUsuario(newVal);

            if (!error.isEmpty()) {
                txtUsuario.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                lblErrorUsuario.setText(error);
                btnLogin.setDisable(true); // bloquea
            } else {
                txtUsuario.setStyle("-fx-border-color: green; -fx-border-width: 2;");
                lblErrorUsuario.setText("");
                btnLogin.setDisable(false); // habilita
            }
        });
    }

    // LOGIN
    @FXML
    public void onBtnIniciarSesionClicked() {

        String user = txtUsuario.getText();
        String pass = txtContrasena.getText();

        String token = servicioAuth.login(user, pass);

        if (token != null) {

            System.out.println("Token generado: " + token);

            // Extraer id del usuario desde el token JWT
            Claims claims = JwtUtil.validarToken(token);
            int idUsuario = claims.get("id", Integer.class);

            // Consultar el rol del usuario
            RepositorioRol repoRol = new RepositorioRol();
            List<Rol> roles = repoRol.listarPorUsuario(idUsuario);
            String nombreRol = roles.isEmpty() ? "" : roles.get(0).getNombre().toLowerCase();

            // Guardar en sesión
            SesionUsuario.getInstancia().setToken(token);
            SesionUsuario.getInstancia().setIdUsuario(idUsuario);
            SesionUsuario.getInstancia().setRol(nombreRol);

            navegarSegunRol(nombreRol);

        } else {
            mostrarError("Acceso Denegado", "Usuario o contraseña incorrectos.");
        }
    }

    private void navegarSegunRol(String rol) {
        String vista;
        switch (rol) {
            case "administrador" -> vista = "/com/presentacion/vistas/VistaAdmin.fxml";
            case "agendador"     -> vista = "/com/presentacion/vistas/VistaAgendador.fxml";
            case "paciente"      -> vista = "/com/presentacion/vistas/VistaPaciente.fxml";
            default              -> vista = "/com/presentacion/vistas/VistaAgendador.fxml";
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}