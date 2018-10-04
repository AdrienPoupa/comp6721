package ca.concordia.comp6721.miniproject1;

import java.io.IOException;
import java.util.*;

/**
 * Puzzle class
 */
public class Puzzle {

    /**
     * Static properties for the Puzzle: row size, column size and total size
     */
    public static int ROW_SIZE = 3;
    public static int COL_SIZE = 4;
    public static int PUZZLE_SIZE = ROW_SIZE * COL_SIZE;

    private int [][] puzzle;

    private Puzzle parent;

    private int heuristic;

    /**
     * Puzzle constructor
     * @param puzzle 2D puzzle array
     */
    Puzzle(int[][] puzzle) {
        this.puzzle = puzzle;
    }

    /**
     * Get the puzzle grid
     * @return puzzle int[][]
     */
    public int[][] getPuzzle() {
        return puzzle;
    }

    /**
     * Set the puzzle grid
     * @param puzzle int[][]
     */
    public void setPuzzle(int[][] puzzle) {
        this.puzzle = puzzle;
    }

    /**
     * Get the Puzzle's parent
     * @return Puzzle parent
     */
    public Puzzle getParent() {
        return parent;
    }

    /**
     * Set the Puzzle's parent
     * @param parent Puzzle parent
     */
    public void setParent(Puzzle parent) {
        this.parent = parent;
    }

    /**
     * Get the heuristic number
     * @return heuristic int heuristic number
     */
    public int getHeuristic() {
        return heuristic;
    }

    /**
     * Set the heuristic number
     * @param heuristic int heuristic number
     */
    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Compute f(n) using the number of parents (g(n), distance) and the heuristic (h(n))
     * @return int total cost
     */
    public int getTotalCost() {
        int totalCost = 0;

        // Get the distance from the root (g(n))
        int parentCounter = this.getNumberOfParents();

        // Add g(n)
        totalCost += parentCounter;

        // Add the estimate cost (h(n))
        totalCost += heuristic;

        // Return f(n)
        return totalCost;
    }

    /**
     * Compute the number of parents
     * @return int number of parents
     */
    public int getNumberOfParents() {

        // Get the distance from the root (g(n))
        int parentCounter = 0;
        Puzzle currentPuzzle = this;
        while (currentPuzzle.getParent() != null) {
            parentCounter++;
            currentPuzzle = currentPuzzle.getParent();
        }

        // Return f(n)
        return parentCounter;
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
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {

                globalCounter++;

                // If this is not the last iteration (has been handled before) and the value of the puzzle is not
                // equal to the counter: false
                if (globalCounter != PUZZLE_SIZE && puzzle[row][col] != globalCounter) {
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

        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                if (puzzle[row][col] == 0) {
                    zeroRow = row;
                    zeroCol = col;
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
     * Write the solution trace to the specified file
     * @param filename name of the file in which to write the solution
     */
    public void writeSolutionTrace(String filename) {

        String line;

        // We will store the trace in a stack for now
        Stack<String> trace = new Stack<>();

        // Loop though the puzzle tree structure from bottom to top
        Puzzle currentPuzzle = this;
        while (currentPuzzle.getParent() != null) {
            line = FileUtil.getLine(currentPuzzle.getPuzzle(), false);
            trace.push(line);
            currentPuzzle = currentPuzzle.getParent();
        }

        // Last Puzzle, the root Puzzle
        line = FileUtil.getLine(currentPuzzle.getPuzzle(), true);
        trace.push(line);

        // Now, unstack everything and write line by line
        while (!trace.isEmpty()) {
            line = trace.pop();
            try {
                FileUtil.writeInFile(filename, line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate a random puzzle
     * @return random Puzzle
     */
    public static Puzzle generateRandom() {
        // Values that have to be in the puzzle: 0 to PUZZLE_SIZE - 1 (so 0 to 11 for a 3 * 4 puzzle)
        // Create an ArrayList containing those numbers
        ArrayList<Integer> values = new ArrayList<>();
        for(int i = 0; i < PUZZLE_SIZE; i++) {
            values.add(i);
        }

        // Shuffle the ArrayList for randomness
        Collections.shuffle(values);

        // Create the 2D array puzzle
        int[][] puzzle = new int[ROW_SIZE][COL_SIZE];

        // Fill it with the values from above
        int globalCounter = 0;
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                int number = values.get(globalCounter);
                puzzle[row][col] = number;
                globalCounter++;
            }
        }

        // Return an instance with the newly generated Puzzle
        return new Puzzle(puzzle);
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
