package uta.group23.wurdle.socket;

import java.util.ArrayList;
import java.util.List;

public class LobbyList {
    private List<Lobby> lobbies;

    public LobbyList() {
        lobbies = new ArrayList<>();
    }

    public void addLobby(Lobby lobby) {
        lobbies.add(lobby);
    }

    public void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    } 
    public void searchID(int lobbyID) {
     
    }
    
}