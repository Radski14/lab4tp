package com.example;

import java.util.Objects;

/**
 * Reprezentuje pojedynczy punkt.
 * Klasa przechowuje współrzędne całkowitoliczbowe X oraz Y.
 * Dzięki nadpisaniu metod {@link #equals(Object)} oraz {@link #hashCode()},
 * obiekty tej klasy mogą być poprawnie używane jako klucze w mapach lub
 * elementy w zbiorach (np. {@link java.util.HashSet}).
 *
 */
public class Point {

    public int x;
    public int y;

    /**
     * Tworzy nowy punkt o określonych współrzędnych.
     *
     * @param x Współrzędna pozioma.
     * @param y Współrzędna pionowa.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Porównuje ten punkt z innym obiektem.
     * Punkty są uznawane za równe, jeśli obie ich współrzędne (x oraz y) są identyczne.
     *
     * @param o Obiekt do porównania.
     * @return {@code true}, jeśli podany obiekt jest punktem o tych samych współrzędnych.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }

    /**
     * Generuje kod skrótu (hash code) dla punktu na podstawie jego współrzędnych.
     * Spójność z metodą {@link #equals(Object)} zapewnia poprawne działanie w kolekcjach typu hash.
     *
     * @return Wartość hash wyliczona dla pary współrzędnych (x, y).
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}