package uta.group23.wurdle;

import java.io.File;
import java.util.ArrayList;

import uta.group23.wurdle.models.Player;

public class Game {
    private ArrayList<Player> players;
    private ArrayList<String> grid;
    // Timer
    private File wordList;
    private ArrayList<String> found;

    public Game() {
        this.players = new ArrayList<Player>();
        this.grid = new ArrayList<String>();
        this.wordList = new File("wordlist.txt");
        this.found = new ArrayList<String>();
    }

    public void initializeGrid() {
    }

    public void start() {
    }

    public void startTimer() {
    }

    public void stop() {
    }

    public void updateGrid() {
    }

    public void updateTimer() {
    }

    public void checkWord(Player player, String selectedCells[]) {
    }

    public void removeWordFound(String word) {
    }

    public void assignPoints(Player player) {
    }

    public boolean isGameOver(Player player, int pointThreshold) {
        return false;
    }

    public void display_selection(int selected_cell, int hovered_cell) {

    }

    public void displayPoints(Player player, int points) {

    }

    public void displayWordsFound(Player player, int numFound) {

    }

}
