package uta.group23.wurdle.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.*;

import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Player;
import java.io.File;
import java.io.FileInputStream;

public class HTTPServer {
    private int port;
    private HttpServer server;
    private Executor executor;
    private Context context;

    public HTTPServer(int port, Context context) {
        this.port = port;
        this.context = context;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new RootHandler());

            server.createContext("/room", new JoinRoomHandler(this.context));
            server.setExecutor(null);
            server.start();

            System.out.println("HTTPServer started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void stop() {
        server.stop(0);
    }
}

class JoinRoomHandler implements HttpHandler {

    private Context context;

    public JoinRoomHandler(Context ctx) {
        this.context = ctx;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        String requestURI = he.getRequestURI().toString();
        String[] split = requestURI.split("/");
        if (split.length != 4) {
            he.sendResponseHeaders(400, 0);
            he.getResponseBody().close();
            return;
        }

        String playerName = split[3];

        String response = "Room";

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

class RootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        // Serve an HTML file
        String htmlPath = "./html/";
        String requestURI = he.getRequestURI().toString();
        if (requestURI.equals("/")) {
            htmlPath += "index.html";
        } else {
            htmlPath += requestURI.substring(1);
        }

        File file = new File(htmlPath);
        if (!file.exists()) {
            he.sendResponseHeaders(404, 0);
            he.getResponseBody().close();
            return;
        }

        he.sendResponseHeaders(200, file.length());
        OutputStream os = he.getResponseBody();
        FileInputStream fs = new FileInputStream(file);
        byte[] buffer = new byte[0x10000];
        int count = 0;
        while ((count = fs.read(buffer)) >= 0) {
            os.write(buffer, 0, count);
        }
        fs.close();
        os.close();

    }

}