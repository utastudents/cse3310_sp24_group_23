package uta.group23.wurdle.models;

import java.util.UUID;

import uta.group23.wurdle.socket.Lobby;

public class Player {
    private String nickname;
    private String id;
    private int score;

    private String[] words_found;
    private Colour user_colour;
    private int num_found;
    private UserType user_type;

    Player() {
        this.id = UUID.randomUUID().toString();
    }

    public Player(String nickname) {
        this();
        this.nickname = nickname;
        this.score = 0;
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

    public String getId() {
        return id;
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
        if (user_type == UserType.Player) {
            user_type = UserType.Spectator;
        } else {
            user_type = UserType.Player;
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

    public boolean isReader() {
        return false;
    }

    public void selectCell(Lobby lobby, int row, int col) {

    }

    public void sendMessage(String[] message) {

    }

}
