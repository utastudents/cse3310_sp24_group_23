package uta.group23.wurdle.models;

public enum PlayerType {
    Player('P'),
    Spectator('S');

    private char type;

    PlayerType(char type) {
        this.type = type;
    }

    public void changeType() {
        if (this == Player) {
            this.type = 'S';
        } else {
            this.type = 'P';
        }

    }
}
