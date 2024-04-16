package com.group23.wurdle;

import uta.group23.wurdle.models.Context;
import org.junit.Test;
import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Colour;

import uta.group23.wurdle.models.Player;
import uta.group23.wurdle.socket.Lobby;
import uta.group23.wurdle.socket.Mode;
import uta.group23.wurdle.socket.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import static org.junit.Assert.assertEquals;

public class MessageTest {
    @Test
    public void testMessageSentToAllPlayers() {
        Context ctx = new Context();
        Player bot = new Player("bot", Colour.Red);
        Player bot2 = new Player("bot2", Colour.Blue);
        Lobby lobby = new Lobby("lobbyName", "lobbyID", Status.WAITING, 0, Mode.Timer, "password", 4, bot);
        ctx.addLobby(lobby, bot);
        lobby.addPlayer(bot2);

        ctx.addMessage(bot.getNickname(), "Hello");
        ctx.addMessage(bot2.getNickname(), "Hi");

        String messageBoard = ctx.getMessageBoard();
        // parse json
        JsonArray messages = JsonParser.parseString(messageBoard).getAsJsonObject().get("messageBoard")
                .getAsJsonArray();
        assertEquals(2, messages.size());

        // check if first message is Hello by bot
        assertEquals("Hello", messages.get(0).getAsJsonObject().get("message").getAsString());
        assertEquals(bot.getNickname(), messages.get(0).getAsJsonObject().get("username").getAsString());

        // check if second message is Hi by bot2
        assertEquals("Hi", messages.get(1).getAsJsonObject().get("message").getAsString());
        assertEquals(bot2.getNickname(), messages.get(1).getAsJsonObject().get("username").getAsString());

    }
}
