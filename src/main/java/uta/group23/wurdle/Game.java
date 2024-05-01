package uta.group23.wurdle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.models.Player;

public class Game {
    private Grid grid;
    // Timer
    private File wordList;
    private ArrayList<String> found;

    public Game() {
        this.grid = new Grid(20, 20);
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

    
    public void checkWord(Player player, List<int[]> path) {
        // check is at least 3 cells are selected
        if (path.size() < 3) {
            return;
        }

        String constructedWord = "";
        for (int i = 0; i < path.size(); i++) {
            // get the letter from the cell
            char letter = grid.getCell(path.get(i)[0], path.get(i)[1]).getLetter();
            constructedWord += letter;
            // add the letter to the constructed word
        }

        // TODO: check if the word is in the word list
    }

    public void removeWordFound(String word) {
    }

    public void assignPoints(Player player, String selectedCells[]) {
        // calculate score for word found based on word length
        int lengthmultiplier = 2;
        int score = selectedCells.length * lengthmultiplier;

        // get players current scscore and add on the score for the word found
        int currentScore = player.getScore();
        player.setScore(currentScore + score);
    }

    public boolean isGameOver(Player player, int pointThreshold) {
        // Check if the game is over based on a score threshold
        return player.getScore() >= pointThreshold;
    }

    public void display_selection(int selected_cell, int hovered_cell) {

    }

    public void displayPoints(Player player, int points) {

    }

    public void displayWordsFound(Player player, int numFound) {

    }

    public void hint_player(Player player) {
        // check the is player reached 5 consecHints
        if (player.getConsecHints() == 5) {
            // Disqualify the player
        } else {
            // Increment consecHints
            player.incrementConsecHints();
        }
    }

    public void completeWord(Player player1, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'completeWord'");
    }

}
