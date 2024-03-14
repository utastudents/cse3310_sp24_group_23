package com.group23.wurdle;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(1297);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
