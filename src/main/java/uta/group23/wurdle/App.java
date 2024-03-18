package uta.group23.wurdle;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.socket.*;
import uta.group23.server.RootHandler;
import uta.group23.server.CreatePlayerHandler;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {

        Context ctx = new Context();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new RootHandler());
            server.createContext("/create_player", new CreatePlayerHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP Server started on port 8080");

            WSServer socketServer = new WSServer(new InetSocketAddress(9999), ctx);
            socketServer.run();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}