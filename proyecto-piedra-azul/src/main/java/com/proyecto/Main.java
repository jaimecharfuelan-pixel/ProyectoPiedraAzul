package com.proyecto;

import javafx.application.Application;

/*
 * ¿Por qué no ejecutamos esta clase Main directamente?
 * 
 * Esta clase hereda de `Application` (JavaFX). Si intentamos ejecutarla directamente 
 * a partir de Java 11+, el motor de Java bloqueará la ejecución pidiendo permisos 
 * estrictos de módulos ("faltan los componentes de JavaFX runtime"), los cuales se
 * configuran en un archivo llamado `module-info.java`.
 *
 * Para evitar configurar ese complejo archivo de módulos y poder usar cualquier 
 * librería libremente, usamos la clase `App.java`. Como `App.java` es una clase Java 
 * normal (no hereda de nada), arranca el programa sin restricciones de módulos y, 
 * desde adentro, llama a este `Main.java` (Main.main()) para levantar la interfaz.
 */
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public static void main(String[] args) {
        launch(args);
    }
}