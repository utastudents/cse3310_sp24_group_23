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
        for (Lobby l : lobbies) {
            if (l.getPlayers().contains(player)) {
                l.removePlayer(player);
                lobbies.remove(l);
            }
        }

    }

    public void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    public Lobby searchID(String lobbyID) {
        for (Lobby l : lobbies) {
            if (l.getLobbyID() == lobbyID) {
                return l;
            }
        }
        return null;

    }

    String toJson() {
        JsonArray lobbyList = new JsonArray();
        for (Lobby l : lobbies) {
            lobbyList.add(l.summary());
        }
        System.out.println(lobbyList.toString());
        return "{\"lobbyList\":" + lobbyList.toString() + "}";
    }

}