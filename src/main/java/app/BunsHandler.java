package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.NoSuchElementException;

public class BunsHandler implements HttpHandler {

    private final BakeryService service;

    public BunsHandler(BakeryService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            Integer id = HttpUtils.extractId(exchange, "/buns");
            Integer adminId = HttpUtils.getQueryParamInt(exchange, "adminId");

            switch (method) {
                case "GET" -> {
                    if (id == null) {
                        HttpUtils.sendJson(exchange, 200, service.getAllBunsInfo());
                    } else {
                        HttpUtils.sendJson(exchange, 200, service.getBunById(id));
                    }
                }
                case "POST" -> {
                    service.checkAdminAccess(adminId);
                    Buns req = HttpUtils.MAPPER.readValue(HttpUtils.readBody(exchange), Buns.class);
                    Buns created = service.createBun(req.price);
                    HttpUtils.sendJson(exchange, 201, created);
                }
                case "PUT" -> {
                    if (id == null) throw new IllegalArgumentException("Укажите ID булочки в пути (/buns/{id})");
                    service.checkAdminAccess(adminId);
                    Buns req = HttpUtils.MAPPER.readValue(HttpUtils.readBody(exchange), Buns.class);
                    Buns updated = service.updateBunPrice(id, req.price);
                    HttpUtils.sendJson(exchange, 200, updated);
                }
                case "DELETE" -> {
                    if (id == null) throw new IllegalArgumentException("Укажите ID булочки в пути (/buns/{id})");
                    service.checkAdminAccess(adminId);
                    service.deleteBun(id);
                    HttpUtils.sendJson(exchange, 200, service.getAllBunsInfo());
                }
                default -> {
                    exchange.getResponseHeaders().set("Allow", "GET, POST, PUT, DELETE");
                    HttpUtils.sendError(exchange, 405, "Метод не поддерживается");
                }
            }
        } catch (NumberFormatException e) {
            HttpUtils.sendError(exchange, 400, "Некорректный числовой формат параметров");
        } catch (IllegalArgumentException e) {
            HttpUtils.sendError(exchange, 400, e.getMessage());
        } catch (NoSuchElementException e) {
            HttpUtils.sendError(exchange, 404, e.getMessage());
        } catch (Exception e) {
            HttpUtils.sendError(exchange, 400, "Ошибка обработки запроса: " + e.getMessage());
        }
    }
}
