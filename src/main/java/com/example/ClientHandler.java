package com.example;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final Stone stone;
    private final GameSession session;

    public ClientHandler(Socket socket, Stone stone, GameSession session) throws IOException {
        this.stone = stone;
        this.session = session;

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public Stone getStone() {
        return stone;
    }

    public void sendState(GameState state) {
        try {
            out.writeObject(state);
            out.flush();
        } catch (IOException e) {
            System.out.println("Client disconnected: " + stone);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Move move = (Move) in.readObject();
                session.handleMove(move, this);
            }
        } catch (Exception e) {
            System.out.println("Client disconnected: " + stone);
        }
    }
}