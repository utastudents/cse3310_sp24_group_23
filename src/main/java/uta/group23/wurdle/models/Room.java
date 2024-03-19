package uta.group23.wurdle.models;

import java.util.ArrayList;
import uta.group23.wurdle.server.Client;

public class Room {
    private String name;
    private String roomID;
    private int roomOwnerID;
    private ArrayList<Client> players = new ArrayList<Client>();

    public Room(String name, String roomID, int roomOwnerID) {
        this.name = name;
        this.roomID = roomID;
        this.roomOwnerID = roomOwnerID;
    }

}
