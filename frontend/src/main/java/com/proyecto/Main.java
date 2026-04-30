package com.proyecto;

public class Main {

    public static void main(String[] args) {
        // Forzar UTF-8 en toda la JVM para evitar ?? en caracteres especiales (Windows)
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("stdout.encoding", "UTF-8");
        java.nio.charset.Charset.defaultCharset(); // trigger reload
        App.launch(App.class, args);
    }
}
