package uta.group23.wurdle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class HttpServer extends ServerSocket {
    int port;

    public HttpServer(int port) throws IOException {
        super(port);
        this.port = port;

        try {
            this.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() throws IOException {
        super.close();
    }

    public void init() throws IOException {
        ServerSocket server = new ServerSocket(this.port);
        server.setReuseAddress(true);
        try {
            System.out.println("Server start on port " + this.port);

            Socket client = server.accept();

            System.out.println("New client connected " + client.getInetAddress().getHostAddress());
        } finally {
            server.close();
        }
    }

}
