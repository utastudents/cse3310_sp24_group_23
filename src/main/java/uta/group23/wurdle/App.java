package uta.group23.wurdle;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

import uta.group23.wurdle.models.Context;

import uta.group23.wurdle.server.HTTPServer;
import uta.group23.wurdle.socket.*;
import uta.group23.wurdle.Game;

public class App {

    public static void main(String[] args) {

        Context ctx = new Context();
        HTTPServer server = new HTTPServer(9023, ctx);

        try {
            // port 9123
            WSServer socketServer = new WSServer("127.0.0.1", new InetSocketAddress(9123), ctx);
            socketServer.setReuseAddr(true);
            socketServer.start();

            // Check if game can start
            int playerCount = ctx.getPlayerSize();
            boolean possiblestart = false;

            if (playerCount >= 2 && playerCount <= 4) 
            {
                possiblestart = true;
            }

            if (possiblestart) {
                Game game = new Game();
                game.start();
              }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
