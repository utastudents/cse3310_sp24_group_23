package uta.group23.wurdle.socket;

import java.util.ArrayList;

import com.google.gson.JsonArray;

import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Player;

public class LobbyList {
    private ArrayList<Lobby> lobbies;

    public LobbyList() {
        lobbies = new ArrayList<>();
    }

    public void addLobby(Lobby lobby, Player player, Context context) {
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

    public int length() {
        return lobbies.size();
    }

    public Lobby searchID(String lobbyID) {
        for (Lobby l : lobbies) {
            if (l.getLobbyID() == lobbyID) {
                return l;
            }
        }
        return null;

    }

    public String toJson() {

        JsonArray lobbyList = new JsonArray();
        for (Lobby l : lobbies) {
            lobbyList.add(l.toJson());
        }

        return "{\"lobbyList\":" + lobbyList.toString() + "}";
    }

}