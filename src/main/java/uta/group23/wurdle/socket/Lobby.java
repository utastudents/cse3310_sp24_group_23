package uta.group23.wurdle.socket;

import java.util.ArrayList;

import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Player;

public class Lobby {
    private String lobbyName;
    private int lobbyID;
    private Status lobbyStatus;
    private int playerCount;
    private Mode lobbyMode;
    private ArrayList<Player> players;

    public Lobby(String lobbyName, int lobbyID, Status lobbyStatus, int playerCount, Mode lobbyMode) {
        this.lobbyName = lobbyName;
        this.lobbyID = lobbyID;
        this.lobbyStatus = lobbyStatus;
        this.playerCount = playerCount;
        this.lobbyMode = lobbyMode;
        this.players = new ArrayList<Player>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void addSpectator(Player player) {
        // add spectate
    }

    public void removeSpectate(Player player) {
        // remove spectate
    }

    public void setPlayerColour(Player player, Colour colour) {
        // Set player color
    }

    public boolean readyPlayer(Player player) {
        // Ready player
        return false;
    }

    public void startGame() {
        // Start game
    }

}
