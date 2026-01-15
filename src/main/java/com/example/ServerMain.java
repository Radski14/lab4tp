package com.example;

import java.net.*;

/**
 * Główna klasa serwera gry Go.
 * Odpowiada za uruchomienie gniazda serwerowego ({@link ServerSocket}),
 * oczekiwanie na połączenia od dwóch klientów i zainicjowanie sesji gry.
 */
public class ServerMain {

    /** Domyślny numer portu, na którym serwer nasłuchuje połączeń. */
    private static final int PORT = 12345;

    /**
     * Punkt wejścia aplikacji serwerowej.
     * <p>
     * Metoda wykonuje następujące kroki:
     * <ol>
     * <li>Otwiera gniazdo serwerowe na porcie 12345.</li>
     * <li>Wstrzymuje wykonanie (blokuje wątek) do momentu połączenia się pierwszego gracza.</li>
     * <li>Ponownie blokuje wątek do momentu połączenia się drugiego gracza.</li>
     * <li>Tworzy obiekt {@link GameSession}, który przejmuje dalszą obsługę logiki gry.</li>
     * </ol>
     * </p>
     *
     * @param args Argumenty linii komend (nieużywane).
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, waiting for players on port " + PORT + "...");

            Socket p1 = serverSocket.accept();
            System.out.println("Player 1 connected from: " + p1.getInetAddress());

            Socket p2 = serverSocket.accept();
            System.out.println("Player 2 connected from: " + p2.getInetAddress());

            GameSession session = new GameSession(p1, p2);
            session.start();

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}