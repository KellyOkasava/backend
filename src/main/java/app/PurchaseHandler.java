package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

public class PurchaseHandler implements HttpHandler {

    private final BakeryService service;

    public PurchaseHandler(BakeryService service) {
        this.service = service;
    }

    public static class PurchaseRequest {
        public Integer buyerId;
        public Integer bunId;
        public Integer quantity;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            HttpUtils.sendError(exchange, 405, "Для покупки используйте метод POST");
            return;
        }

        try {
            PurchaseRequest req = HttpUtils.MAPPER.readValue(HttpUtils.readBody(exchange), PurchaseRequest.class);
            if (req.buyerId == null) {
                throw new IllegalArgumentException("Поле buyerId обязательно для покупки");
            }
            Map<String, Object> result = service.buyBuns(req.buyerId, req.bunId, req.quantity);
            HttpUtils.sendJson(exchange, 200, result);
        } catch (IllegalArgumentException e) {
            HttpUtils.sendError(exchange, 400, e.getMessage());
        } catch (NoSuchElementException e) {
            HttpUtils.sendError(exchange, 404, e.getMessage());
        } catch (Exception e) {
            HttpUtils.sendError(exchange, 400, "Ошибка обработки покупки: " + e.getMessage());
        }
    }
}
