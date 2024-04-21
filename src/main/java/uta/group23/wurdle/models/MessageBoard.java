package uta.group23.wurdle.models;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.*;

public class MessageBoard {

  private ArrayList<Message> messages = new ArrayList<>();

  public void addMessage(String nick, String message, ChatScope scope) {

    messages.add(new Message(nick, message, scope));

  }

  public Message getLastMessage() {
    return messages.get(messages.size() - 1);
  }

  public String toJson() {
    // only send last message
    // ["data",{"id":30,"data":{"id":0,"msg":"test2"}}] (S->C)

    JsonObject j = new JsonObject();
    j.addProperty("id", messages.get(messages.size() - 1).getSender());
    j.addProperty("msg", messages.get(messages.size() - 1).getMessage());
    return j.toString();
  }
}
