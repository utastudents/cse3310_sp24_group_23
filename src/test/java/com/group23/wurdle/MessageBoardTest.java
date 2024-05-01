package com.group23.wurdle;
import org.junit.Test;
import static org.junit.Assert.*;


import uta.group23.wurdle.models.MessageBoard;
import uta.group23.wurdle.Game;
import uta.group23.wurdle.models.ChatScope;
import uta.group23.wurdle.models.Message;
import uta.group23.wurdle.models.Context;


public class MessageBoardTest {

    @Test
    public void testAddMessageOnMessageBoard() {
        // Arrange
        MessageBoard messageBoard = new MessageBoard();
        String nick = "User1";
        String message = "Hello, world!";
        ChatScope scope = ChatScope.Global;

        // Act
        messageBoard.addMessage(nick, message, scope);
        Message lastMessage = messageBoard.getLastMessage();

        // Assert
        assertEquals(nick, lastMessage.getSender());
        assertEquals(message, lastMessage.getMessage());
    }

    @Test
    public void testAddMessageDuringGame() {
        // Arrange
        MessageBoard messageBoard = new MessageBoard();
        String nick = "User2";
        String message = "Let's play!";
        ChatScope scope = ChatScope.Local;

        // Act
        messageBoard.addMessage(nick, message, scope);
        Message lastMessage = messageBoard.getLastMessage();

        // Assert
        assertEquals(nick, lastMessage.getSender());
        assertEquals(message, lastMessage.getMessage());
    }

    // Add more test cases for other scenarios...

    @Test
    public void testGlobalMessageBoardInteraction() {
        // Arrange
        MessageBoard messageBoard = new MessageBoard();
        String[] users = {"User1", "User2", "User3"};
        String[] messages = {"Message 1", "Message 2", "Message 3"};

        // Act
        for (int i = 0; i < users.length; i++) {
            messageBoard.addMessage(users[i], messages[i], ChatScope.Global);
        }
        Message lastMessage = messageBoard.getLastMessage();

        // Assert
        assertEquals(users[users.length - 1], lastMessage.getSender());
        assertEquals(messages[messages.length - 1], lastMessage.getMessage());
    }

    @Test
    public void testRealTimeMessageUpdate() {
        // Arrange
        MessageBoard messageBoard = new MessageBoard();
        String[] users = {"User1", "User2", "User3"};
        String[] messages = {"Message 1", "Message 2", "Message 3"};

        // Act
        for (int i = 0; i < users.length; i++) {
            messageBoard.addMessage(users[i], messages[i], ChatScope.Global);
            Message lastMessage = messageBoard.getLastMessage();
            // Simulate real-time display (not really possible in a unit test)
            System.out.println("Real-time message displayed: " + lastMessage.getSender() + ": " + lastMessage.getMessage());
        }

        // Assert
        // Not applicable for real-time behavior in a unit test
        // The messages are printed in real-time in the console
    }

    @Test
    public void testDisplayUserJoinLeaveEvents() {
        // Arrange
        MessageBoard messageBoard = new MessageBoard();
        String userJoin = "User1";
        String userLeave = "User2";

        // Act
        messageBoard.addMessage(userJoin, "Joined the lobby.", ChatScope.Global);
        messageBoard.addMessage(userLeave, "Left the lobby.", ChatScope.Global);
        Message lastMessage = messageBoard.getLastMessage();

        // Assert
        assertEquals(userLeave, lastMessage.getSender());
        assertEquals("Left the lobby.", lastMessage.getMessage());
    }

}
