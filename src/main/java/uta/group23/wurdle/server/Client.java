package uta.group23.wurdle.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;

import uta.group23.wurdle.models.Player;

public class Client {

    private WebSocket conn;

    public Client(WebSocket conn) {
        this.conn = conn;
    }

    public WebSocket getConn() {
        return conn;
    }
}