package uta.group23.wurdle.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Grid {
    private int width;
    private int height;
    private int charCount;
    private Cell[][] grid;
    private float density;
    private HashMap<String, Direction> words = new HashMap<>();

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        this.words = new HashMap<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public void addWord(String word, Direction direction) {
        words.put(word, direction);
    }

    public HashMap<String, Direction> getWords() {
        return words;
    }

    public float getDensity() {
        return density;
    }

    public void calculateDensity() {
        int totalCells = width * height;
        density = (float) charCount / totalCells;
    }

    public void addCharCount(int count) {
        charCount += count;
    }

    public int getCharCount() {
        return charCount;
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
