package ca.concordia.comp6721.miniproject1;

import java.util.Arrays;

public class Puzzle {
    private int [][] puzzle;

    Puzzle parent;

    public Puzzle(int[][] puzzle) {
        this.puzzle = puzzle;
    }

    public int[][] getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(int[][] puzzle) {
        this.puzzle = puzzle;
    }

    public Puzzle getParent() {
        return parent;
    }

    public void setParent(Puzzle parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Puzzle puzzle1 = (Puzzle) o;
        return Arrays.deepEquals(puzzle, puzzle1.getPuzzle());
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(puzzle);
    }
}
