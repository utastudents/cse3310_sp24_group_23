package uta.group23.wurdle.grid;

import java.util.UUID;

public class Cell {
    private String claimId;
    private char letter;
    private Boolean isClaimed;
    private Boolean isHighlighted;
    private String selectorID;

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
        this.isHighlighted = false;
        this.selectorID = "";
    }

    public void setIsHighlighted(boolean b) {
        // TODO Auto-generated method stub
        this.isHighlighted = b;
    }

    public Boolean getIsHighlighted() {
        return this.isHighlighted;
    }

    public void setSelectorID(String id) {
        this.selectorID = id;
    }

    public String getSelectorID() {
        return this.selectorID;
    }
}