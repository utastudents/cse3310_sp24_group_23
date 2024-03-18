package uta.group23.wurdle.models;

import java.util.UUID;

public class Player {
    private String nickname;
    private String id;
    private int score;

    Player() {
        this.id = UUID.randomUUID().toString();
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

}
