package com.example;

import java.io.Serializable;

public class Move implements Serializable {
    public final int x;
    public final int y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }
}