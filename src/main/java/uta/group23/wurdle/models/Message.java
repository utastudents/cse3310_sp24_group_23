package uta.group23.wurdle.models;

import org.java_websocket.*;

public class Message {
    private String nick;
    private String message;
    private ChatScope scope;

    public Message(String nick, String msg, ChatScope scope) {
        this.nick = nick;
        this.message = msg;
        this.scope = scope;

    }

    public String getSender() {
        return nick;
    }

    public String getMessage() {
        return message;
    }

    public void postMessage(String Message) {

    }
}
