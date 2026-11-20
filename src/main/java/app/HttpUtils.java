package app;

import com.sun.net.httpserver.HttpExchange;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {
    public static final JsonMapper MAPPER = JsonMapper.builder().build();

    public static void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        byte[] bytes = MAPPER.writeValueAsBytes(data);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    public static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJson(exchange, statusCode, Map.of("error", message));
    }

    public static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    public static Integer extractId(HttpExchange exchange, String basePath) {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith(basePath + "/")) {
            String idPart = path.substring((basePath + "/").length());
            if (!idPart.isEmpty()) {
                return Integer.parseInt(idPart);
            }
        }
        return getQueryParamInt(exchange, "id");
    }

    public static Integer getQueryParamInt(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return Integer.parseInt(pair[1]);
            }
        }
        return null;
    }
}
