package com.example;

import java.net.*;

public class ServerMain {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started, waiting for players...");

            Socket p1 = serverSocket.accept();
            System.out.println("Player 1 connected");

            Socket p2 = serverSocket.accept();
            System.out.println("Player 2 connected");

            GameSession session = new GameSession(p1, p2);
            session.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}