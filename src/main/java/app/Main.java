package app;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        BakeryService bakeryService = new BakeryService();

        server.createContext("/ping", new PingHandler());
        server.createContext("/buns", new BunsHandler(bakeryService));
        server.createContext("/buyers", new BuyersHandler(bakeryService));
        server.createContext("/buy", new PurchaseHandler(bakeryService));

        server.start();
        System.out.println("Сервер запущен: http://localhost:" + PORT);
    }
}
