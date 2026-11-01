package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class BakeryHandler implements HttpHandler {
            
    Buns makBuns = new Buns( 67, 25);
    Buyer currentBuyer = new Buyer("standart", 0);

    @Override 
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        String jsonAnswer = "";
        String query = exchange.getRequestURI().getQuery();

        if ("GET".equals(method) && query == null) {
            jsonAnswer = "{\"Приветствие\":\"Добро пожаловать в нашу пекарню 'Маковые булочки'! Для покупки допишите параметры в адресную строку. Сначала вводится количество желаемых булочек, потом статус клиента и имеющееся сумма.\"," +
                         "\"Цена за булочку сегодня\":" + makBuns.price + "," +
                         "\"остаток булочек на складе\":" + makBuns.quantity + "}";
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

            Buyer currentBuyer = new Buyer(clientStatus, clientWallet);

            if ("standart".equals(currentBuyer.status) && wantToBuy > 5) {
                jsonAnswer = "{\"error\":\"Обычный клиент не может купить больше 5 булочек за один раз\"}";
            } 

            else {
                int totalPrice = makBuns.price * wantToBuy;
                if ("VIP".equals(currentBuyer.status)) {
                    totalPrice = (int) (totalPrice * 0.8); 
                }

                if (!makBuns.IsAvailability(wantToBuy)) {
                    jsonAnswer = "{\"error\":\"На складе недостаточно булочек!\"}";
                } else if (!currentBuyer.IsEnoughMoney(totalPrice)) {
                    jsonAnswer = "{\"error\":\"У покупателя не хватает денег! Нужно: " + totalPrice + ", а есть: " + currentBuyer.wallet + "\"}";
                } else {
                    
                    makBuns.quantity -= wantToBuy;      
                    currentBuyer.wallet -= totalPrice;  
                    
                    jsonAnswer = "{\"Статус покупки\":\"Успешная покупка!\",\"статус текущего клиента\":\"" + currentBuyer.status + 
                                 "\",\"итоговая цена\":" + totalPrice + 
                                 ",\"остаток в кошельке\":" + currentBuyer.wallet + 
                                 ",\"остаток булочек на складе\":" + makBuns.quantity + "}";
                }
            }
        }

        byte[] bodyBytes = jsonAnswer.getBytes("UTF-8");

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(200, bodyBytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bodyBytes);
        }
    }
}
