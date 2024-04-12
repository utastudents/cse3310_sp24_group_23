package uta.group23.wurdle.models;

import java.util.UUID;

import org.java_websocket.WebSocket;

import uta.group23.wurdle.server.Client;
import uta.group23.wurdle.socket.Lobby;

public class Player {
    private Client client;
    private String nickname;
    private String playerID;
    private int score;
    private int consecHints;
    private String[] words_found;
    private Colour user_colour;
    private int num_found;
    private PlayerType user_type;

    Player() {
        this.playerID = UUID.randomUUID().toString();
    }

    public Player(String nickname, WebSocket conn) {
        this();
        this.nickname = nickname;
        this.client = new Client(conn);
        this.score = 0;
    }

    public Client getClient() {
        return this.client;
    }

    public Player(String nickname, Colour user_colour) {
        this();
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
        return false;
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

}
