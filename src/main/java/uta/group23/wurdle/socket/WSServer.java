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

    public WSServer(InetSocketAddress addr, Context ctx) {
        this.addr = addr;
        this.ctx = ctx;
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JsonObject j = JsonParser.parseString(message).getAsJsonObject();
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer buff) {
        System.out.println("Message recv: bytebuff");

    }

    @Override
    public void onStart() {
        System.out.println("Started socket server on port " + this.addr.getPort());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New client connected " + conn.getResourceDescriptor());
        String newId = UUID.randomUUID().toString();

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // TODO Auto-generated method stub
        System.out.println("Connection closed : " + conn.getResourceDescriptor());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println(ex.toString());
    }

}