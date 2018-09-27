package ca.concordia.comp6721.miniproject1.solvers;

import ca.concordia.comp6721.miniproject1.FileUtil;
import ca.concordia.comp6721.miniproject1.Puzzle;

import java.io.IOException;
import java.util.Stack;

import static ca.concordia.comp6721.miniproject1.Puzzle.*;

/**
 * Depth First Solver
 */
public class DepthFirstSolver implements Solver {

    /**
     * Solve the Puzzle
     * @param initialPuzzle puzzle to solve
     * @return true if solved, false if not
     */
    public boolean solve(Puzzle initialPuzzle) {
        Stack<Puzzle> open = new Stack<>();
        Stack<Puzzle> close = new Stack<>();

        open.add(initialPuzzle);

        // Is it the first iteration? Useful for the first line of the trace
        boolean isFirst = true;

        // As long as we have grids to solve in the open stack
        while (!open.isEmpty()) {
            Puzzle currentPuzzleInstance = open.pop();

            int[][] currentPuzzle = currentPuzzleInstance.getPuzzle();

            // If puzzle is solved, return true
            if (currentPuzzleInstance.isSolved()) {
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
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(upPuzzle));

            // UP-RIGHT move
            int[][] upRightPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a UP-RIGHT move
            newZeroRow = zeroRow - 1;
            newZeroCol = zeroCol + 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(upRightPuzzle));

            // RIGHT move
            int[][] rightPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a RIGHT move
            newZeroRow = zeroRow;
            newZeroCol = zeroCol + 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(rightPuzzle));

            // DOWN-RIGHT move
            int[][] downRightPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a DOWN-RIGHT move
            newZeroRow = zeroRow + 1;
            newZeroCol = zeroCol + 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(downRightPuzzle));

            // DOWN move
            int[][] downPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a DOWN move
            newZeroRow = zeroRow + 1;
            newZeroCol = zeroCol;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(downPuzzle));

            // DOWN-LEFT move
            int[][] downLeftPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a DOWN-LEFT move
            newZeroRow = zeroRow + 1;
            newZeroCol = zeroCol - 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(downLeftPuzzle));

            // LEFT move
            int[][] leftPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a LEFT move
            newZeroRow = zeroRow;
            newZeroCol = zeroCol - 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(leftPuzzle));

            // UP-LEFT move
            int[][] upLeftPuzzle = clonePuzzle(currentPuzzle); // Copy currentPuzzle

            // Compute new 0 position for a UP-LEFT move
            newZeroRow = zeroRow - 1;
            newZeroCol = zeroCol - 1;

            // If the position is valid
            generateChild(children, zeroRow, zeroCol, newZeroRow, newZeroCol, new Puzzle(upLeftPuzzle));

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
            String line = FileUtil.getLine(currentPuzzle, isFirst);

            // This is not the first iteration anymore
            if (isFirst) {
                isFirst = false;
            }

            try {
                FileUtil.writeInFile("puzzleDFS", line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Generate the child
     * @param childrenStack
     * @param zeroRow
     * @param zeroCol
     * @param newZeroRow
     * @param newZeroCol
     * @param puzzle
     */
    private void generateChild(Stack<Puzzle> childrenStack, int zeroRow, int zeroCol, int newZeroRow, int newZeroCol, Puzzle puzzle) {
        int temp;
        if (puzzle.cellExists(newZeroRow, newZeroCol)) {
            // Swap the values
            int[][] puzzleTable = puzzle.getPuzzle();
            temp = puzzleTable[newZeroRow][newZeroCol];
            puzzleTable[newZeroRow][newZeroCol] = 0;
            puzzleTable[zeroRow][zeroCol] = temp;

            // Now that the puzzle is ready, push it to the potential children stack
            childrenStack.push(puzzle);
        }
    }
}
