package com.group23.wurdle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uta.group23.wurdle.models.Player;
import uta.group23.wurdle.socket.Lobby;
import uta.group23.wurdle.socket.LobbyList;
import uta.group23.wurdle.socket.Mode;
import uta.group23.wurdle.socket.Status;
import uta.group23.wurdle.models.Colour;

public class LobbyTest {
    private Lobby lobby;
    private Player player1, player2, host;
    private LobbyList lobbyList;

    @Before
    public void setUp() {
        host = new Player("Host", Colour.Blue);
        player1 = new Player("Player1", Colour.Red);
        player2 = new Player("Player2", Colour.Green);
        lobby = new Lobby("Test Lobby", "001", Status.WAITING, 0, Mode.Point, "securePass", 10, host);
        lobbyList = new LobbyList();
        lobbyList.addLobby(lobby, host, null);
    }

    @Test
    public void testCreateLobby() {
        assertEquals("Expected lobby name to match", "Test Lobby", lobby.getLobbyName());
        assertEquals("Expected lobby ID to match", "001", lobby.getLobbyID());
        assertEquals("Expected lobby password to match", "securePass", lobby.getPassword());
        assertNotNull("Expected game object to not be null", lobby.getGame());
    }


    

    @Test
    public void testDisplayLobbies() {
        assertNotNull(lobbyList.toJson());
        assertEquals(1, lobbyList.length());
    }

    @Test
    public void testJoinLobby() {
        lobby.addPlayer(player1);
        assertTrue(lobby.getPlayers().contains(player1));
        assertEquals(2, lobby.getPlayers().size()); 
    }

    @Test
    public void testLeaveLobby() {
        lobby.addPlayer(player1);
        lobby.removePlayer(player1);
        assertFalse(lobby.getPlayers().contains(player1));
    }

    @Test
    public void testChangeMaxPlayers() {
        lobby.setPlayerCap(15);  
        assertEquals("Player capacity should update correctly.", 15, lobby.getPlayerCap());  
    }


    @Test
    public void testLobbyUniqueIDDisplay() {
        assertEquals("001", lobby.getLobbyID());
    }

    @Test
    public void testReadyStateChange() {
        lobby.addPlayer(player1);
        assertFalse(player1.isReady());
        player1.setReady(true);
        assertTrue(player1.isReady());
    }

    @Test
    public void testAddPlayerWhenFull() {
        for (int i = 0; i < 10; i++) {  
            lobby.addPlayer(new Player("Player" + i, Colour.Yellow));
        }
        Player newPlayer = new Player("Late", Colour.Purple);
        lobby.addPlayer(newPlayer);
        assertFalse(lobby.getPlayers().contains(newPlayer));
    }

    
}
