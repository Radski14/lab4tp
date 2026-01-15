package com.example;

import java.net.*;

public class GameSession {
    private final Board board = new Board(19);
    private final RulesEngine rules = new RulesEngine();
    private ClientHandler black;
    private ClientHandler white;
    private Stone currentTurn = Stone.BLACK;
    private Board previousBoard = null; // To pole jest niezbędne dla reguły Ko

    // Zmienne stanu gry
    private int consecutivePasses = 0;
    private boolean gameOver = false;
    private boolean scoringPhase = false;

    // Zmienne do punktacji
    private int blackPrisoners = 0;
    private int whitePrisoners = 0;

    // Zgody na koniec fazy usuwania
    private boolean blackDone = false;
    private boolean whiteDone = false;

    public GameSession(Socket p1, Socket p2) throws Exception {
        black = new ClientHandler(p1, Stone.BLACK, this);
        white = new ClientHandler(p2, Stone.WHITE, this);
    }

    public void start() {
        black.start();
        white.start();
        broadcast("Game started. BLACK begins.", true);
    }

    public synchronized void handleMove(Move move, ClientHandler sender) {
        if (gameOver) return;

        // punktacja
        if (scoringPhase) {
            handleScoringMove(move, sender);
            return;
        }

        // zwyczajne ruchy
        if (sender.getStone() != currentTurn) {
            sender.sendState(new GameState(board.toString(), "Not your turn", false));
            return;
        }

        // poddanie się
        if (move.resign) {
            endGameByResignation(sender);
            return;
        }

        // pass
        if (move.pass) {
            consecutivePasses++;
            if (consecutivePasses >= 2) {
                startScoringPhase();
                return;
            }
            switchTurn(sender, "You passed", "Opponent passed. Your turn.");
            return;
        }


        consecutivePasses = 0;

        boolean ok = rules.applyMove(board, move, currentTurn, this);

        if (!ok) {
            sender.sendState(new GameState(board.toString(), "Invalid move", true));
            return;
        }

        switchTurn(sender, "Move accepted", "Your turn");
    }


    private void startScoringPhase() {
        scoringPhase = true;
        blackDone = false;
        whiteDone = false;

        String msg = "SCORING PHASE. Click DEAD stones to remove them.\nPress DONE when finished.";
        black.sendState(new GameState(board.toString(), msg, true));
        white.sendState(new GameState(board.toString(), msg, true));
    }

    private void handleScoringMove(Move move, ClientHandler sender) {
        // Obsługa przycisku DONE
        if (move.doneScoring) {
            if (sender.getStone() == Stone.BLACK) blackDone = true;
            else whiteDone = true;

            sender.sendState(new GameState(board.toString(), "Waiting for opponent...", false));

            if (blackDone && whiteDone) {
                finishGameAndScore();
            }
            return;
        }

        if (board.inBounds(move.x, move.y)) {
            Stone target = board.get(move.x, move.y);

            if (target != Stone.EMPTY) {
                board.set(move.x, move.y, Stone.EMPTY);

                if (target == Stone.BLACK) whitePrisoners++;
                else blackPrisoners++;

                // Reset zgody po zmianie stanu
                blackDone = false;
                whiteDone = false;

                String msg = "Stone removed. Keep marking or press DONE.";
                black.sendState(new GameState(board.toString(), msg, true));
                white.sendState(new GameState(board.toString(), msg, true));
            }
        }
    }

    private void finishGameAndScore() {
        gameOver = true;
        ScoringEngine engine = new ScoringEngine();
        ScoringResult result = engine.score(board, blackPrisoners, whitePrisoners, 6.5f);

        String msg = String.format("GAME OVER\nBLACK: %.1f | WHITE: %.1f\n%s wins!",
                result.blackScore,
                result.whiteScore,
                result.blackScore > result.whiteScore ? "BLACK" : "WHITE");

        black.sendState(new GameState(board.toString(), msg, false));
        white.sendState(new GameState(board.toString(), msg, false));
    }


    private void switchTurn(ClientHandler currentSender, String msgSelf, String msgOther) {
        currentTurn = currentTurn.opposite();
        ClientHandler other = (currentSender.getStone() == Stone.BLACK) ? white : black;

        currentSender.sendState(new GameState(board.toString(), msgSelf, false));
        other.sendState(new GameState(board.toString(), msgOther, true));
    }

    private void endGameByResignation(ClientHandler loser) {
        gameOver = true;
        ClientHandler winner = (loser.getStone() == Stone.BLACK) ? white : black;
        loser.sendState(new GameState(board.toString(), "You resigned. You lose.", false));
        winner.sendState(new GameState(board.toString(), "Opponent resigned. You win.", false));
    }

    private void broadcast(String msg, boolean blackTurn) {
        black.sendState(new GameState(board.toString(), msg, blackTurn));
        white.sendState(new GameState(board.toString(), msg, !blackTurn));
    }

    public void addPrisoner(Stone capturer) {
        if (capturer == Stone.BLACK) blackPrisoners++;
        else whitePrisoners++;
    }


    public Board getPreviousBoard() {
        return previousBoard;
    }

    public void setPreviousBoard(Board b) {
        previousBoard = b;
    }
}