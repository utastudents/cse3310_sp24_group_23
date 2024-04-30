
package com.group23.wurdle;

import uta.group23.wurdle.models.Context;

import org.junit.Test;
import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.models.Timer;
import uta.group23.wurdle.Game;
import uta.group23.wurdle.models.Leaderboard;
import uta.group23.wurdle.grid.GridGen;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class StartGameTest {

    @Test
    public void testStartTimer() {
        // Create a Timer object
        Timer timer = new Timer();

        assertNotNull(timer); // Check if timer object is not null

        // Start the timer
        timer.startTimer();

        // Since the startTimer method runs in a separate thread and there's no direct method to check its status,
        // we can only ensure that the method doesn't throw any exceptions
    }

    @Test
    public void testStartGame(){
        Game game = new Game();

        assertNotNull(game); // Check if game object is not null

        // Start the game
        game.start();

    }


}
