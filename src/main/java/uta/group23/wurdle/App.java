package uta.group23.wurdle;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

import uta.group23.wurdle.models.Context;

import uta.group23.wurdle.server.HTTPServer;
import uta.group23.wurdle.server.RootHandler;
import uta.group23.wurdle.socket.*;

public class App {

    public static void main(String[] args) {

        Context ctx = new Context();
        HTTPServer server = new HTTPServer(8080, ctx);

        try {
            WSServer socketServer = new WSServer(new InetSocketAddress(9999), ctx);
            socketServer.run();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}