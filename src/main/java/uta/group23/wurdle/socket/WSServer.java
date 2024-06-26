package uta.group23.wurdle.socket;

import org.java_websocket.*;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import uta.group23.wurdle.Game;
import uta.group23.wurdle.models.Context;
import uta.group23.wurdle.models.Message;
import uta.group23.wurdle.models.Message;
import uta.group23.wurdle.models.Player;

import java.util.UUID;

import javax.swing.text.AbstractDocument.Content;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import com.google.gson.JsonArray;
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
            String version;
            // get version from env
            // if not set, default to "local build"

            version = System.getenv("VERSION") != null ? System.getenv("VERSION") : "local build";

            conn.send("[\"data\",{\"id\":11,\"data\":{\"id\":10,\"data\":\"" + version + "\"}}]");
            JsonObject data = j.get(1).getAsJsonObject();
            String username = data.get("data").getAsJsonObject().get("username").getAsString();

            // check if username is already taken
            if (ctx.isUsernameTaken(username)) {
                // send rejected
                // 11 is server->client message
                // id 8 is rejected
                // id 9 is accepted
                // pass username if accepted
                conn.send("[\"data\",{\"id\":11,\"data\":{\"id\":8}}]");
                return;
            }

            // set username
            ctx.getPlayerByConn(conn).setNickname(username);
            conn.send("[\"data\",{\"id\":11,\"data\":{\"id\":9,\"data\":{\"username\":\"" + username + "\"}}}]");

            // ctx.getPlayerByConn(conn).setNickname(username);

            ctx.addMessage("System", "New player connected: " + username);

            broadcast("[\"data\",{\"id\":30,\"data\":{\"id\":\"system\",\"msg\":\"" + username
                    + " has joined the game\"}}]");

        }

        if (j.get(0).getAsString().equals("leave")) {
            System.out.println("Leaving lobby");
            String id = ctx.getPlayerByConn(conn).getplayerId();
            ctx.leaveLobby(id);
            broadCastLobbyList();
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
            lobby.addPlayer(lobbyOwner);

            // send data back to creator
            String lobbyData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":1,\"data\":"
                    + lobby.toJsonObjectPrivate().toString()
                    + "}}]";

            System.out.println(lobbyData);
            conn.send(lobbyData);

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
                    /*
                     * [
                     * "data",
                     * {
                     * id: 12, // C->S
                     * data: {
                     * id: 1, // join lobby
                     * data: {
                     * id: id,
                     * },
                     * },
                     * },
                     * ]
                     * 
                     */

                    JsonObject subData = data.get("data").getAsJsonObject();
                    int subId = subData.get("id").getAsInt();

                    if (subId == 1) {
                        System.out.println("Joining lobby");
                        // join lobby
                        String lobbyID = subData.get("data").getAsJsonObject().get("id").getAsString();
                        Lobby lobby = ctx.searchID(lobbyID);
                        Player player = ctx.getPlayerByConn(conn);
                        lobby.addPlayer(player);

                        // broadcast lobby info to all clients of this lobby
                        for (Player p : lobby.getPlayers()) {
                            String lobbyData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":1,\"data\":"
                                    + lobby.toJsonObjectPrivate().toString() + "}}]";

                            System.out.println(lobbyData);
                            p.getConn().send(lobbyData);
                        }

                        broadCastLobbyList();
                    }

                    if (subId == 6) {
                        // highlight or unhighlight selected cell
                        // single coord pair
                        // ["data",{"id":12,"data":{"id":6,"data":{"coords":[7,6], highlight: true,
                        // lobbyID:}}}]

                        System.out.println("Highlighting cell");
                        ArrayList<int[]> selectedCells = new ArrayList<>();
                        // construct coords array
                        String coords = subData.get("data").getAsJsonObject().get("coords").toString();

                        System.out.println("Selected cells: " + coords);

                        // update grid and broadcast to all players
                        String lobbyID = subData.get("data").getAsJsonObject().get("lobbyID").getAsString();
                        Player player = ctx.getPlayerByConn(conn);

                        Lobby lobby = ctx.searchID(lobbyID);

                        // construct selected cells
                        JsonArray cellArray = JsonParser.parseString(coords).getAsJsonArray();
                        for (int i = 0; i < cellArray.size(); i++) {
                            System.out.println(cellArray.get(i));
                            JsonArray cell = cellArray.get(i).getAsJsonArray();
                            int[] cellCoords = { cell.get(0).getAsInt(), cell.get(1).getAsInt() };
                            selectedCells.add(cellCoords);
                            Boolean isHighlighted = subData.get("data").getAsJsonObject().get("highlighted")
                                    .getAsBoolean();
                            lobby.getGame().getGrid().highlightCell(cellCoords[0], cellCoords[1],
                                    isHighlighted);

                            System.out.println("Highlighting cell: " + cellCoords[0] + ", " + cellCoords[1]);

                            // set selectorID
                            lobby.getGame().getGrid().getCell(cellCoords[0], cellCoords[1])
                                    .setSelectorID(player.getplayerId());
                        }

                        // broadcast updated grid to all players
                        for (Player p : lobby.getPlayers()) {
                            String gridData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":3,\"data\":"
                                    + lobby.getGame().getGrid().gridDataJson() + "}}]";
                            p.getConn().send(gridData);
                        }
                    }

                    if (subId == 5) {
                        // receiving word data
                        // "coords": [[0,0],[0,1],[0,2] ...
                        /*
                         * 
                         * ["data",{"id":12,"data":{"id":5,"data":{"coords":[[7,6],[7,7],[7,8],[7,9],[7,
                         * 10],[7,11],[7,12],[7,13]],"lobbyID":"9d945fb8-9ac1-452d-8d3b-74658571dd89"}}}
                         * ]
                         */

                        ArrayList<int[]> selectedCells = new ArrayList<>();

                        // construct coords array
                        String coords = subData.get("data").getAsJsonObject().get("coords").toString();

                        String lobbyID = subData.get("data").getAsJsonObject().get("lobbyID").getAsString();
                        Player player = ctx.getPlayerByConn(conn);
                        Lobby lobby = ctx.searchID(lobbyID);

                        System.out.println("Selected cells: " + coords);

                        // construct selected cells
                        JsonArray cellArray = JsonParser.parseString(coords).getAsJsonArray();
                        for (int i = 0; i < cellArray.size(); i++) {
                            System.out.println(cellArray.get(i));
                            JsonArray cell = cellArray.get(i).getAsJsonArray();
                            int[] cellCoords = { cell.get(0).getAsInt(), cell.get(1).getAsInt() };
                            selectedCells.add(cellCoords);
                        }

                        // check word
                        lobby.getGame().checkWord(player, selectedCells);

                        // broadcast updated score to all players
                        for (Player p : lobby.getPlayers()) {
                            String scoreData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":5,\"data\":"
                                    + "{\"id\":\"" + player.getplayerId() + "\",\"score\":\""
                                    + player.getScore() + "\"}}}]";
                            p.getConn().send(scoreData);

                            String gridData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":3,\"data\":"
                                    + lobby.getGame().getGrid().gridDataJson() + "}}]";
                            p.getConn().send(gridData);

                            // use Id 4 for wordlist undiscovred and found
                            String wordListData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":4,\"data\":"
                                    + lobby.getGame().getWordListJson() + "}}]";

                            p.getConn().send(wordListData);
                        }

                    }

                    if (subId == 4) {
                        // ready
                        String lobbyID = subData.get("data").getAsJsonObject().get("id").getAsString();
                        Lobby lobby = ctx.searchID(lobbyID);
                        Player player = ctx.getPlayerByConn(conn);
                        String readyStatus = subData.get("data").getAsJsonObject().get("status").getAsString();
                        if (readyStatus.equals("READY")) {
                            lobby.setPlayerReady(player);
                        } else {
                            lobby.setPlayerUnready(player);
                        }

                        // broadcast lobby information
                        for (Player p : lobby.getPlayers()) {
                            String lobbyData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":1,\"data\":"
                                    + lobby.toJsonObjectPrivate().toString() + "}}]";

                            p.getConn().send(lobbyData);
                        }

                    }

                    if (subId == 2) {
                        // start game
                        String lobbyID = subData.get("data").getAsJsonObject().get("id").getAsString();
                        Lobby lobby = ctx.searchID(lobbyID);
                        try {
                            lobby.startGame();
                            Game game = lobby.getGame();

                            // broadcast lobby information
                            for (Player p : lobby.getPlayers()) {
                                String lobbyData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":1,\"data\":"
                                        + lobby.toJsonObjectPrivate().toString() + "}}]";

                                p.getConn().send(lobbyData);

                                String gameStartData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":7}}]";

                                p.getConn().send(gameStartData);

                                String gridData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":3,\"data\":"
                                        + game.getGrid().gridDataJson() + "}}]";

                                // use Id 4 for wordlist undiscovred and found
                                String wordListData = "[\"data\",{\"id\":11,\"data\":" + "{\"id\":4,\"data\":"
                                        + game.getWordListJson() + "}}]";

                                p.getConn().send(gridData);
                                p.getConn().send(wordListData);
                            }

                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                    }

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

        String version;
        // get version from env
        // if not set, default to "local build"

        version = System.getenv("VERSION") != null ? System.getenv("VERSION") : "local build";

        conn.send("[\"data\",{\"id\":11,\"data\":{\"id\":10,\"data\":\"" + version + "\"}}]");

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed : " + conn.getResourceDescriptor());

        ctx.addMessage("System", "Player '" + ctx.getPlayerByConn(conn).getNickname() + "' has disconnected");
        // last message as json
        broadcast(ctx.getMessageBoard());
        String id = ctx.getPlayerByConn(conn).getplayerId();

        ctx.removePlayer(id);
        System.out.println("Client count: " + ctx.getPlayerSize());

        broadCastLobbyList();

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // TODO Auto-generated method stub

    }

}
