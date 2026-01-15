package com.example;

import java.io.Serializable;

/**
 * Reprezentuje akcję wykonaną przez gracza i przesłaną do serwera.
 * Klasa implementuje {@link Serializable}, aby umożliwić przesyłanie danych przez sieć.
 * Obiekt ten może reprezentować trzy rodzaje akcji:
 * Postawienie kamienia (użycie pól x, y)
 * Akcję specjalną (pass, resign)
 * Potwierdzenie zakończenia fazy punktacji (doneScoring)
 *
 */
public class Move implements Serializable {

    public int x;
    public int y;

    public boolean pass;
    public boolean resign;
    public boolean doneScoring;

    /**
     * Konstruktor pomocniczy dla standardowych akcji gry.
     * Domyślnie ustawia {@code doneScoring} na {@code false}.
     *
     * @param x      Współrzędna X ruchu.
     * @param y      Współrzędna Y ruchu.
     * @param pass   {@code true}, jeśli gracz pasuje.
     * @param resign {@code true}, jeśli gracz rezygnuje.
     */
    public Move(int x, int y, boolean pass, boolean resign) {
        this(x, y, pass, resign, false);
    }

    /**
     * Pełny konstruktor obiektu ruchu, pozwalający na ustawienie wszystkich flag.
     *
     * @param x           Współrzędna X ruchu.
     * @param y           Współrzędna Y ruchu.
     * @param pass        {@code true}, jeśli gracz pasuje.
     * @param resign      {@code true}, jeśli gracz rezygnuje.
     * @param doneScoring {@code true}, jeśli gracz potwierdza koniec fazy punktacji.
     */
    public Move(int x, int y, boolean pass, boolean resign, boolean doneScoring) {
        this.x = x;
        this.y = y;
        this.pass = pass;
        this.resign = resign;
        this.doneScoring = doneScoring;
    }
}