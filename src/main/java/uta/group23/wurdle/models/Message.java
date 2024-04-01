package uta.group23.wurdle.models;

import org.java_websocket.*;

public class Message {
    private Player sender;
    private Message message;
    private ChatScope scope;

    public Message(Player sender, Message msg, ChatScope scope) {
        this.sender = sender;
        this.message = msg;
        this.scope = scope;

    }
    public void postMessage(String Message){

    }
}
