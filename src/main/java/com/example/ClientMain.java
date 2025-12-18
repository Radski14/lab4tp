package com.example;


import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 12345);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Scanner sc = new Scanner(System.in);

        // Wątek odbierający stan gry od serwera
        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    GameState state = (GameState) in.readObject();

                    // Wyświetlamy aktualną planszę
                    System.out.println(state.board);

                    // Wyświetlamy komunikat
                    if (state.message != null && !state.message.isEmpty()) {
                        System.out.println(state.message);
                    }
                    if (state.yourTurn) {
                        System.out.print("Your move x y: ");
                    }
                }
            } catch (Exception e) {
                System.out.println("Disconnected from server.");
                System.exit(0);
            }
        });
        receiverThread.start();

        // Wątek wysyłający ruchy
        Thread senderThread = new Thread(() -> {
            try {
                while (true) {
                    int x = sc.nextInt();
                    int y = sc.nextInt();
                    out.writeObject(new Move(x, y));
                    out.flush();
                }
            } catch (Exception e) {
                System.out.println("Error sending move: " + e.getMessage());
            }
        });
        senderThread.start();
    }
}