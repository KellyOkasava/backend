package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BakeryHandler implements HttpHandler {
            
    Buns makBuns = new Buns(1, 67, 25);
    Buyer currentBuyer = new Buyer("1", "standart", 0);
    JsonMapper mapper = JsonMapper.builder().build();

    @Override 
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        Object responseObject = null;
        String query = exchange.getRequestURI().getQuery();

        if ("GET".equals(method) && query == null) {
            Map<String, Object> getResponse = new HashMap<>();
            getResponse.put("Приветствие", "Добро пожаловать в нашу пекарню 'Маковые булочки'! Для покупки допишите параметры в адресную строку. Сначала вводится количество желаемых булочек, потом статус клиента и имеющееся сумма.");
            getResponse.put("Цена за булочку сегодня", makBuns.price);
            getResponse.put("остаток булочек на складе", makBuns.quantity);
            responseObject = getResponse;
        }
        
         else if ("POST".equals(method) || (query != null && query.contains("quantity="))) {
  
            int wantToBuy = 1;
            String clientStatus = "standart";
            int clientWallet = 0;
            
            if (query != null) {
                if (query.contains("quantity=")) {
                    wantToBuy = Integer.parseInt(query.split("quantity=")[1].split("&")[0]);
                }
                if (query.contains("status=")) {
                    clientStatus = query.split("status=")[1].split("&")[0];
                }
                if (query.contains("wallet=")) {
                    clientWallet = Integer.parseInt(query.split("wallet=")[1].split("&")[0]);
                }
            }

            Buyer currentBuyer = new Buyer("1", clientStatus, clientWallet);

            Map<String, Object> errorMap = new HashMap<>();
            if (wantToBuy <= 0) {
                errorMap.put("error", "Количество булочек для покупки должно быть больше 0!");
                responseObject = errorMap;
            }
            
            else if ("standart".equals(currentBuyer.status) && wantToBuy > 5) {
                errorMap.put("error", "Обычный клиент не может купить больше 5 булочек за один раз");
                responseObject = errorMap;
            } 

            else {
                int totalPrice = makBuns.price * wantToBuy;
                if ("VIP".equals(currentBuyer.status)) {
                    totalPrice = (int) (totalPrice * 0.8); 
                }

                if (!makBuns.IsAvailability(wantToBuy)) {
                    errorMap.put("error", "На складе недостаточно булочек!");
                    responseObject = errorMap;
                } else if (!currentBuyer.IsEnoughMoney(totalPrice)) {
                    errorMap.put("error", "У покупателя не хватает денег! Нужно: " + totalPrice + ", а есть: " + currentBuyer.wallet);
                    responseObject = errorMap;
                } else {
                    
                    makBuns.quantity -= wantToBuy;      
                    currentBuyer.wallet -= totalPrice;  
                    
                    Map<String, Object> successMap = new HashMap<>();
                    successMap.put("Статус покупки", "Успешная покупка!");
                    successMap.put("статус текущего клиента", currentBuyer.status);
                    successMap.put("итоговая цена", totalPrice);
                    successMap.put("остаток в кошельке", currentBuyer.wallet);
                    successMap.put("остаток булочек на складе", makBuns.quantity);
                    responseObject = successMap;
                }
            }
        }

        byte[] bodyBytes = mapper.writeValueAsBytes(responseObject);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(200, bodyBytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bodyBytes);
        }
    }
}
