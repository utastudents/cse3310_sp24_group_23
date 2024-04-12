package uta.group23.wurdle.models;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import org.java_websocket.WebSocket;

import com.google.gson.JsonArray;

import uta.group23.wurdle.server.Client;

public class Context {
    private ArrayList<Player> players = new ArrayList<>();
    private MessageBoard messageBoard = new MessageBoard();

    public Context() {
    }

    public void addPlayer(Player player) {
        players.add(player);
        System.out.println("Client added" + player.getClient().getConn().getResourceDescriptor());
    }

    public void removePlayer(WebSocket conn) {
        players.removeIf(client -> client.getClient().getConn().equals(conn));
        System.out.println("Client removed" + conn.getResourceDescriptor());
    }

    public void addMessage(Player sender, String message) {
        messageBoard.addMessage(sender, message, ChatScope.Global);
    }

    public Player getPlayerByConn(WebSocket conn) {
        return players.stream().filter(client -> client.getClient().getConn().equals(conn)).findFirst().orElse(null);
    }

    public int getPlayerSize() {
        return players.size();
    }

    public String getMessageBoard() {
        return messageBoard.toJson();
    }

}
