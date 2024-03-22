package uta.group23.wurdle.models;

import uta.group23.wurdle.socket.Lobby;

public class User {
    private String nickname;
    private int points;
    private String[] words_found;
    private Colour user_colour;
    private int num_found;

    public User(String nickname, int points, String[] words_found, Colour user_colour, int num_found) {
        this.nickname = nickname;
        this.points = points;
        this.words_found = words_found;
        this.user_colour = user_colour;
        this.num_found = num_found;
    }

    public void setNickName(String nickname) {
        this.nickname = nickname;
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
