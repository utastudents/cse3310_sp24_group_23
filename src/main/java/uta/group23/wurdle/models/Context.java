package uta.group23.wurdle.models;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

public class Context {
    private Map<Player, WebSocket> clients = new HashMap<>();

    public Context() {
    }

}
