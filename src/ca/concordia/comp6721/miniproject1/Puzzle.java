package ca.concordia.comp6721.miniproject1;

import java.util.Arrays;
import java.util.Stack;
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

    private int heuristic;

    Puzzle(int[][] puzzle) {
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

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Check if a cell exists
     * @param row row to check
     * @param col col to check
     * @return true if it exists, false otherwise
     */
    private boolean cellExists(int row, int col) {
        try {
            // Try to access the value
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
     * Generate the children of a Puzzle, in the following order:
     * UP > UP-RIGHT > RIGHT > DOWN-RIGHT > DOWN > DOWN-LEFT > LEFT > UP-LEFT
     * UP is pushed first, so it is in the bottom of the stack and will be processed last
     * But this results in it being pushed at the top of the open stack for DFS, which we want
     * @return Stack of Puzzles
     */
    public Stack<Puzzle> generateChildren() {
        // Generate children
        Stack<Puzzle> children = new Stack<>();

        // We will move the 0 tile in 8 different positions, if possible

        // First, retrieve 0's position
        int zeroRow = 0, zeroCol = 0, newZeroRow, newZeroCol;

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                if (puzzle[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                }
            }
        }

        // UP move
        int[][] upPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a UP move
        newZeroRow = zeroRow - 1;
        newZeroCol = zeroCol;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(upPuzzle));

        // UP-RIGHT move
        int[][] upRightPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a UP-RIGHT move
        newZeroRow = zeroRow - 1;
        newZeroCol = zeroCol + 1;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(upRightPuzzle));

        // RIGHT move
        int[][] rightPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a RIGHT move
        newZeroRow = zeroRow;
        newZeroCol = zeroCol + 1;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(rightPuzzle));

        // DOWN-RIGHT move
        int[][] downRightPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a DOWN-RIGHT move
        newZeroRow = zeroRow + 1;
        newZeroCol = zeroCol + 1;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(downRightPuzzle));

        // DOWN move
        int[][] downPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a DOWN move
        newZeroRow = zeroRow + 1;
        newZeroCol = zeroCol;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(downPuzzle));

        // DOWN-LEFT move
        int[][] downLeftPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a DOWN-LEFT move
        newZeroRow = zeroRow + 1;
        newZeroCol = zeroCol - 1;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(downLeftPuzzle));

        // LEFT move
        int[][] leftPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a LEFT move
        newZeroRow = zeroRow;
        newZeroCol = zeroCol - 1;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(leftPuzzle));

        // UP-LEFT move
        int[][] upLeftPuzzle = clonePuzzle(puzzle); // Copy puzzle

        // Compute new 0 position for a UP-LEFT move
        newZeroRow = zeroRow - 1;
        newZeroCol = zeroCol - 1;

        // If the position is valid
        generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(upLeftPuzzle));

        return children;
    }

    /**
     * Generate the child, add it to the children stack
     * @param children children stack
     * @param zeroRow row of the zero
     * @param zeroCol col of the zero
     * @param newZeroRow new row of the zero
     * @param newZeroCol new col of the zero
     * @param puzzle puzzle that we work on
     */
    private void generateChild(Stack<Puzzle> children, int zeroRow, int zeroCol, int newZeroRow, int newZeroCol, Puzzle puzzle) {
        int temp;
        if (puzzle.cellExists(newZeroRow, newZeroCol)) {
            // Swap the values
            int[][] puzzleTable = puzzle.getPuzzle();
            temp = puzzleTable[newZeroRow][newZeroCol];
            puzzleTable[newZeroRow][newZeroCol] = 0;
            puzzleTable[zeroRow][zeroCol] = temp;

            // Now that the puzzle is ready, push it to the potential children stack
            children.push(puzzle);
        }
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

    /**
     * Override equals based on the puzzle array, so Stack's contain works
     * @param o object to compare
     * @return object to compare
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Puzzle puzzle1 = (Puzzle) o;
        return Arrays.deepEquals(puzzle, puzzle1.getPuzzle());
    }

    /**
     * Override hashCode since we override equals
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(puzzle);
    }

    /**
     * Clone a Puzzle
     * Inspired by http://www.java2s.com/Code/Java/Collections-Data-Structure/clonetwodimensionalarray.htm
     * @param puzzle puzzle to clone
     * @return puzzle cloned
     */
    private static int[][] clonePuzzle(int[][] puzzle) {
        int[][] b = new int[puzzle.length][];

        for (int i = 0; i < puzzle.length; i++) {
            b[i] = puzzle[i].clone();
        }

        return b;
    }
}
