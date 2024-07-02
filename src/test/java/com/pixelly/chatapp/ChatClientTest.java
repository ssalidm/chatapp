package com.pixelly.chatapp;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javafx.stage.Stage;

import java.io.*;
import javax.net.ssl.*;

public class ChatClientTest {
    @Test
    void testClientInitialization() {
        // Write a test to check if the client initializes correctly
        assertDoesNotThrow(() -> {
            new ChatClient().start(new Stage());
        });
    }

    @Test
    void testSendMessage() {
        // Write a test to check if the client can send a message
        assertDoesNotThrow(() -> {
            ChatClient client = new ChatClient();
            client.sendMessage("Hello, Server!");
        });
    }
}
