package ca.concordia.comp6721.miniproject1;

import java.io.*;
import java.util.*;

public class Main {

    public static int ROW_SIZE = 3;
    public static int COL_SIZE = 4;
    public static int PUZZLE_SIZE = ROW_SIZE * COL_SIZE;

    public static void main(String[] args) {
        System.out.println("COMP6721 Mini-project 1");

        deleteFiles();

        // Test data: 1 0 3 7 5 2 6 4 9 10 11 8

        int[][] initialPuzzle = new int[ROW_SIZE][COL_SIZE];

        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                try {
                    int number = scanner.nextInt();
                    initialPuzzle[i][j] = number;
                } catch (InputMismatchException e) {
                    System.out.println("Please input numbers");
                    return;
                }
            }
        }

        // Check if we have all the numbers we expect: 0 to PUZZLE_SIZE - 1 (so 0 to 11 for a 3 * 4 puzzle)
        // First, create an ArrayList containing those numbers
        ArrayList<Integer> expectedValues = new ArrayList<>();
        for(int i = 0; i < PUZZLE_SIZE; i++) {
            expectedValues.add(i);
        }

        // Remove each of the numbers contained in the puzzle from the ArrayList
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                final int puzzleValue = initialPuzzle[i][j];
                expectedValues.removeIf(s -> s.equals(puzzleValue));
            }
        }

        // If the ArrayList is not empty, we have a problem
        if (!expectedValues.isEmpty()) {
            System.out.println("The numbers you entered are not valid");
            return;
        }

        puzzlePrettyPrint(initialPuzzle);

        Puzzle initialPuzzleInstance = new Puzzle(initialPuzzle);

        System.out.println();

        depthFirst(initialPuzzleInstance);
    }

    public static boolean depthFirst(Puzzle initialPuzzle) {
        Stack<Puzzle> open = new Stack<>();
        Stack<Puzzle> close = new Stack<>();

        open.add(initialPuzzle);

        // Write current path in file
        String line = getLine(initialPuzzle.getPuzzle(), true);

        try {
            writeInFile("puzzleDFS", line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!open.isEmpty()) {
            Puzzle currentPuzzleInstance = open.pop();

            int[][] currentPuzzle = currentPuzzleInstance.getPuzzle();

            // If puzzle is solved, return true
            if (puzzleSolved(currentPuzzle)) {
                return true;
            }

            // Generate children
            Stack<Puzzle> children = new Stack<>();

            // We will move the 0 tile in 8 different positions, if possible

            // First, retrieve 0's position
            int zeroRow = 0, zeroCol = 0, newZeroRow, newZeroCol;

            for (int i = 0; i < ROW_SIZE; i++) {
                for (int j = 0; j < COL_SIZE; j++) {
                    if (currentPuzzle[i][j] == 0) {
                        zeroRow = i;
                        zeroCol = j;
                    }
                }
            }

            // UP move
            int[][] upPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a UP move
            newZeroRow = zeroRow - 1;
            newZeroCol = zeroCol;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, upPuzzle);

            // UP-RIGHT move
            int[][] upRightPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a UP-RIGHT move
            newZeroRow = zeroRow - 1;
            newZeroCol = zeroCol + 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, upRightPuzzle);

            // RIGHT move
            int[][] rightPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a RIGHT move
            newZeroRow = zeroRow;
            newZeroCol = zeroCol + 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, rightPuzzle);

            // DOWN-RIGHT move
            int[][] downRightPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a DOWN-RIGHT move
            newZeroRow = zeroRow + 1;
            newZeroCol = zeroCol + 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, downRightPuzzle);

            // DOWN move
            int[][] downPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a DOWN move
            newZeroRow = zeroRow + 1;
            newZeroCol = zeroCol;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, downPuzzle);

            // DOWN-LEFT move
            int[][] downLeftPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a DOWN-LEFT move
            newZeroRow = zeroRow + 1;
            newZeroCol = zeroCol - 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, downLeftPuzzle);

            // LEFT move
            int[][] leftPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a LEFT move
            newZeroRow = zeroRow;
            newZeroCol = zeroCol - 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, leftPuzzle);

            // UP-LEFT move
            int[][] upLeftPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a UP-LEFT move
            newZeroRow = zeroRow - 1;
            newZeroCol = zeroCol - 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, upLeftPuzzle);

            // Add current puzzle to the close stack
            close.add(currentPuzzleInstance);

            // Discard existing children and insert the others in the open stack
            while (!children.isEmpty()) {
                Puzzle child = children.pop();

                // If the child is neither in open or close, push it to the open stack
                // We'll push UP-LEFT moves to UP last (least preferred to most preferred, so the
                // most preferred move will be on top of the open stack and tried first
                if (!open.contains(child) && !close.contains(child)) {
                    open.push(child);
                }
            }

            // Write current path in the puzzleDFS.txt file
            line = getLine(currentPuzzle, false);

            try {
                writeInFile("puzzleDFS", line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
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

    private static void generateChild(Stack<Puzzle> childrenStack, int zeroRow, int zeroCol, int newZeroRow, int newZeroCol, int[][] puzzle) {
        int temp;
        if (cellExists(newZeroRow, newZeroCol, puzzle)) {
            // Swap the values
            temp = puzzle[newZeroRow][newZeroCol];
            puzzle[newZeroRow][newZeroCol] = 0;
            puzzle[zeroRow][zeroCol] = temp;

            // Now that the puzzle is ready, push it to the potential children stack
            childrenStack.push(new Puzzle(puzzle));
        }
    }

    /**
     * Check if a cell exists
     * @param row row to check
     * @param col col to check
     * @param puzzle puzzle to look in for the row/col couple
     * @return true if it exists, false otherwise
     */
    public static boolean cellExists(int row, int col, int[][] puzzle)
    {
        try {
            int value = puzzle[row][col];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        return true;
    }

    /**
     * Print the puzzle as a grid
     * Credits: https://stackoverflow.com/a/34846615
     * @param puzzle Puzzle we want to print
     */
    public static void puzzlePrettyPrint(int[][] puzzle) {
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
        System.out.println(lineSplit);
    }

    /**
     * Maps a number to a letter, ie A = 1, B = 2 etc
     * Credits: https://stackoverflow.com/a/10813256
     * @param i integer for which we want the char
     * @return
     */
    private static String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'A' - 1)) : null;
    }

    /**
     * Get the String line ready to be written in the file
     * @param puzzle puzzle to search in
     * @param isFirstLine Do we want to write the first line?
     * @return String to write, like: e [1, 2, 3, 4, 0, 5, 6, 7, 8, 9, 10, 11]
     */
    public static String getLine(int[][] puzzle, boolean isFirstLine) {
        StringBuilder line = new StringBuilder();

        // Add the move letter
        // If it's the first line, display 0 as the letter
        if (isFirstLine) {
            line.append("0");
        } else {
            // Else, the letter is the place of the 0
            // ie: if 0 lies in second position, we have a "b"
            int globalCounter = 1;
            for (int i = 0; i < ROW_SIZE; i++) {
                for (int j = 0; j < COL_SIZE; j++) {
                    if (puzzle[i][j] == 0) {
                        String alphabet = getCharForNumber(globalCounter);
                        if (alphabet != null) {
                            line.append(alphabet.toLowerCase());
                        }
                    }
                    globalCounter++;
                }
            }
        }

        // First bracket after the letter
        line.append(" [");

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                line.append(puzzle[i][j]);
                line.append(", ");
            }
        }

        // Delete the extra ", " at the end
        line.deleteCharAt(line.length() - 1);
        line.deleteCharAt(line.length() - 1);

        // Add the final bracket
        line.append("]");

        // Return the string
        return line.toString();
    }

    /**
     * Append String in a File
     * @param filename name of the file
     * @param content content to add
     * @throws IOException if we cannot access the file
     */
    public static void writeInFile(String filename, String content) throws IOException {
        try (FileWriter fw = new FileWriter("results/"+filename+".txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(content);
        }
    }

    /**
     * Delete files from the results folder
     * Keep the .gitkeep
     */
    public static void deleteFiles() {
        File files = new File("results/");
        for (File file: Objects.requireNonNull(files.listFiles())) {
            if(!file.getName().equals(".gitkeep")) {
                file.delete();
            }
        }
    }

    /**
     * Check if the puzzle is solved
     * @param puzzle puzzle to check
     * @return
     */
    public static boolean puzzleSolved(int[][] puzzle) {

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
}
