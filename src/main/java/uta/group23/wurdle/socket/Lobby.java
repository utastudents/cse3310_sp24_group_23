package uta.group23.wurdle.socket;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import uta.group23.wurdle.Game;
import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.grid.GridGen;
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
    private HashSet<Player> players;
    private int playerCount;
    private int playerCap;
    private Game game;

    public Lobby(String lobbyName, String lobbyID, Status lobbyStatus, int playerNum, Mode lobbyMode, String password,
            int playerCap,
            Player lobbyOwner) {
        this.lobbyName = lobbyName;
        this.lobbyID = lobbyID;
        this.lobbyOwner = lobbyOwner;
        this.lobbyStatus = lobbyStatus;
        this.password = password;

        this.lobbyMode = lobbyMode;
        this.players = new HashSet<>();
        this.playerCap = playerCap;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Game getGame() {
        return game;
    }

    public void setLobbyOwner(Player player) {
        this.lobbyOwner = player;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public String getPassword() {
        return password;
    }

    private void checkFull() {
        if (playerCount == playerCap) {
            lobbyStatus = Status.IN_PROGRESS;
        }
    }

    public void addPlayer(Player player) {

        if (playerCount == playerCap) {
            return;
        }

        // check if player is already in a lobby by nickname or by connection
        for (Player p : players) {
            if (p.getNickname().equals(player.getNickname())) {
                return;
            }
            // don't need to compare conn since nickname is unique
        }
        players.add(player);
        this.playerCount++;
        checkFull();
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

    public HashSet<Player> getPlayers() {
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
        jsonObject.addProperty("playerCount", players.size());
        jsonObject.addProperty("id", lobbyID);
        jsonObject.addProperty("ownerID", lobbyOwner.getId());
        jsonObject.addProperty("ownerName", lobbyOwner.getNickname());
        jsonObject.addProperty("lobbyMode", lobbyMode.toString());

        return jsonObject;
    }

    public JsonObject toJsonObjectPrivate() {
        JsonArray playersList = new JsonArray();
        for (Player p : players) {
            playersList.add(p.toJsonObject());

        }

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("lobbyName", lobbyName);
        jsonObject.addProperty("lobbyStatus", lobbyStatus.toString());
        jsonObject.addProperty("playerCount", players.size());
        jsonObject.addProperty("id", lobbyID);
        jsonObject.addProperty("ownerID", lobbyOwner.getId());
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("lobbyMode", lobbyMode.toString());

        jsonObject.add("players", playersList);
        return jsonObject;

    }
}
