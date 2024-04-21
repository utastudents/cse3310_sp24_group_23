package uta.group23.wurdle.socket;

import org.java_websocket.*;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Message;
import uta.group23.wurdle.models.Player;

import java.util.UUID;

import javax.swing.text.AbstractDocument.Content;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WSServer extends WebSocketServer {
    private Context ctx;
    private InetSocketAddress addr;

    public WSServer(String hostname, InetSocketAddress addr, Context ctx) {
        super(addr);

        this.addr = addr;
        this.ctx = ctx;

    }

    public void broadCastLobbyList() {
        String lobbies = ctx.getLobbyList();
        broadcast(lobbies);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        /*
         * id (1) player add ["data" : {"id": 1, "data": <user information here>}] S->C
         * id (2) player remove ["data" : {"id": 2, "data": {"id" : <user id>}}]
         * id (11) server->client message ["data": {"id": 11, "data":}]
         * id (12) client->server message ["data": {"id": 12, "data":}]
         * 
         * id (30) send message ["data",{"id":30,"data":"\<message content\>"}]
         * id (30) send message (server->client)
         * ["data",{"id":30,"data":{"id":0,"msg":"test2"}}] id: id of sender, msg:
         * message content
         * 
         */

        JsonArray j = JsonParser.parseString(message).getAsJsonArray();
        System.out.println(j);
        // check if first key is data
        // ["join",{"id":"","data":{"username":"test"}}]
        // ["data",{"id":30,"data":"\<message content\>"}] example
        // ["join",{"id":"d6576da3-3a90-4fff-a144-4540354804fe","data":{"username":"person"}}]

        if (j.get(0).getAsString().equals("join")) {
            JsonObject data = j.get(1).getAsJsonObject();
            String username = data.get("data").getAsJsonObject().get("username").getAsString();
            ctx.getPlayerByConn(conn).setNickname(username);

            ctx.addMessage("System", "New player connected: " + username);

            broadcast("[\"data\",{\"id\":30,\"data\":{\"id\":\"system\",\"msg\":\"" + username
                    + " has joined the game\"}}]");

        }

        if (j.get(0).getAsString().equals("create")) {
            /*
             * JSON.stringify([
             * "create",
             * {
             * id: clientState.uuid,
             * data: {
             * name: document.getElementById("lobby-name").value,
             * owner: clientState.uuid,
             * players: [],
             * playerCount: document.getElementById("player-count").value,
             * lobbyMode: document.getElementById("game-type").value,
             * password: document.getElementById("lobby-password").value,
             * },
             * },
             * ]);
             */
            // create lobby
            JsonObject data = j.get(1).getAsJsonObject();
            String lobbyName = data.get("data").getAsJsonObject().get("name").getAsString();
            String lobbyID = UUID.randomUUID().toString();
            String password = data.get("data").getAsJsonObject().get("password").getAsString();
            int playerCap = data.get("data").getAsJsonObject().get("playerCount").getAsInt();
            String mode = data.get("data").getAsJsonObject().get("lobbyMode").getAsString();
            String modeStr = mode.equals("timer") ? "Timer" : "Point";
            Mode lobbyMode = Mode.valueOf(modeStr);
            Player lobbyOwner = ctx.getPlayerByConn(conn);
            Lobby lobby = new Lobby(lobbyName, lobbyID, Status.WAITING, 0, lobbyMode, password, playerCap, lobbyOwner);

            ctx.addLobby(lobby, lobbyOwner);

            broadCastLobbyList();
        }

        if (j.get(0).getAsString().equals("data")) {
            JsonObject data = j.get(1).getAsJsonObject();
            switch (data.get("id").getAsInt()) {
                case 11:
                    // server->client message
                    break;
                case 12:
                    // client->server message

                    break;
                case 30:
                    // send message

                    String msgData = data.get("data").getAsJsonObject().get("msg").getAsString();
                    Player player = ctx.getPlayerByConn(conn);
                    String msg = msgData;
                    ctx.addMessage(player.getNickname(), msg);
                    // broadCastMessageBoard();

                    broadcast(ctx.getMessageBoard());
                    break;
                default:
                    break;
            }
        } else {
            return;
        }

        /**
         * if (j.get("type").getAsString().equals("message")) {
         * String msg = j.get("content").getAsString();
         * 
         * // add to message board
         * ctx.addMessage(ctx.getPlayerByConn(conn).getNickname(), msg);
         * // broadcast messageBoard to all clients
         * /**
         * messageBoard: [
         * {"username": "Player 1", "message": "Hello, is anyone here?"},]
         * 
         * 
         * broadCastMessageBoard();
         * 
         * }
         * 
         * broadCastLobbyList();
         * 
         * if (j.get("type").getAsString().equals("setUsername")) {
         * setUsername(conn, j);
         * }
         * 
         * if (j.get("type").getAsString().equals("createLobby")) {
         * createLobby(conn, j);
         * }
         * 
         * if (j.get("type").getAsString().equals("leaveLobby")) {
         * leaveLobby(conn, j);
         * }
         * 
         * if (j.get("type").getAsString().equals("startGame")) {
         * String lobbyID = j.get("lobbyID").getAsString();
         * Lobby lobby = ctx.searchID(lobbyID);
         * lobby.startGame();
         * }
         * 
         * if (j.get("type").getAsString().equals("joinLobby")) {
         * 
         * String lobbyID = j.get("lobbyId").getAsString();
         * Lobby lobby = ctx.searchID(lobbyID);
         * 
         * if (j.get("password") != null) {
         * String password = j.get("password").getAsString();
         * if (!lobby.getPassword().equals(password)) {
         * return;
         * }
         * }
         * 
         * Player player = ctx.getPlayerByConn(conn);
         * lobby.addPlayer(player);
         * 
         * // broadcast lobby info to all clients of this lobby
         * 
         * for (Player p : lobby.getPlayers()) {
         * String data = "{\"type\": \"lobbyUpdate\", \"lobby\": " +
         * lobby.toJsonObjectPrivate().toString() + "}";
         * System.out.println(data);
         * p.getClient().getConn().send(data);
         * }
         * 
         * broadCastLobbyList();
         */

    }

    private void setUsername(WebSocket conn, JsonObject j) {
        String username = j.get("username").getAsString();
        // ctx.getPlayerByConn(conn).setNickname(username);
        if (ctx.isUsernameTaken(username)) {
            /* type: "usernameQuery", accepted: false */
            conn.send("{\"type\": \"usernameQuery\", \"accepted\": false}");
            return;
        }

        conn.send("{\"type\": \"usernameQuery\", \"accepted\": true}");
        ctx.getPlayerByConn(conn).setNickname(username);
        ctx.addMessage("System", "New player connected: " + username);
        broadcast(ctx.getMessageBoard());
    }

    private void leaveLobby(WebSocket conn, JsonObject j) {
        String lobbyID = j.get("lobbyID").getAsString();
        Lobby lobby = ctx.searchID(lobbyID);
        Player player = ctx.getPlayerByConn(conn);
        lobby.removePlayer(player);

        // broadcast lobby info to all clients of this lobby
        for (Player p : lobby.getPlayers()) {
            String data = "{\"type\": \"lobbyUpdate\", \"lobby\": " + lobby.toJsonObjectPrivate().toString() + "}";
            p.getClient().getConn().send(data);
        }

        broadCastLobbyList();
    }

    private void createLobby(WebSocket conn, JsonObject j) {
        String lobbyName = j.get("lobbyName").getAsString();
        String lobbyID = UUID.randomUUID().toString();
        String password = "";

        int playerCap = j.get("playerCount").getAsInt();

        if (j.get("password") != null) {
            password = j.get("password").getAsString();
        }

        String mode = j.get("lobbyMode").getAsString();
        System.out.println(1);
        String modeStr = mode.equals("timer") ? "Timer" : "Point";
        Mode lobbyMode = Mode.valueOf(modeStr);
        Player lobbyOwner = ctx.getPlayerByConn(conn);
        Lobby lobby = new Lobby(lobbyName, lobbyID, Status.WAITING, 0, lobbyMode, password, playerCap, lobbyOwner);

        ctx.addLobby(lobby, lobbyOwner);

        String data = "{\"type\": \"lobbyUpdate\", \"lobby\": " + lobby.toJsonObjectPrivate().toString() + "}";
        conn.send(data);
        broadCastLobbyList();

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer buff) {
        System.out.println("Message recv: bytebuff");

    }

    @Override
    public void onStart() {
        System.out.println("Started socket server on port " + this.addr.getPort() + " at " + this.addr.getAddress());

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New client connected " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        String newId = UUID.randomUUID().toString();
        Player newPlayer = new Player(newId, conn);

        ctx.addPlayer(newPlayer);
        System.out.println("Client count: " + ctx.getPlayerSize());
        // send selfID to client
        // conn.send("{\"type\": \"selfID\", \"id\": \"" + newId + "\"}");
        conn.send("[\"data\",{\"id\":1,\"data\":{\"id\":\"" + newId + "\"}}]");

        conn.send(ctx.getMessageBoard());
        conn.send(ctx.getLobbyList());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // TODO Auto-generated method stub
        System.out.println("Connection closed : " + conn.getResourceDescriptor());

        ctx.addMessage("System", "Player '" + ctx.getPlayerByConn(conn).getNickname() + "' has disconnected");
        // last message as json
        broadcast(ctx.getMessageBoard());

        ctx.removePlayer(conn);
        System.out.println("Client count: " + ctx.getPlayerSize());

        broadCastLobbyList();

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // TODO Auto-generated method stub

    }

}