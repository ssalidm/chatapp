package com.pixelly.chatapp;

import java.io.*;
import java.security.KeyStore;
import java.util.*;
import java.util.logging.*;
import javax.net.ssl.*;

public class ChatServer {
    private static final int PORT = 1234;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    static final Logger logger = Logger.getLogger(ChatServer.class.getName());

    public static void main(String[] args) {
        setupLogger();
        setupServer();
    }

    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupServer() {
        try {
            SSLServerSocketFactory factory = createSSLServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(PORT)) {
                logger.info("SSL Chat Server is listening on port " + PORT);

                while (true) {
                    SSLSocket socket = (SSLSocket) serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Server exception: " + e.getMessage(), e);
        }
    }

    private static SSLServerSocketFactory createSSLServerSocketFactory() throws Exception {
        char[] keyStorePassword = "tshilidzi".toCharArray();
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("server.keystore"), keyStorePassword);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        return sslContext.getServerSocketFactory();
    }

    static void broadcast(String message, ClientHandler excludeUser) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (client != excludeUser) {
                    client.sendMessage(message);
                }
            }
        }
    }

    static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}
