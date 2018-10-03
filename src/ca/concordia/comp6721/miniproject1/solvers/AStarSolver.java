package ca.concordia.comp6721.miniproject1.solvers;

import ca.concordia.comp6721.miniproject1.FileUtil;
import ca.concordia.comp6721.miniproject1.Puzzle;
import ca.concordia.comp6721.miniproject1.heuristics.Heuristic;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Generic AStarSolver
 * Works using a heuristic that is defined in actual classes
 */
public class AStarSolver implements Solver {

    /**
     * Solve the Puzzle
     * @param initialPuzzle puzzle to solve
     * @param heuristic heuristic that will be used
     * @return true if solved, false if not
     */
    public boolean solve(Puzzle initialPuzzle, Heuristic heuristic) {
        // Create the open priority queue, sorted by the heuristic in ascending order
        PriorityQueue<Puzzle> open = new PriorityQueue<>(Comparator.comparingInt(Puzzle::getTotalCost));

        // The close queue does not need to be sorted, so it is a simple queue (LinkedList)
        LinkedList<Puzzle> close = new LinkedList<>();

        // Set the heuristic value initialPuzzle
        heuristic.evaluate(initialPuzzle);

        // Add the initial puzzle to the open priority queue
        open.add(initialPuzzle);

        // Is it the first iteration? Useful for the first line of the trace
        boolean isFirst = true;

        while(!open.isEmpty()) {

            // Get the head (highest value of the heuristic) of the queue while removing it from the PriorityQueue
            Puzzle currentPuzzleInstance = open.remove();

            // Get the actual puzzle from the Puzzle instance
            int[][] currentPuzzle = currentPuzzleInstance.getPuzzle();

            // If puzzle is solved, return true
            if (currentPuzzleInstance.isSolved()) {

                // Write current path in the puzzleAs-hX.txt file
                FileUtil.writeLine("puzzleAs-"+heuristic.toString(), currentPuzzle, isFirst);

                return true;
            }

            // Evaluate the puzzle: get the children
            // We will move the 0 tile in 8 different positions, if possible.
            // Here, we get a stack with all the admissible children.
            Stack<Puzzle> childrenReverse = currentPuzzleInstance.generateChildren();

            // This stack will be in correct order
            Stack<Puzzle> children = new Stack<>();

            // This stack contains the least preferred moves on top, so we want to reverse it to evaluate UP first
            while (!childrenReverse.isEmpty()) {
                Puzzle stackPuzzle = childrenReverse.pop();
                children.push(stackPuzzle);
            }

            // Add the current puzzle to the close queue
            close.add(currentPuzzleInstance);

            // Discard existing children and insert the others in the open stack
            while (!children.isEmpty()) {
                Puzzle child = children.pop();

                // If the child is neither in open or close, push it to the open stack
                // We'll push UP-LEFT moves to UP last (least preferred to most preferred, so the
                // most preferred move will be on top of the open stack and tried first
                if (!open.contains(child) && !close.contains(child)) {

                    // For those children, set the heuristic
                    heuristic.evaluate(child);

                    // Set the current puzzle as parent
                    child.setParent(currentPuzzleInstance);

                    // Finally add the puzzle to the open stack
                    open.add(child);
                }
            }

            // Write current path in the puzzleAs-hX.txt file
            FileUtil.writeLine("puzzleAs-"+heuristic.toString(), currentPuzzle, isFirst);

            // This is not the first iteration anymore
            if (isFirst) {
                isFirst = false;
            }
        }

        return false;
    }
}