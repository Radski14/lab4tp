package com.example;

import java.io.Serializable;

/**
 * Reprezentuje migawkę stanu gry przesyłaną z serwera do klienta.
 * Klasa implementuje interfejs {@link Serializable}, co umożliwia jej
 * przesyłanie przez strumienie obiektowe (ObjectStreams).
 */
public class GameState implements Serializable {

    /** * Tekstowa reprezentacja planszy.
     * Zawiera układ kamieni ('B', 'W', '.') ułożony w wiersze i kolumny.
     */
    public final String board;

    /** * Komunikat tekstowy dla gracza.
     * Wyświetlany bezpośrednio w interfejsie użytkownika jako status gry.
     */
    public final String message;

    /** * Flaga informująca, czy gracz odbierający ten obiekt ma prawo do wykonania ruchu.
     */
    public final boolean yourTurn;

    /**
     * Konstruuje nowy obiekt stanu gry.
     *
     * @param board Tekstowy opis układu planszy.
     * @param message Komunikat statusu dla klienta.
     * @param yourTurn Określa, czy jest tura gracza, do którego trafi ten stan.
     */
    public GameState(String board, String message, boolean yourTurn) {
        this.board = board;
        this.message = message;
        this.yourTurn = yourTurn;
    }
}