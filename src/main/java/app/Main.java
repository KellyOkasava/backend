package app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    private static final int PORT = 8080;

    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/ping", new PingHandler());

        server.createContext("/buns", new BakeryHandler());

        server.start();
        System.out.println("Сервер запущен: http://localhost:" + PORT + "/ping");
    }
}