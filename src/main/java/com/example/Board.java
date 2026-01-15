package com.example;

/**
 * Reprezentuje planszę do gry.
 * Plansza przechowuje stan gry w postaci dwuwymiarowej tablicy obiektów {@link Stone}.
 */
public class Board {

    /**
     * Dwuwymiarowa tablica przechowywująca układ kamieni na planszy.
     * Pierwszy indeks to współrzędna X, drugi to współrzędna Y.
     */
    private final Stone[][] grid;

    /**
     * Rozmiar boku planszy.
     */
    private final int size;

    /**
     * Tworzy nową, pustą planszę o zadanym rozmiarze.
     * Wszystkie pola są inicjalizowane wartością {@link Stone#EMPTY}.
     *
     * @param size Długość boku planszy.
     */
    public Board(int size) {
        this.size = size;
        grid = new Stone[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                grid[i][j] = Stone.EMPTY;
    }

    /**
     * Sprawdza, czy podane współrzędne mieszczą się w granicach planszy.
     *
     * @param x Współrzędna pozioma.
     * @param y Współrzędna pionowa.
     * @return {@code true}, jeśli współrzędne (x, y) są poprawne (wewnątrz planszy),
     * w przeciwnym razie {@code false}.
     */
    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    /**
     * Pobiera kamień znajdujący się na podanych współrzędnych.
     * <p>
     * Uwaga: Metoda nie sprawdza granic tablicy. W przypadku podania
     * nieprawidłowych współrzędnych może zostać rzucony wyjątek {@link ArrayIndexOutOfBoundsException}.
     * Zaleca się wcześniejsze sprawdzenie metodą {@link #inBounds(int, int)}.
     * </p>
     *
     * @param x Współrzędna pozioma pola.
     * @param y Współrzędna pionowa pola.
     * @return Obiekt {@link Stone} znajdujący się na wskazanym polu.
     */
    public Stone get(int x, int y) {
        return grid[x][y];
    }

    /**
     * Ustawia podany kamień na wskazanych współrzędnych planszy.
     *
     * @param x Współrzędna pozioma pola.
     * @param y Współrzędna pionowa pola.
     * @param s Kamień ({@link Stone}), który ma zostać postawiony (np. BLACK, WHITE lub EMPTY).
     */
    public void set(int x, int y, Stone s) {
        grid[x][y] = s;
    }

    /**
     * Generuje tekstową (ASCII) reprezentację aktualnego stanu planszy.
     * @return Łańcuch znaków przedstawiający wizualizację planszy wiersz po wierszu.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                sb.append(grid[x][y] == Stone.BLACK ? 'B' :
                        grid[x][y] == Stone.WHITE ? 'W' : '.');
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Zwraca rozmiar planszy.
     *
     * @return Długość boku planszy.
     */
    public int getSize() {
        return size;
    }

    /**
     * Tworzy głęboką kopię bieżącej planszy.
     * Nowa plansza jest niezależnym obiektem z takim samym układem kamieni.
     *
     * @return Nowy obiekt {@code Board} będący kopią bieżącego.
     */
    public Board copy() {
        Board b = new Board(size);
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                b.grid[x][y] = this.grid[x][y];
        return b;
    }

    /**
     * Porównuje tę planszę z innym obiektem.
     * Dwie plansze są uznawane za równe, jeśli mają ten sam rozmiar
     * oraz identyczny układ kamieni na wszystkich polach.
     *
     * @param o Obiekt do porównania.
     * @return {@code true}, jeśli obiekty są logicznie równe, w przeciwnym razie {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Board)) return false;
        Board other = (Board) o;
        if (this.size != other.size) return false;

        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                if (this.grid[x][y] != other.grid[x][y])
                    return false;

        return true;
    }
}