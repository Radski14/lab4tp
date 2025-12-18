package com.example;

public enum Stone {
    BLACK, WHITE, EMPTY;

    public Stone opposite() {
        return this == BLACK ? WHITE : BLACK;
    }
}