package com.example;

import java.io.Serializable;

public class GameState implements Serializable {
    public final String board;
    public final String message;
    public final boolean yourTurn;

    public GameState(String board, String message, boolean yourTurn) {
        this.board = board;
        this.message = message;
        this.yourTurn = yourTurn;
    }
}