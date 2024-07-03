// package com.pixelly.chatapp;

// import org.junit.jupiter.*;
// import org.junit.jupiter.api.Test;

// import static org.junit.jupiter.api.Assertions.*;

// import java.io.*;
// import java.net.*;
// import java.util.*;

// public class ChatServerTest {
//     @Test
//     void testServerInitialization() {
//         // Test to check if the server initializes correctly
//         assertDoesNotThrow(() -> {
//             ChatServer.main(new String[0]);
//         });
//     }

//     @Test
//     void testClientConnection() {
//         // Test to check if the server accepts client connections
//         assertDoesNotThrow(() -> {
//             Socket socket = new Socket("localhost", 1234);
//             socket.close();
//         });
//     }
// }
