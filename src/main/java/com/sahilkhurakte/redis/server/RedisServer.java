package com.sahilkhurakte.redis.server;

import com.sahilkhurakte.redis.protocol.RespParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {

    private final int port;
    public RedisServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        // ServerSocket = the LISTENING socket. Binding it reserves the port
        // and tells the OS "route incoming TCP connections here."
        // try-with-resources closes it automatically if start() ever exits.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on " + port);

            //The accept loop; runs forever , one iteration per one incoming client
            while (true) {
                // accept() BLOCKS here until a client connects.
                // When it returns, 'client' is a NEW connected socket —
                // a fresh, separate byte stream just for this one client.
                Socket client =  serverSocket.accept();

                System.out.println("Accepted connection from " + client.getInetAddress().getHostName());

                // Hand this client off to its own thread so the loop can
                // immediately go back to accept()-ing the NEXT client.
                // Without this, one slow/idle client would block everyone else.
                new Thread(() -> handleClient(client)).start();
            }
        }
    }

    private void handleClient(Socket client) {
        RespParser parser = new RespParser();
        try (client; OutputStream out = client.getOutputStream()) {
            InputStream in = client.getInputStream();

            String[] args;
            while ((args = parser.parseCommand(in)) != null) {
                System.out.println("received: " + String.join(" ", args));

                String command = args[0];
                if (command.equalsIgnoreCase("PING")) {
                    out.write("+PONG\r\n".getBytes());
                } else if (command.equalsIgnoreCase("ECHO")) {
                    String value = args[1];
                    out.write(("$" + value.length() + "\r\n" + value + "\r\n").getBytes());
                }
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("client error: " + e.getMessage());
        }
    }
}
