package com.pixelly.chatapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.ConnectException;
import java.security.KeyStore;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient extends Application {
    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());

    private TextArea messageArea;
    private TextField inputField;
    private PrintWriter writer;
    private BufferedReader reader;
    SSLSocket socket;
    private boolean connected = false;

    @Override
    public void start(Stage primaryStage) {
        setupUI(primaryStage);
        // Prompt for username before establishing connection
        getUsername();
    }

    private void setupUI(Stage primaryStage) {
        primaryStage.setTitle("JavaFX SSL Chat Client");

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPrefHeight(400);

        inputField = new TextField();
        inputField.setPromptText("Enter your message...");
        inputField.setOnAction(e -> sendMessage());

        vBox.getChildren().addAll(messageArea, inputField);

        Scene scene = new Scene(vBox, 400, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void getUsername() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your username:");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            // Set the username and start the connection
            new Thread(() -> initializeSSLConnection(username)).start();
        });
    }

    private void initializeSSLConnection(String username) {
        try {
            SSLSocketFactory factory = createSSLSocketFactory();
            socket = (SSLSocket) factory.createSocket("localhost", 1234);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println(username);  // Send the username to the server
            connected = true;

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                String finalServerMessage = serverMessage;
                Platform.runLater(() -> messageArea.appendText("Server: " + finalServerMessage + "\n"));
            }
        } catch (ConnectException e) {
            logger.log(Level.SEVERE, "Connection to server failed: " + e.getMessage(), e);
            messageArea.appendText("Failed to connect to the server.\n"); // Notify user
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error establishing connection: " + e.getMessage(), e);
        } finally {
            closeConeection(); // Close resources in case of any exception
        }
    }

    private SSLSocketFactory createSSLSocketFactory() throws Exception {
        char[] trustStorePassword = "tshilidzi".toCharArray();
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream("client.truststore"), trustStorePassword);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    private void sendMessage() {
        if (!connected) {
            messageArea.appendText("Not connected to the server.\n");
            return;
        }

        String message = inputField.getText();
        if (!message.isEmpty()) {
            writer.println(message);
            Platform.runLater(() -> {
                messageArea.appendText("You: " + message + "\n");
                inputField.clear();
            });
        }
    }

    private void closeConeection() {
        try {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing connection: " + e.getMessage(), e);
        }
        connected = false;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
