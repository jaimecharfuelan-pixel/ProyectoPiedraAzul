package com.proyecto.presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Cliente HTTP simple que llama al API Gateway.
 * Reemplaza los imports directos a servicios del monolito.
 * Todos los controladores JavaFX usan esta clase para hablar con el backend.
 */
public class ClienteHttp {

    // URL base del API Gateway — todo pasa por aquí
    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpClient    cliente  = HttpClient.newHttpClient();
    private static final ObjectMapper  mapper   = crearMapper();

    private static ObjectMapper crearMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return m;
    }

    // ─── GET ─────────────────────────────────────────────────────────────────

    public static String get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    public static String getConToken(String path, String token) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    // ─── POST ────────────────────────────────────────────────────────────────

    public static String post(String path, Object body) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    public static String postConToken(String path, Object body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    // ─── PUT ─────────────────────────────────────────────────────────────────

    public static String put(String path, Object body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    public static String delete(String path, String token) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    // ─── Utilidad ────────────────────────────────────────────────────────────

    public static <T> T parsear(String json, Class<T> clase) throws Exception {
        return mapper.readValue(json, clase);
    }

    public static <T> java.util.List<T> parsearLista(String json, Class<T> clase) throws Exception {
        return mapper.readValue(json,
                mapper.getTypeFactory().constructCollectionType(java.util.List.class, clase));
    }
}
