package uta.group23.wurdle.models;

public enum UserType {
    Player('P'),
    Spectator('S');

    private char type;

    UserType(char type) {
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
