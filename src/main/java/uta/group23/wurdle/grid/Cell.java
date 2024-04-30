package uta.group23.wurdle.grid;

import java.util.UUID;

public class Cell {
    private String claimId;
    private char letter;
    private Boolean isClaimed;

    public char getLetter() {
        return this.letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public String getClaimId() {
        return this.claimId;
    }

    public Boolean getIsClaimed() {
        return this.isClaimed;
    }

    public void setIsClaimed(Boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public Cell() {
        this.claimId = "";
        this.isClaimed = false;
        this.letter = ' ';
    }
}