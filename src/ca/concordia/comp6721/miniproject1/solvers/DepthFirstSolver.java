package ca.concordia.comp6721.miniproject1.solvers;

import ca.concordia.comp6721.miniproject1.FileUtil;
import ca.concordia.comp6721.miniproject1.Puzzle;

import java.util.Stack;

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

            // Get the actual puzzle from the Puzzle instance
            int[][] currentPuzzle = currentPuzzleInstance.getPuzzle();

            // If puzzle is solved, return true
            if (currentPuzzleInstance.isSolved()) {
                // Write current path in the puzzleDFS.txt file
                FileUtil.writeLine("puzzleDFS", currentPuzzle, isFirst);

                return true;
            }

            // We will move the 0 tile in 8 different positions, if possible.
            // Here, we get a stack with all the admissible children.
            Stack<Puzzle> children = currentPuzzleInstance.generateChildren();

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
            FileUtil.writeLine("puzzleDFS", currentPuzzle, isFirst);

            // This is not the first iteration anymore
            if (isFirst) {
                isFirst = false;
            }
        }

        return false;
    }
}
