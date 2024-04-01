package uta.group23.wurdle.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.*;

import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Player;

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
            server.createContext("/api/create_player", new CreatePlayerHandler(this.context));
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

class CreatePlayerHandler implements HttpHandler {

    private Context context;

    public CreatePlayerHandler(Context ctx) {
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
        Player player = new Player(playerName);

        String response = "<h1>Player created</h1>" + "<h1>Player: " + player.getNickname() + "</h1>" + "<h1>Score: "
                + player.getScore() + "</h1>" + "<h1>Id: " + player.getId() + "</h1>";
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
        Player player = new Player(playerName);

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
