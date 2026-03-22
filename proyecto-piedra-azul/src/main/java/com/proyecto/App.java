package com.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Carga la VistaLogin desde tu carpeta de recursos corregida
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
        Parent root = loader.load();
        
        stage.setScene(new Scene(root));
        stage.setTitle("Proyecto Piedra Azul - Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}