package uta.group23.wurdle.models;

import org.java_websocket.*;

public class Message {
    private Player sender;
    private String message;
    private ChatScope scope;

    public Message(Player sender, String msg, ChatScope scope) {
        this.sender = sender;
        this.message = msg;
        this.scope = scope;

    }

    public Player getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void postMessage(String Message) {

    }
}
