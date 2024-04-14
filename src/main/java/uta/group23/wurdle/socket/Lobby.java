package uta.group23.wurdle.socket;

import java.util.ArrayList;

import com.google.gson.JsonArray;

import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Player;

public class Lobby {
    private String lobbyName;
    private String lobbyID;
    private Player lobbyOwner;
    private Status lobbyStatus;
    private int playerNum;
    private int password;
    private Mode lobbyMode;
    private ArrayList<Player> players;
    private int playerCount;
    private int playerCap;

    public Lobby(String lobbyName, String lobbyID, Status lobbyStatus, int playerNum, Mode lobbyMode, int password,
            int playerCap,
            Player lobbyOwner) {
        this.lobbyName = lobbyName;
        this.lobbyID = lobbyID;
        this.lobbyOwner = lobbyOwner;
        this.lobbyStatus = lobbyStatus;
        this.password = password;

        this.lobbyMode = lobbyMode;
        this.players = new ArrayList<Player>();
        this.playerCap = playerCap;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getLobbyID() {
        return lobbyID;
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void displayPlayerStats(Player player) {
        // Display player stats logic
    }

    String toJson() {
        return "{\"lobbyName\":\"" + lobbyName + "\",\"lobbyID\":" + lobbyID + ",\"lobbyOwner\":\""
                + lobbyOwner.getNickname() + "\",\"lobbyStatus\":\"" + lobbyStatus + "\",\"playerNum\":" + playerNum
                + ",\"password\":" + password + ",\"lobbyMode\":\"" + lobbyMode + "\",\"players\":" + players
                + ",\"playerCount\":" + playerCount + ",\"playerCap\":" + playerCap + "}";

    }

    String summary() {
        /*
         * 
         * {"lobbyName": "Lobby 1", "status": "In Progress", "playerCount": "2",
         * "lobbyId": ""},]}
         * 
         */
        return "{\"lobbyName\":\"" + lobbyName + "\",\"status\":\"" + lobbyStatus + "\",\"playerCount\":\""
                + playerCount
                + "\",\"lobbyId\":\"" + lobbyID + "\"}";
    }
}
