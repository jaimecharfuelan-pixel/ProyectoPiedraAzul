package com.proyecto;

import javafx.application.Application; // <-- IMPORTANTE: Debe estar este import
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// DEBES agregar "extends Application" después del nombre de la clase
public class App extends Application { 

    @Override
    public void start(Stage stage) throws Exception {
        // Aquí va tu lógica para cargar el FXML (Login o Main)
        Parent root = FXMLLoader.load(getClass().getResource("/com/presentacion/vistas/VistaLogin.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Proyecto Piedra Azul");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Ahora esto ya no debería dar error
    }
}