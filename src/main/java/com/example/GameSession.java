package com.example;

import java.net.*;

public class GameSession {
    private final Board board = new Board(19);
    private final RulesEngine rules = new RulesEngine();
    private ClientHandler black;
    private ClientHandler white;
    private Stone currentTurn = Stone.BLACK;

    private int blackPrisoners = 0;
    private int whitePrisoners = 0;

    public void addPrisoner(Stone capturer) {
        if (capturer == Stone.BLACK) blackPrisoners++;
        else whitePrisoners++;
    }

    public GameSession(Socket p1, Socket p2) throws Exception {
        black = new ClientHandler(p1, Stone.BLACK, this);
        white = new ClientHandler(p2, Stone.WHITE, this);
    }




    //    zmiana 1
    public void start() {
        black.start();
        white.start();

        black.sendState(new GameState(
                board.toString(),
                "You are BLACK. Game started. BLACK begins.",
                true
        ));

        white.sendState(new GameState(
                board.toString(),
                "You are WHITE. Game started. BLACK begins.",
                false
        ));
    }

    public synchronized void handleMove(Move move, ClientHandler sender) {

        // jeśli nie tura gracza
        if (sender.getStone() != currentTurn) {
            sender.sendState(new GameState(
                    board.toString(),
                    "Not your turn",
                    false
            ));
            return;
        }

        // jeśli ruch niepoprawny
        boolean ok = rules.applyMove(board, move, currentTurn, this);
        if (!ok) {
            sender.sendState(new GameState(
                    board.toString(),
                    "Invalid move",
                    true
            ));
            return;
        }

        // ruch poprawny
        currentTurn = currentTurn.opposite();

        // gracz wykonujący ruch
        sender.sendState(new GameState(board.toString(), "Move accepted", false));

        // drugi gracz
        ClientHandler other = sender.getStone() == Stone.BLACK ? white : black;
        other.sendState(new GameState(board.toString(), "Your turn", true));
    }
}