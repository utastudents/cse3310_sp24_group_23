package uta.group23.wurdle.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.java_websocket.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import uta.group23.wurdle.Game;

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
    private HashSet<Player> readyPlayers;

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
        this.game = new Game();
        this.playerCount = 0;
        this.readyPlayers = new HashSet<>();
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
        /*
         * if (playerCount == playerCap) {
         * lobbyStatus = Status.IN_PROGRESS;
         * }
         */
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

    public void startGame() throws IOException {
        // populate grid with words
        lobbyStatus = Status.IN_PROGRESS;

        GridGen gridGen = new GridGen(); // grid generator helper object
        String staticPath = "./html/";
        gridGen.setWordList(staticPath + "words.txt");
        gridGen.generateGrid(game.getGrid());

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
        jsonObject.addProperty("readyCount", readyPlayers.size());

        /*
         * if (lobbyStatus == Status.IN_PROGRESS) {
         * // wordlist
         * JsonArray words = new JsonArray();
         * 
         * for (String word : game.getWords().keySet()) {
         * JsonObject wordObj = new JsonObject();
         * wordObj.addProperty("word", word);
         * wordObj.addProperty("found", game.getWords().get(word));
         * words.add(wordObj);
         * }
         * 
         * jsonObject.add("words", words);
         * }
         */

        jsonObject.add("players", playersList);
        return jsonObject;

    }

    public void setPlayerReady(Player player) {
        readyPlayers.add(player);

    }

    public void setPlayerUnready(Player player) {
        readyPlayers.remove(player);

    }
}
