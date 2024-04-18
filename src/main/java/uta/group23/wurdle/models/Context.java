package uta.group23.wurdle.models;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import org.java_websocket.WebSocket;

import com.google.gson.JsonArray;

import uta.group23.wurdle.server.Client;
import uta.group23.wurdle.socket.Lobby;

public class Context {
    private ArrayList<Player> players = new ArrayList<>();
    private MessageBoard messageBoard = new MessageBoard();
    private ArrayList<Lobby> lobbies = new ArrayList<>();

    public Context() {
    }

    public void addPlayer(Player player) {
        players.add(player);
        System.out.println("Client added" + player.getClient().getConn().getResourceDescriptor());
    }

    public void removePlayer(WebSocket conn) {

        // remove player from possible lobbies O(n*m)
        String nick = getPlayerByConn(conn).getNickname();
        for (Lobby l : lobbies) {
            // if lobby owner and player count zero, remove lobby
            // if players present, transfer ownership to next player
            if (l.getPlayers().contains(getPlayerByConn(conn))) {
                l.removePlayer(getPlayerByConn(conn));
                if (l.getPlayers().size() == 0) {
                    lobbies.remove(l);
                } else {
                    l.setLobbyOwner(l.getPlayers().toArray(new Player[0])[0]);
                }
            }
        }
        System.out.println("Client removed" + nick);

        players.remove(getPlayerByConn(conn));
    }

    public void addMessage(String nick, String message) {

        messageBoard.addMessage(nick, message, ChatScope.Global);

    }

    public Player getPlayerByConn(WebSocket conn) {
        return players.stream().filter(client -> client.getClient().getConn().equals(conn)).findFirst().orElse(null);
    }

    public int getPlayerSize() {
        return players.size();
    }

    public int getLobbySize() {
        return lobbies.size();
    }

    public String getMessageBoard() {
        return messageBoard.toJson();
    }

    public void addLobby(Lobby lobby, Player player) {
        System.out.println("Adding lobby");
        // check if player is already in a lobby
        // In our case, lobby means a game room
        for (Lobby l : lobbies) {
            if (l.getPlayers().contains(player)) {
                l.removePlayer(player);
                lobbies.remove(l);
            }
        }

        lobby.addPlayer(player);
        lobbies.add(lobby);

    }

    public void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    public Lobby searchID(String lobbyID) {
        for (Lobby l : lobbies) {
            if (l.getLobbyID().equals(lobbyID)) {
                return l;
            }
        }
        return null;

    }

    public boolean isLobbyNameTaken(String lobbyName) {
        for (Lobby l : lobbies) {
            if (l.getLobbyName().equals(lobbyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUsernameTaken(String username) {
        for (Player p : players) {
            if (p.getNickname().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String getLobbyList() {
        JsonArray lobbyList = new JsonArray();
        for (Lobby l : lobbies) {
            lobbyList.add(l.toJsonObject());
        }

        return "{\"lobbyList\":" + lobbyList.toString() + "}";
    }

}
