package uta.group23.wurdle.models;

import org.java_websocket.*;

public class Message {
    private Player sender;
    private String message;

    public Message(Player sender, String msg) {
        this.sender = sender;
        this.message = msg;

    }
    public void postMessage(StringMessage){

    }
}
