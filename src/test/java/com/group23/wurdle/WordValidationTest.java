package com.group23.wurdle;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Player;
import uta.group23.wurdle.Game;

public class WordValidationTest {

    @Test
    public void testWordLengthRequirement() 
    {
        Game game = new Game();
        Player player = new Player("Sad", Colour.Red);
        ArrayList<int[]> selectedCells = new ArrayList<>();
        
        // Add cells to form a word less than 3 characters long
        selectedCells.add(new int[]{0, 0});
        selectedCells.add(new int[]{0, 1});
        
        // Simulate checkWord method
        game.checkWord(player, selectedCells);
        
        // Assert that no points are assigned
        assertEquals(0, player.getScore()); // Player's score should remain unchanged
    }

    @Test
    public void testPointsAssignment() 
    {
        Game game = new Game();
        Player player = new Player("Happy", Colour.Red);
        String word = "sample"; // A sample word for testing
        
        // Simulate assignPoints method
        game.assignPoints(player, word);
        
        // Calculate expected score
        int expectedScore = word.length() * 2; // Length of word * 2
        
        // Assert that points are correctly assigned
        assertEquals(expectedScore, player.getScore());
    }
}
