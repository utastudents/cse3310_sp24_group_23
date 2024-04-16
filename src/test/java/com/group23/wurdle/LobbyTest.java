package com.group23.wurdle;

import org.junit.Test;

import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Player;
import uta.group23.wurdle.socket.Lobby;
import uta.group23.wurdle.socket.Mode;
import uta.group23.wurdle.socket.Status;

import com.google.gson.*;

import static org.junit.Assert.assertEquals;

public class LobbyTest {

    @Test
    public void testPlayerAddedToLobby() {
        Player bot = new Player("bot", Colour.Red);
        Lobby lobby = new Lobby("lobbyName", "lobbyID", Status.WAITING, 0, Mode.Timer, "password", 4, bot);
        lobby.addPlayer(bot);
        assertEquals(1, lobby.getPlayers().size());
    }

    @Test
    public void testUUIDValidity() {
        Player bot = new Player("bot", Colour.Red);
        assertEquals(36, bot.getplayerId().length());
    }

    @Test
    public void testLobbyAddition() {
        Context ctx = new Context();
        Player bot = new Player("bot", Colour.Red);
        Player bot2 = new Player("bot2", Colour.Blue);
        Lobby lobby = new Lobby("lobbyName", "lobbyID", Status.WAITING, 0, Mode.Timer, "password", 4, bot);
        ctx.addLobby(lobby, bot);
        lobby.addPlayer(bot2);
        assertEquals(1, ctx.getLobbySize());
    }

}