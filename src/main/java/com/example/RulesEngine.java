package com.example;

import java.util.*;

/**
 * Odpowiada za sprawdzanie poprawności ruchów
 * oraz egzekwowanie reguł gry Go.
 */
public class RulesEngine {

    /**
     * Próbuje wykonać ruch na planszy zgodnie z zasadami gry.
     *
     * @param board   Aktualna plansza.
     * @param move    Ruch wykonywany przez gracza.
     * @param stone   Kamień gracza wykonującego ruch.
     * @param session Sesja gry (obsługa reguły Ko i jeńców).
     * @return true, jeśli ruch jest poprawny, w przeciwnym razie false.
     */
    public boolean applyMove(Board board, Move move, Stone stone, GameSession session) {
        int x = move.x;
        int y = move.y;

        /**
         * Sprawdzenie, czy ruch mieści się w granicach planszy
         * oraz czy pole jest puste.
         */
        if (!board.inBounds(x, y)) return false;
        if (board.get(x, y) != Stone.EMPTY) return false;

        /**
         * Zapisanie stanu planszy przed ruchem
         * (potrzebne do cofnięcia ruchu i reguły Ko).
         */
        Board beforeMove = board.copy();

        /**
         * Postawienie kamienia na planszy.
         */
        board.set(x, y, stone);

        /**
         * Licznik zbitych kamieni przeciwnika.
         */
        int captured = 0;

        /**
         * Sprawdzenie i ewentualne zbicie łańcuchów przeciwnika.
         */
        for (int[] n : neighbors(x, y)) {
            int nx = n[0], ny = n[1];
            if (!board.inBounds(nx, ny)) continue;

            if (board.get(nx, ny) == stone.opposite()) {
                Set<Point> chain = collectChain(board, nx, ny);
                if (!hasLiberty(board, chain)) {
                    removeChain(board, chain);
                    captured += chain.size();
                }
            }
        }

        /**
         * Sprawdzenie samobójstwa kamienia
         * (dozwolone tylko, jeśli coś zostało zbite).
         */
        Set<Point> myChain = collectChain(board, x, y);
        if (!hasLiberty(board, myChain) && captured == 0) {
            restoreBoard(board, beforeMove);
            return false;
        }

        /**
         * Sprawdzenie reguły Ko.
         */
        Board prev = session.getPreviousBoard();
        if (prev != null && board.equals(prev)) {
            restoreBoard(board, beforeMove);
            return false;
        }

        /**
         * Zapamiętanie poprzedniego stanu planszy
         * do sprawdzania reguły Ko w następnym ruchu.
         */
        session.setPreviousBoard(beforeMove);

        /**
         * Dodanie jeńców do odpowiedniego gracza.
         */
        for (int i = 0; i < captured; i++) {
            session.addPrisoner(stone);
        }

        return true;
    }

    /**
     * Zbiera cały łańcuch połączonych kamieni tego samego koloru.
     *
     * @param board Plansza gry.
     * @param x     Współrzędna początkowa X.
     * @param y     Współrzędna początkowa Y.
     * @return Zbiór punktów należących do jednego łańcucha.
     */
    private Set<Point> collectChain(Board board, int x, int y) {
        Stone color = board.get(x, y);
        Set<Point> chain = new HashSet<>();
        Queue<Point> q = new LinkedList<>();

        Point start = new Point(x, y);
        chain.add(start);
        q.add(start);

        while (!q.isEmpty()) {
            Point p = q.poll();
            for (int[] n : neighbors(p.x, p.y)) {
                int nx = n[0], ny = n[1];
                Point np = new Point(nx, ny);

                if (board.inBounds(nx, ny)
                        && board.get(nx, ny) == color
                        && !chain.contains(np)) {
                    chain.add(np);
                    q.add(np);
                }
            }
        }
        return chain;
    }

    /**
     * Sprawdza, czy dany łańcuch ma przynajmniej jeden oddech.
     *
     * @param board Plansza gry.
     * @param chain Łańcuch kamieni.
     * @return true, jeśli łańcuch ma oddech.
     */
    private boolean hasLiberty(Board board, Set<Point> chain) {
        for (Point p : chain) {
            for (int[] n : neighbors(p.x, p.y)) {
                int nx = n[0], ny = n[1];
                if (board.inBounds(nx, ny)
                        && board.get(nx, ny) == Stone.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Usuwa wszystkie kamienie należące do danego łańcucha.
     *
     * @param board Plansza gry.
     * @param chain Łańcuch do usunięcia.
     */
    private void removeChain(Board board, Set<Point> chain) {
        for (Point p : chain) {
            board.set(p.x, p.y, Stone.EMPTY);
        }
    }

    /**
     * Zwraca listę sąsiadów danego pola
     * (góra, dół, lewo, prawo).
     *
     * @param x Współrzędna X.
     * @param y Współrzędna Y.
     * @return Lista sąsiednich pól.
     */
    private List<int[]> neighbors(int x, int y) {
        List<int[]> list = new ArrayList<>();
        list.add(new int[]{x + 1, y});
        list.add(new int[]{x - 1, y});
        list.add(new int[]{x, y + 1});
        list.add(new int[]{x, y - 1});
        return list;
    }

    /**
     * Przywraca planszę do wcześniejszego stanu.
     *
     * @param board    Plansza do przywrócenia.
     * @param snapshot Poprzedni zapis planszy.
     */
    private void restoreBoard(Board board, Board snapshot) {
        for (int x = 0; x < board.getSize(); x++)
            for (int y = 0; y < board.getSize(); y++)
                board.set(x, y, snapshot.get(x, y));
    }
}