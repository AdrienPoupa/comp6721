package ca.concordia.comp6721.miniproject1.heuristics;

import ca.concordia.comp6721.miniproject1.Puzzle;

import static ca.concordia.comp6721.miniproject1.Puzzle.COL_SIZE;
import static ca.concordia.comp6721.miniproject1.Puzzle.ROW_SIZE;

/**
 * Hamming distance evaluate for the BestFirstSolver
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

        int actualRowSize = ROW_SIZE - 1;
        int actualColSize = COL_SIZE - 1;

        // Global counter for the two for loops, goes from 1 to PUZZLE_SIZE (12 for a 3*4 puzzle)
        int globalCounter = 1;
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {

                // 0 has to be handled differently since we cannot do puzzle[row][col] != globalCounter
                // If current cell is 0 and we know that it is misplaced, calculate the Manhattan distance
                if (puzzle[row][col] == 0) {
                    // Here we do not do row + 1 since ROW_SIZE also needs a - 1... so no need to apply anything else
                    manhattanDistance += Math.abs(row - actualRowSize);
                    manhattanDistance += Math.abs(col - actualColSize);
                }

                // If this is not the last iteration (has been handled before) and the value of the puzzle is not
                // equal to the counter and not equal to 0: begin manhattan distance calculation
                if (puzzle[row][col] != globalCounter) {
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
