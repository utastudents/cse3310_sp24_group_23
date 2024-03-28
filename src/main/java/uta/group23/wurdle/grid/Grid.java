package uta.group23.wurdle.grid;

import java.util.ArrayList;
import java.util.UUID;

public class Grid {
    private int width;
    private int height;
    private int charCount;
    private Cell[][] grid;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public void addCharCount(int count) {
        charCount += count;
    }

    public int getCharCount() {
        return charCount;
    }

    public float calculateDensity() {
        return (float) charCount / (width * height);
    }

    public void setCell(int x, int y, char letter) {
        grid[x][y].letter = letter;
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }

    public int getCellIndex(int x, int y) {
        return x * width + y;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void claimCell(int x, int y, UUID playerId) {
        grid[x][y].claimId = playerId;
    }

}
