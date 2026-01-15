package com.example;

import java.util.*;

/**
 * Silnik odpowiedzialny za obliczanie końcowego wyniku partii.
 * Wykorzystuje algorytm wypełniania do identyfikacji terytoriów
 * otoczonych przez poszczególne kolory kamieni.
 */
public class ScoringEngine {

    /**
     * Oblicza końcowy wynik gry na podstawie aktualnego stanu planszy.
     * Wynik uwzględnia terytorium (puste pola otoczone wyłącznie przez jeden kolor),
     * liczbę zbitych kamieni oraz komi.
     *
     *
     * @param board          Aktualny stan planszy {@link Board}.
     * @param blackPrisoners Liczba białych kamieni zbitych przez czarnego gracza.
     * @param whitePrisoners Liczba czarnych kamieni zbitych przez białego gracza.
     * @param komi           Punkty dodatkowe dla białego gracza (wyrównanie szans).
     * @return Obiekt {@link ScoringResult} zawierający końcową punktację obu graczy.
     */
    public ScoringResult score(Board board, int blackPrisoners, int whitePrisoners, float komi) {

        boolean[][] visited = new boolean[board.getSize()][board.getSize()];
        int blackTerritory = 0;
        int whiteTerritory = 0;

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                // Szukamy nieodwiedzonego jeszcze pustego pola, aby sprawdzić terytorium
                if (board.get(x, y) == Stone.EMPTY && !visited[x][y]) {
                    Territory t = floodTerritory(board, x, y, visited);

                    if (t.owner == Stone.BLACK)
                        blackTerritory += t.size;
                    else if (t.owner == Stone.WHITE)
                        whiteTerritory += t.size;
                }
            }
        }

        float blackTotal = blackTerritory + blackPrisoners;
        float whiteTotal = whiteTerritory + whitePrisoners + komi;

        return new ScoringResult(blackTotal, whiteTotal);
    }

    /**
     * Analizuje spójny obszar pustych pól, aby określić jego przynależność.
     * Obszar jest uznawany za terytorium danego koloru tylko wtedy, gdy styka się
     * wyłącznie z kamieniami tego samego koloru.
     *
     * @param board   Plansza do analizy.
     * @param sx      Współrzędna początkowa X.
     * @param sy      Współrzędna początkowa Y.
     * @param visited Tablica pól już odwiedzonych podczas bieżącej sesji punktowania.
     * @return Obiekt {@link Territory} zawierający kolor właściciela obszaru i jego rozmiar.
     */
    private Territory floodTerritory(Board board, int sx, int sy, boolean[][] visited) {
        Set<Stone> borderingColors = new HashSet<>();
        int count = 0;

        Queue<Point> q = new LinkedList<>();
        q.add(new Point(sx, sy));
        visited[sx][sy] = true;

        while (!q.isEmpty()) {
            Point p = q.poll();
            count++;

            for (int[] n : neighbors(p.x, p.y)) {
                int nx = n[0], ny = n[1];
                if (!board.inBounds(nx, ny)) continue;

                Stone s = board.get(nx, ny);
                if (s == Stone.EMPTY) {
                    if (!visited[nx][ny]) {
                        visited[nx][ny] = true;
                        q.add(new Point(nx, ny));
                    }
                } else {
                    // Jeśli pole nie jest puste, oznacza to kamień graniczny
                    borderingColors.add(s);
                }
            }
        }

        // Jeśli obszar graniczy z oboma kolorami lub nie graniczy z żadnym
        if (borderingColors.size() != 1) {
            return new Territory(Stone.EMPTY, 0);
        }

        // Zwracamy kolor jedynego sąsiada oraz liczbę pól
        return new Territory(borderingColors.iterator().next(), count);
    }

    /**
     * Pomocnicza klasa wewnętrzna reprezentująca wyliczony obszar terytorium.
     */
    private static class Territory {
        /** Właściciel terytorium. */
        Stone owner;
        /** Liczba pól. */
        int size;

        Territory(Stone owner, int size) {
            this.owner = owner;
            this.size = size;
        }
    }

    /**
     * Generuje listę współrzędnych sąsiadujących dla punktu.
     *
     * @param x Współrzędna X pola.
     * @param y Współrzędna Y pola.
     * @return Lista tablic dwuelementowych zawierających współrzędne sąsiadów.
     */
    private List<int[]> neighbors(int x, int y) {
        List<int[]> list = new ArrayList<>();
        list.add(new int[]{x + 1, y});
        list.add(new int[]{x - 1, y});
        list.add(new int[]{x, y + 1});
        list.add(new int[]{x, y - 1});
        return list;
    }
}