package uta.group23.server;

import java.io.IOException;
import com.sun.net.httpserver.*;

public class CreatePlayerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = "<h1>Server started on port</h1>" + "<h1>Port: " + 1234 + "</h1>";
        he.sendResponseHeaders(200, response.length());
        he.getResponseBody().write(response.getBytes());
        he.getResponseBody().close();

        if (he.getRequestMethod().equalsIgnoreCase("GET")) {
            System.out.println("GET request from " + he.getRemoteAddress().getAddress());
        } else if (he.getRequestMethod().equalsIgnoreCase("POST")) {
            System.out.println("POST request");
        }
    }
}