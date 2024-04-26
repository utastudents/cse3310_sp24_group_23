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

   public void checkWord(Player player, String selectedCells[]) 
    {
        //check is at least 3 cells are selected
        if (selectedCells.length < 3) 
        {
            //clear selected cells to allow player to select new word
            selectedCells = new String[0];
        return;
        }
    }

    public void removeWordFound(String word) {
    }

    public void assignPoints(Player player, String selectedCells[]) 
    {
        //calculate score for word found based on word length
        int lengthmultiplier = 2;
        int score = selectedCells.length * lengthmultiplier;

        //get players current score and add on the score for the word found
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
    
    public void hint_player(Player player) 
    {
        //check the is player reached 5 consecHints
        if (player.getConsecHints() == 5) 
        {
            // Disqualify the player
        } 
        else 
        {
            // Increment consecHints
            player.incrementConsecHints();
        }
    }

}
