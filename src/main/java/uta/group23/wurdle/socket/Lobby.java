package uta.group23.wurdle.socket;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Player;

public class Lobby {
    private String lobbyName;
    private String lobbyID;
    private Player lobbyOwner;
    private Status lobbyStatus;
    private int playerNum;
    private String password;
    private Mode lobbyMode;
    private ArrayList<Player> players;
    private int playerCount;
    private int playerCap;

    public Lobby(String lobbyName, String lobbyID, Status lobbyStatus, int playerNum, Mode lobbyMode, String password,
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
        this.playerCount++;
    }

    public void removePlayer(Player player) {
        players.remove(player);
        this.playerCount--;
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

    public Status getLobbyStatus() {
        return this.lobbyStatus;
    }

    public String toJson() {
        // only return the non-sensitive information
        return "{\"lobbyName\":\"" + lobbyName + "\",\"lobbyID\":" + lobbyID + ",\"lobbyOwner\":\""
                + lobbyOwner.getNickname() + "\",\"lobbyStatus\":\"" + lobbyStatus + "\",\"playerNum\":" + playerNum
                + ",\"lobbyMode\":\"" + lobbyMode + "\",\"players\":" + players + ",\"playerCount\":" + playerCount
                + ",\"playerCap\":" + playerCap + "}";
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("lobbyName", lobbyName);
        jsonObject.addProperty("lobbyStatus", lobbyStatus.toString());
        jsonObject.addProperty("playerCount", playerCount);
        jsonObject.addProperty("id", lobbyID);
        jsonObject.addProperty("ownerID", lobbyOwner.getId());

        return jsonObject;
    }
}
