package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.NoSuchElementException;

public class BuyersHandler implements HttpHandler {

    private final BakeryService service;

    public BuyersHandler(BakeryService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            Integer id = HttpUtils.extractId(exchange, "/buyers");

            switch (method) {
                case "GET" -> {
                    if (id == null) {
                        HttpUtils.sendJson(exchange, 200, service.getAllBuyers());
                    } else {
                        HttpUtils.sendJson(exchange, 200, service.getBuyerById(id));
                    }
                }
                case "POST" -> {
                    Buyer req = HttpUtils.MAPPER.readValue(HttpUtils.readBody(exchange), Buyer.class);
                    Buyer created = service.createBuyer(req.status, req.wallet);
                    HttpUtils.sendJson(exchange, 201, created);
                }
                case "PUT" -> {
                    if (id == null) throw new IllegalArgumentException("Укажите ID покупателя в пути (/buyers/{id})");
                    Buyer req = HttpUtils.MAPPER.readValue(HttpUtils.readBody(exchange), Buyer.class);
                    Buyer updated = service.updateBuyer(id, req.status, req.wallet);
                    HttpUtils.sendJson(exchange, 200, updated);
                }
                case "DELETE" -> {
                    if (id == null) throw new IllegalArgumentException("Укажите ID покупателя в пути (/buyers/{id})");
                    service.deleteBuyer(id);
                    HttpUtils.sendJson(exchange, 200, service.getAllBuyers());
                }
                default -> {
                    exchange.getResponseHeaders().set("Allow", "GET, POST, PUT, DELETE");
                    HttpUtils.sendError(exchange, 405, "Метод не поддерживается");
                }
            }
        } catch (NumberFormatException e) {
            HttpUtils.sendError(exchange, 400, "Некорректный числовой формат");
        } catch (IllegalArgumentException e) {
            HttpUtils.sendError(exchange, 400, e.getMessage());
        } catch (NoSuchElementException e) {
            HttpUtils.sendError(exchange, 404, e.getMessage());
        } catch (Exception e) {
            HttpUtils.sendError(exchange, 400, "Ошибка обработки запроса: " + e.getMessage());
        }
    }
}
