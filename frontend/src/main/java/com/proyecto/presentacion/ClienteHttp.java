package com.proyecto.presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

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
        HttpRequest req = HttpRequestFactory.crearGet(path, null);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return resp.body();
    }

    public static String getConToken(String path, String token) throws Exception {
        HttpRequest req = HttpRequestFactory.crearGet(path, token);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return resp.body();
    }

    // ─── POST ────────────────────────────────────────────────────────────────

    public static String post(String path, Object body) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequestFactory.crearPost(path, json, null);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new Exception("HTTP " + resp.statusCode() + ": " + resp.body());
        }
        return resp.body();
    }

    public static String postConToken(String path, Object body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequestFactory.crearPost(path, json, token);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new Exception("HTTP " + resp.statusCode() + ": " + resp.body());
        }
        return resp.body();
    }

    // ─── PUT ─────────────────────────────────────────────────────────────────

    public static String put(String path, Object body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequestFactory.crearPut(path, json, token);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new Exception("HTTP " + resp.statusCode() + ": " + resp.body());
        }
        return resp.body();
    }

    /** PUT sin body (útil para endpoints con query params como asignar especialidad). */
    public static String putSinBody(String path, String token) throws Exception {
        HttpRequest req = HttpRequestFactory.crearPut(path, "", token);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new Exception("HTTP " + resp.statusCode() + ": " + resp.body());
        }
        return resp.body();
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    public static String delete(String path, String token) throws Exception {
        HttpRequest req = HttpRequestFactory.crearDelete(path, token);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new Exception("HTTP " + resp.statusCode() + ": " + resp.body());
        }
        return resp.body();
    }

    // ─── PATCH ───────────────────────────────────────────────────────────────

    public static String patch(String path, Object body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequestFactory.crearPatch(path, json, token);
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new Exception("HTTP " + resp.statusCode() + ": " + resp.body());
        }
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

    /**
     * Factory Method para centralizar la creación de HttpRequest.
     */
    private static class HttpRequestFactory {
        private static HttpRequest.Builder baseBuilder(String path, String token) {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "application/json");
            if (token != null && !token.isBlank()) {
                builder.header("Authorization", "Bearer " + token);
            }
            return builder;
        }

        private static HttpRequest crearGet(String path, String token) {
            return baseBuilder(path, token).GET().build();
        }

        private static HttpRequest crearPost(String path, String jsonBody, String token) {
            return baseBuilder(path, token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
        }

        private static HttpRequest crearPut(String path, String jsonBody, String token) {
            return baseBuilder(path, token)
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
        }

        private static HttpRequest crearDelete(String path, String token) {
            return baseBuilder(path, token).DELETE().build();
        }

        private static HttpRequest crearPatch(String path, String jsonBody, String token) {
            return baseBuilder(path, token)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
        }
    }
}
