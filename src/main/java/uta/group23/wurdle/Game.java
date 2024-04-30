package uta.group23.wurdle;

import java.io.File;
import java.util.ArrayList;

import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.models.Player;

public class Game {
    private Grid grid;
    // Timer

    private ArrayList<String> found;

    public Game() {
        this.grid = new Grid(20, 20);

        this.found = new ArrayList<String>();
    }

    public void initializeGrid() {
    }

    public void startTimer() {
    }

    public void stop() {
    }

    public void updateGrid() {
    }

    public void updateTimer() {
    }

    // check coords
    public void checkWord(Player player, ArrayList<int[]> selectedCells) {
        // check is at least 3 cells are selected
        if (selectedCells.size() < 3) {
            return;
        }

        String constructedWord = "";
        for (int i = 0; i < selectedCells.size(); i++) {
            // get the letter from the cell
            char letter = grid.getCell(selectedCells.get(i)[0], selectedCells.get(i)[1]).getLetter();
            constructedWord += letter;
            // add the letter to the constructed word
        }
        System.out.println("Constructed word: " + constructedWord);

        // TODO: check if the word is in the word list
        // if it is, add it to the found list
        // grid has words <String, Direction>
        // if the word is found, remove it from the grid
        if (grid.getWords().containsKey(constructedWord)) {
            found.add(constructedWord);
            grid.getWords().remove(constructedWord);
            assignPoints(player, constructedWord);

            // set state of cells to claimed
            for (int i = 0; i < selectedCells.size(); i++) {
                // set claimId to player id
                grid.getCell(selectedCells.get(i)[0], selectedCells.get(i)[1]).setClaimId(player.getId());
                grid.getCell(selectedCells.get(i)[0], selectedCells.get(i)[1]).setIsClaimed(true);
            }
        }

    }

    public void removeWordFound(String word) {
    }

    public void assignPoints(Player player, String word) {
        // calculate score for word found based on word length
        int lengthmultiplier = 2;
        int score = word.length() * lengthmultiplier;

        // get players current score and add on the score for the word found
        int currentScore = player.getScore();
        player.setScore(currentScore + score);

        System.out.println("Player " + player.getNickname() + " found word: " + word + " for " + score + " points");
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

    public Grid getGrid() {

        return grid;
    }

}
