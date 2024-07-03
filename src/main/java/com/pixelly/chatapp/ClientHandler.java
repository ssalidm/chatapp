package com.pixelly.chatapp;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.logging.*;

class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private SSLSocket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;

    public ClientHandler(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        setupStreams();
        readAndstoreUsername();
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

    private void readAndstoreUsername() {
        try {
            // Read the username from the client
            this.username = reader.readLine();
            String joinMessage = this.username + " has joined the chat.";
            logger.info(joinMessage);
            ChatServer.broadcast(joinMessage, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientMessage() {
        try {
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                String message = this.username + ": " + clientMessage;
                logger.info(message); // Log the received message
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
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close socket: " + e.getMessage(), e);
        }
        ChatServer.removeClient(this);
        String leaveMessage = this.username + " has left the chat.";
        logger.info(leaveMessage);
        ChatServer.broadcast(leaveMessage, this);
    }

    void sendMessage(String message) {
        writer.println(message);
    }
}
