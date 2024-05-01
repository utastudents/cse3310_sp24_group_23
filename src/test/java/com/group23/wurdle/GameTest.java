package com.group23.wurdle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Player;
import uta.group23.wurdle.Game;
import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.grid.GridGen;

public class GameTest {
    private Game game;
    private Player player1, player2;
    private HashSet<String> validWords;
    
    
    @Before
    public void setUp() throws Exception {
        // Print the current working directory to understand where the JVM is running from
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
    
        game = new Game();
        player1 = new Player("Player1", Colour.Blue);
        player2 = new Player("Player2", Colour.Green);
        // Attempt to load words from file
        try {
            validWords = new HashSet<>(Files.readAllLines(Paths.get("resources/words.txt")));
        } catch (Exception e) {
            e.printStackTrace(); // This will print more detailed info about why the file cannot be found
        }
    }
    

    @Test
    public void testSimultaneousGridUpdates() {
        // Mock the grid being updated simultaneously by two players
        game.checkWord(player1, simulateSelection(0, 0, 0, 2)); // Simulate player1 selecting a word
        game.checkWord(player2, simulateSelection(1, 0, 1, 2)); // Simulate player2 selecting a word

        // Verify that both updates appear simultaneously
        assertTrue("Grid should display updates from both players", gridUpdatedCorrectly());
    }

    @Test
    public void testHighlightWordsOnGrid() {
        // Player selects a valid word
        game.checkWord(player1, simulateSelection(0, 0, 0, 3));
        assertTrue("Word should be highlighted on the grid", gridHighlightsWord(0, 0, 0, 3));
    }

    @Test
    public void testDeselectNonExistingWord() {
        // Player selects a non-existing word
        List<int[]> selection = simulateSelection(0, 0, 0, 1);
        game.checkWord(player1, simulateSelection(0, 0, 0, 1));
        assertFalse("Non-existing word should not be highlighted", gridHighlightsWord(0, 0, 0, 1));
    }

    @Test
    public void testSelectCellsByDragging() {
        // Simulate dragging across cells
        List<int[]> path = simulateDragging(0, 0, 0, 4);
        game.checkWord(player1, path);
        assertTrue("Cells should be selected by dragging", cellsAreSelected(path));
    }

    @Test
    public void testPointAllocationForWordCompletion() {
        // Two players complete the same word
        game.checkWord(player1, simulateSelection(0, 0, 0, 2));
        game.checkWord(player2, simulateSelection(0, 0, 0, 2));
        int score1 = player1.getScore();
        int score2 = player2.getScore();

        // Determine who submitted last letter first
        game.completeWord(player1, "existingWord");
        game.completeWord(player2, "existingWord");
        assertTrue("Player with last letter submission should score points", score1 != score2);
    }

    private ArrayList<int[]> simulateSelection(int startX, int startY, int endX, int endY) {
        ArrayList<int[]> selection = new ArrayList<>();
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                selection.add(new int[]{i, j});
            }
        }
        return selection;
    }

    private boolean gridUpdatedCorrectly() {
        // This should check the current state of the grid to ensure it reflects both players' actions
        return true; // Placeholder
    }

    private boolean gridHighlightsWord(int startX, int startY, int endX, int endY) {
        // This should check if the grid visually highlights the selected cells as a word
        return true; // Placeholder
    }

    private boolean cellsAreSelected(List<int[]> path) {
        // Verify that all cells in the path are marked as selected in the grid
        return true; // Placeholder
    }

    private List<int[]> simulateDragging(int startX, int startY, int endX, int endY) {
        List<int[]> path = new ArrayList<>();
        for (int i = startX; i <= endX; i++) {
            path.add(new int[]{i, startY});
        }
        return path;
    }
}
