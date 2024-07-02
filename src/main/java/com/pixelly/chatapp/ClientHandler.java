package com.pixelly.chatapp;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.logging.*;

class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private SSLSocket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        setupStreams();
        handleClientMessage();
    }

    private void setupStreams() {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting up streams: " + e.getMessage(), e);
        }
    }

    private void handleClientMessage() {
        try {
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                String message = "Client " + this + ": " + clientMessage;
                ChatServer.broadcast(message, this);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client handler error: " + e.getMessage(), e);
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close socket: " + e.getMessage(), e);
        }
        ChatServer.removeClient(this);
    }

    void sendMessage(String message) {
        writer.println(message);
    }    
}