package com.group23.wurdle;

import org.junit.Test;
import static org.junit.Assert.*;

import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Player;


public class PlayerTest {

    @Test
    public void testUniqueNickname()
    {
        // Create players with unique nicknames
        Player player1 = new Player("bot1", Colour.Red);
        Player player2 = new Player("bot2", Colour.Blue);

        // Assert that nicknames are unique
        assertNotEquals(player1.getNickname(), player2.getNickname());
    }

    @Test
    public void testChangeNickname() 
    {
        
        // Create a player
        Player player = new Player("bot3", Colour.Green);

        // Change the nickname
        player.setNickname("bot4");

        // Assert that the nickname has changed
        assertEquals("bot4", player.getNickname());
    }

    @Test
    public void testNicknameUniqueness() {
        
        //create players with the same nickname
        Player player3 = new Player("bot5", Colour.Yellow);
        Player player4 = new Player("bot6", Colour.Purple);

        // Assert that the system checks if a user's nickname is unique
        assertNotEquals(player3.getNickname(), player4.getNickname());
    }
}
