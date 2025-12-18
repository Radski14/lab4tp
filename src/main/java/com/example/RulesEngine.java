package com.example;

import java.util.*;

public class RulesEngine {

    public boolean applyMove(Board board, Move move, Stone stone, GameSession session) {
        int x = move.x;
        int y = move.y;

        if (!board.inBounds(x, y)) return false;
        if (board.get(x, y) != Stone.EMPTY) return false;

        board.set(x, y, stone);

        //sprawdzenia bicia
        for (int[] n : neighbors(x, y)) {
            int nx = n[0], ny = n[1];
            if (!board.inBounds(nx, ny)) continue;
            if (board.get(nx, ny) == stone.opposite() && !hasAnyLiberty(board, nx, ny)) {
                board.set(nx, ny, Stone.EMPTY);
                session.addPrisoner(stone);
            }
        }

        // samobójstwo nowego kamienia
        if (!hasAnyLiberty(board, x, y)) {
            board.set(x, y, Stone.EMPTY);
            return false;
        }

        return true;
    }
    //sprawdza czy są dostępne oddechy
    private boolean hasAnyLiberty(Board board, int x, int y) {
        for (int[] n : neighbors(x, y)) {
            int nx = n[0], ny = n[1];
            if (!board.inBounds(nx, ny)) continue;
            if (board.get(nx, ny) == Stone.EMPTY) return true;
        }
        return false;
    }
    //zwraca sąsiadów
    private List<int[]> neighbors(int x, int y) {
        List<int[]> list = new ArrayList<>();
        list.add(new int[]{x + 1, y});
        list.add(new int[]{x - 1, y});
        list.add(new int[]{x, y + 1});
        list.add(new int[]{x, y - 1});
        return list;
    }
}