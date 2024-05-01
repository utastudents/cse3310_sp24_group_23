package uta.group23.wurdle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.models.Player;

public class Game {
    private Grid grid;
    // Timer

    private HashMap<String, Boolean> words;

    public Game() {
        this.grid = new Grid(20, 20);
        this.words = new HashMap<String, Boolean>();
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

    public HashMap<String, Boolean> getWords() {
        return words;
    }

    public Boolean isWordFound(String word) {
        return words.get(word);
    }

    public void initializeWords(Set<String> words) {
        // initialize the words to be found
        for (String word : words) {
            this.words.put(word, false);
        }
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

        // if it is, add it to the found list
        // grid has words <String, Direction>
        // if the word is found, remove it from the grid
        if (grid.getWords().containsKey(constructedWord)) {
            // set word to found
            words.put(constructedWord, true);
            // grid.getWords().remove(constructedWord);
            grid.addFoundWord(constructedWord);
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

    public String getWordListJson() {
        JsonObject wordList = new JsonObject();
        // toString creates "[]" instead of []
        /*
         * wordList.addProperty("words", grid.getWords().keySet().toString());
         * wordList.addProperty("foundWords", grid.getFoundWords().toString());
         */
        JsonArray words = new JsonArray();
        for (String word : grid.getWords().keySet()) {
            words.add(word);
        }
        wordList.add("words", words);

        JsonArray foundWords = new JsonArray();
        for (String word : grid.getFoundWords()) {
            foundWords.add(word);
        }
        wordList.add("foundWords", foundWords);

        return wordList.toString();
    }

}
