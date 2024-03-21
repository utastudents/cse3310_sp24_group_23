package uta.group23.wurdle.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.java_websocket.*;

import com.sun.net.httpserver.*;

public class RootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = "<h1>Server started on port</h1>" + "<h1>Port: " + 1234 + "</h1>";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();

        // send a websocket message

    }

}