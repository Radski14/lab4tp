package com.example;
/**
 * Przechowuje końcowe wyniki punktowe obu graczy po zakończeniu partii.
 * Obiekt tej klasy jest generowany przez {@link ScoringEngine} i zawiera
 * zsumowane punkty z terytoriów, jeńców oraz ewentualnego komi.
 */
public class ScoringResult {
    float blackScore;
    float whiteScore;

    /**
     * Konstruuje nowy obiekt wyniku z określonymi wartościami punktowymi.
     *
     * @param b Punkty uzyskane przez czarne kamienie.
     * @param w Punkty uzyskane przez białe kamienie.
     */
    ScoringResult(float b, float w) {
        blackScore = b;
        whiteScore = w;
    }
}