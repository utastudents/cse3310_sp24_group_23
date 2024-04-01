package uta.group23.wurdle.grid;

import java.util.UUID;

public class Cell {
    UUID claimId;
    char letter;

    public char getLetter() {
        return this.letter;
    }

    public Cell() {
        this.claimId = null;
        this.letter = ' ';
    }
}