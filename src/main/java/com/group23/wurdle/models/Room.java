package com.group23.wurdle.models;

public class Room {
    private String name;
    private int roomID;
    private int roomOwnerID;

    public Room(String name, int roomID, int roomOwnerID) {
        this.name = name;
        this.roomID = roomID;
        this.roomOwnerID = roomOwnerID;
    }

}
