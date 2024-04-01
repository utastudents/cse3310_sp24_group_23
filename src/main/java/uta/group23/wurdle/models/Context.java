package uta.group23.wurdle.models;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import org.java_websocket.WebSocket;

import uta.group23.wurdle.server.Client;

public class Context {
    private ArrayList<Client> clients = new ArrayList<>();

    public Context() {
    }

    public void addClient(WebSocket conn) {
        clients.add(new Client(conn));
        System.out.println("Client added" + conn.getResourceDescriptor());
    }

}
