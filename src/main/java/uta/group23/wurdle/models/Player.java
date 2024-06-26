package uta.group23.wurdle.models;

import java.util.UUID;

import org.java_websocket.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import uta.group23.wurdle.server.Client;
import uta.group23.wurdle.socket.Lobby;

public class Player {
    private Client client;
    private String nickname;
    private String playerID;
    private int score;
    private boolean ready;
    private int consecHints;
    private String[] words_found;
    private Colour user_colour;
    private int num_found;
    private PlayerType user_type;
    private int lastActive;

    Player() {
        this.playerID = UUID.randomUUID().toString();
        this.score = 0;
        this.lastActive = 0;
        this.ready = false;
    }

    public Player(String nickname, WebSocket conn) {
        this.playerID = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.client = new Client(conn);
        this.score = 0;
    }

    public Client getClient() {
        return this.client;
    }

    public WebSocket getConn() {
        return this.client.getConn();
    }

    public Player(String nickname, Colour user_colour) {
        this.playerID = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.user_colour = user_colour;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public String getplayerId() {
        return playerID;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getConsecHints() {
        return this.consecHints;
    }

    public void incrementConsecHints() {
        this.consecHints++;
    }

    @Override
    public String toString() {
        return nickname + " " + score;
    }

    public void toggleSpectator() {
        if (user_type == PlayerType.Player) {
            user_type = PlayerType.Spectator;
        } else {
            user_type = PlayerType.Player;
        }
    }

    public void joinLobby(Lobby lobby) {
      if (user_type == PlayerType.Player) {
            lobby.addPlayer(this);
        } else {
            lobby.addSpectator(this);
        }
    }

    public Lobby createLobby() {
        return null;
    }

    public Colour setColour() {
        return null;
    }

    public void leaveLobby() {

    }

    public void lobbyPasswordToggle() {

    }

    public boolean isReady() {
        return ready;
    }
    
     public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void selectCell(Lobby lobby, int row, int col) {

    }

    public void sendMessage(String[] message) {

    }

    public void playAgain() {

    }

    public String getId() {
        return playerID;
    }

    public JsonObject toJsonObject() {

        JsonObject obj = new JsonObject();
        obj.addProperty("nickname", nickname);
        obj.addProperty("playerID", playerID);
        obj.addProperty("score", score);
        return obj;

    }

}
