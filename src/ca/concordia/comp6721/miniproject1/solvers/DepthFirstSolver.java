package ca.concordia.comp6721.miniproject1.solvers;

import ca.concordia.comp6721.miniproject1.Puzzle;
import ca.concordia.comp6721.miniproject1.heuristics.Heuristic;

import java.util.Stack;

/**
 * Depth First Solver
 */
public class DepthFirstSolver implements Solver {

    /**
     * Depth limit for the DFS solver
     * 10 by default, 0 for no limit
     */
    public static int DEPTH_LIMIT = 5;

    /**
     * Solve the Puzzle
     * @param initialPuzzle puzzle to solve
     * @param heuristic heuristic that will be used (null for DFS)
     * @return true if solved, false if not
     */
    public boolean solve(Puzzle initialPuzzle, Heuristic heuristic) {
        Stack<Puzzle> open = new Stack<>();
        Stack<Puzzle> close = new Stack<>();

        // Add the initial puzzle to the open stack
        open.add(initialPuzzle);

        // As long as we have grids to solve in the open stack
        while (!open.isEmpty()) {
            Puzzle currentPuzzle = open.pop();

            // If puzzle is solved, return true
            if (currentPuzzle.isSolved()) {
                // Write current path in the puzzleDFS.txt file
                currentPuzzle.writeSolutionTrace("puzzleDFS");

                // Puzzle is solved, return true
                return true;
            }

            // Add current puzzle to the close stack
            close.add(currentPuzzle);

            // Generate the children if we have not reached the cutoff yet or if it's disabled
            int numberOfParents = currentPuzzle.getNumberOfParents();
            if (numberOfParents < DEPTH_LIMIT || DEPTH_LIMIT == 0) {
                // We will move the 0 tile in 8 different positions, if possible.
                // Here, we get a stack with all the admissible children.
                Stack<Puzzle> children = currentPuzzle.generateChildren();

                // Discard existing children and insert the others in the open stack
                while (!children.isEmpty()) {
                    Puzzle child = children.pop();

                    // If the child is neither in open or close, push it to the open stack
                    // We'll push UP-LEFT moves to UP last (least preferred to most preferred, so the
                    // most preferred move will be on top of the open stack and tried first
                    if (!open.contains(child) && !close.contains(child)) {

                        // Set the current puzzle as parent
                        child.setParent(currentPuzzle);

                        // Push the child to the open stack
                        open.push(child);
                    }
                }
            }
        }

        // For some reason, the puzzle wasn't solved: return false
        return false;
    }
}
