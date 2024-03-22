package uta.group23.wurdle.grid;

import java.io.File;

enum Direction {
    Horizontal,
    Vertical,
    Diagonal
}

public class GridGen {
    private File wordList;
    private String grid;

    public GridGen() {
        this.wordList = new File("wordlist.txt");
        this.grid = "";
    }

    public void getDensity(int startIndex, int endIndex) {
    }

    public int getCellIndex(int x, int y) {
        return x * grid.length() + y;
    }

    private Direction calculateDirection(int startIndex, int endIndex) {
        int previousAngle = 0;

        return Direction.Horizontal; // placeholder
    }

}
