package uta.group23.wurdle.models;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.*;

public class MessageBoard {

  private ArrayList<Message> messages = new ArrayList<>();

  public void addMessage(String nick, String message, ChatScope scope) {

    messages.add(new Message(nick, message, scope));

  }

  public String toJson() {
    /**
     * messageBoard: [
     * {"username": "Player 1", "message": "Hello, is anyone here?"},]
     */
    // messageBoard is a key that must be present in the JSON object
    JsonArray messageBoard = new JsonArray();
    for (Message m : messages) {
      JsonObject message = new JsonObject();
      message.addProperty("username", m.getSender());
      message.addProperty("message", m.getMessage());
      messageBoard.add(message);
    }

    System.out.println(messageBoard.toString());

    return "{\"messageBoard\":" + messageBoard.toString() + "}";
  }
}
