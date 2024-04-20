package uta.group23.wurdle.socket;

import org.java_websocket.*;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Player;

import java.util.UUID;

import javax.swing.text.AbstractDocument.Content;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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

    public void broadCastMessageBoard() {
        String messages = ctx.getMessageBoard();
        broadcast(messages);
    }

    public void broadCastLobbyList() {
        String lobbies = ctx.getLobbyList();
        broadcast(lobbies);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JsonObject j = JsonParser.parseString(message).getAsJsonObject(); // convert to json
         if (j.has("type")) {
            String messageType = j.get("type").getAsString();
    
            if (messageType.equals("playerReadiedUp")) {
                // Extract player ID from the message
                String playerId = j.get("playerId").getAsString();
                // Broadcast this readiness status update to all clients
                broadcastPlayerReadiedUp(playerId);
            }
        }
        if (j.get("type").getAsString().equals("message")) {
            String msg = j.get("content").getAsString();

            // add to message board
            ctx.addMessage(ctx.getPlayerByConn(conn).getNickname(), msg);
            // broadcast messageBoard to all clients
            /**
             * messageBoard: [
             * {"username": "Player 1", "message": "Hello, is anyone here?"},]
             */

            broadCastMessageBoard();

        }

        broadCastLobbyList();

        if (j.get("type").getAsString().equals("setUsername")) {
            setUsername(conn, j);
        }

        if (j.get("type").getAsString().equals("createLobby")) {
            createLobby(conn, j);
        }

        if (j.get("type").getAsString().equals("leaveLobby")) {
            leaveLobby(conn, j);
        }

        if (j.get("type").getAsString().equals("startGame")) {
            String lobbyID = j.get("lobbyID").getAsString();
            Lobby lobby = ctx.searchID(lobbyID);
            lobby.startGame();
        }

        if (j.get("type").getAsString().equals("joinLobby")) {

            String lobbyID = j.get("lobbyId").getAsString();
            Lobby lobby = ctx.searchID(lobbyID);

            if (j.get("password") != null) {
                String password = j.get("password").getAsString();
                if (!lobby.getPassword().equals(password)) {
                    return;
                }
            }

            Player player = ctx.getPlayerByConn(conn);
            lobby.addPlayer(player);

            // broadcast lobby info to all clients of this lobby

            for (Player p : lobby.getPlayers()) {
                String data = "{\"type\": \"lobbyUpdate\", \"lobby\": " + lobby.toJsonObjectPrivate().toString() + "}";
                System.out.println(data);
                p.getClient().getConn().send(data);
            }

            broadCastLobbyList();
        }

    } 
      private void broadcastPlayerReadiedUp(String playerId){
        JsonObject msg= new JsonObject();
        msg.addProperty("type", "playerReadiedUp");
        msg.addProperty("playerId", playerId);
    
        broadcast(msg.toString());
        
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
        broadCastMessageBoard();
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
        conn.send("{\"type\": \"selfID\", \"id\": \"" + newId + "\"}");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // TODO Auto-generated method stub
        System.out.println("Connection closed : " + conn.getResourceDescriptor());
        
        ctx.addMessage("System", "Player '"+ ctx.getPlayerByConn(conn).getNickname() + "' has disconnected");
        broadCastMessageBoard();
        
        ctx.removePlayer(conn);
        System.out.println("Client count: " + ctx.getPlayerSize());

        
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // TODO Auto-generated method stub

    }

}
