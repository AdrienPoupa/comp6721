package ca.concordia.comp6721.miniproject1;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Puzzle class
 */
public class Puzzle {

    public static int ROW_SIZE = 3;
    public static int COL_SIZE = 4;
    public static int PUZZLE_SIZE = ROW_SIZE * COL_SIZE;

    private int [][] puzzle;

    private Puzzle parent;

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

    /**
     * Check if a cell exists
     * @param row row to check
     * @param col col to check
     * @return true if it exists, false otherwise
     */
    public boolean cellExists(int row, int col)
    {
        try {
            int value = puzzle[row][col];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        return true;
    }

    /**
     * Check if the puzzle is solved
     * @return boolean true if puzzle is solved
     */
    public boolean isSolved() {

        // Special case: the last case must be equal to 0
        if (puzzle[ROW_SIZE - 1][COL_SIZE - 1] != 0) {
            return false;
        }

        // Global counter for the two for loops, goes from 1 to PUZZLE_SIZE (12 for a 3*4 puzzle)
        int globalCounter = 0;
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {

                globalCounter++;

                // If this is not the last iteration (has been handled before) and the value of the puzzle is not
                // equal to the counter: false
                if (globalCounter != PUZZLE_SIZE && puzzle[i][j] != globalCounter) {
                    return false;
                }
            }
        }

        // If we didn't exit so far, the puzzle is solved
        return true;
    }

    /**
     * Print the puzzle as a grid
     * Credits: https://stackoverflow.com/a/34846615
     * @return String
     */
    @Override
    public String toString() {
        String lineSplit;
        StringJoiner splitJoiner = new StringJoiner("+", "|", "|");
        for (int index = 0; index < puzzle[0].length; index++) {
            splitJoiner.add(String.format("%4s", "").replace(" ", "-"));
        }
        lineSplit = splitJoiner.toString();
        for (int[] row : puzzle) {
            StringJoiner sj = new StringJoiner(" | ", "| ", " |");
            for (int col : row) {
                sj.add(String.format("%2d", col));
            }
            System.out.println(lineSplit);
            System.out.println(sj.toString());
        }
        return lineSplit;
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

    /**
     * Clone a Puzzle
     * Inspired by http://www.java2s.com/Code/Java/Collections-Data-Structure/clonetwodimensionalarray.htm
     * @param a
     * @return
     */
    public static int[][] clonePuzzle(int[][] a) {
        int[][] b = new int[a.length][];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i].clone();
        }
        return b;
    }
}
