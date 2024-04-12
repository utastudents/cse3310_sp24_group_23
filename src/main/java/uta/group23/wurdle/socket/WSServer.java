package uta.group23.wurdle.socket;

import org.java_websocket.*;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Player;

import java.util.UUID;

import javax.swing.text.AbstractDocument.Content;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WSServer extends WebSocketServer {
    private Context ctx;
    private InetSocketAddress addr;

    public WSServer(String hostname, InetSocketAddress addr, Context ctx) {
        super(addr);

        this.addr = addr;
        this.ctx = ctx;

    }

    public void broadCastMessageBoard() {
        String messages = ctx.getMessageBoard();
        broadcast(messages);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JsonObject j = JsonParser.parseString(message).getAsJsonObject();
        if (j.get("type").getAsString().equals("message")) {
            String msg = j.get("content").getAsString();

            // add to message board
            ctx.addMessage(ctx.getPlayerByConn(conn), msg);
            // broadcast messageBoard to all clients
            /**
             * messageBoard: [
             * {"username": "Player 1", "message": "Hello, is anyone here?"},]
             */

            broadCastMessageBoard();

        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer buff) {
        System.out.println("Message recv: bytebuff");

    }

    @Override
    public void onStart() {
        System.out.println("Started socket server on port " + this.addr.getPort() + " at " + this.addr.getAddress());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New client connected " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        String newId = UUID.randomUUID().toString();
        Player newPlayer = new Player(newId, conn);

        ctx.addPlayer(newPlayer);
        System.out.println("Client count: " + ctx.getPlayerSize());

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // TODO Auto-generated method stub
        System.out.println("Connection closed : " + conn.getResourceDescriptor());

        ctx.removePlayer(conn);
        System.out.println("Client count: " + ctx.getPlayerSize());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // TODO Auto-generated method stub
        System.out.println("Error occured: " + ex.getMessage());
    }

}