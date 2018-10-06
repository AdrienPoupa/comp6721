package ca.concordia.comp6721.miniproject1.heuristics;

import ca.concordia.comp6721.miniproject1.Puzzle;

import static ca.concordia.comp6721.miniproject1.Puzzle.COL_SIZE;
import static ca.concordia.comp6721.miniproject1.Puzzle.ROW_SIZE;

/**
 * Manhattan distance heuristic for the BestFirstSolver
 */
public class ManhattanDistanceHeuristic implements Heuristic {
    /**
     * Manhattan distance: sum up all the distances by which tiles
     * are out of place
     * @param puzzleInstance puzzle to evaluate
     */
    public void evaluate(Puzzle puzzleInstance) {

        int[][] puzzle = puzzleInstance.getPuzzle();

        int manhattanDistance = 0;

        // Global counter for the two for loops, goes from 1 to PUZZLE_SIZE (12 for a 3*4 puzzle)
        int globalCounter = 1;
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {

                if (puzzle[row][col] != 0 && puzzle[row][col] != globalCounter) {
                    // In this cell, we should have the value globalCounter
                    // Let's find it in the puzzle
                    outerloop:
                    for (int goalRow = 0; goalRow < ROW_SIZE; goalRow++) {
                        for (int goalCol = 0; goalCol < COL_SIZE; goalCol++) {
                            // We have found where the current value should be (goalRow/goalCol)
                            if (puzzle[goalRow][goalCol] == globalCounter) {
                                // Apply the Manhattan distance formula
                                // Here we do row + 1 since row starts at 0 but we should have 1 there
                                manhattanDistance += Math.abs(row + 1 - goalRow);
                                manhattanDistance += Math.abs(col + 1 - goalCol);
                                break outerloop;
                            }
                        }
                    }
                }

                globalCounter++;
            }
        }

        puzzleInstance.setHeuristic(manhattanDistance);
    }

    /**
     * Name of the heuristic for the filename
     * @return h1
     */
    public String filename() {
        return "h2";
    }

    /**
     * Name of the heuristic
     * @return Hamming Distance
     */
    @Override
    public String toString() {
        return "Manhattan Distance";
    }
}
