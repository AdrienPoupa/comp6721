package ca.concordia.comp6721.miniproject1.heuristics;

import ca.concordia.comp6721.miniproject1.Puzzle;

import static ca.concordia.comp6721.miniproject1.Puzzle.*;

/**
 * Hamming distance evaluate for the BestFirstSolver
 */
public class HammingDistanceHeuristic implements Heuristic {
    /**
     * Hamming distance: counts the number of tiles that are misplaced
     * @param puzzleInstance puzzle to evaluate
     */
    public void evaluate(Puzzle puzzleInstance) {

        int[][] puzzle = puzzleInstance.getPuzzle();

        int numberOfTilesMisplaced = 0;

        // Special case: the last case must be equal to 0
        if (puzzle[ROW_SIZE - 1][COL_SIZE - 1] != 0) {
            numberOfTilesMisplaced++;
        }

        // Global counter for the two for loops, goes from 1 to PUZZLE_SIZE (12 for a 3*4 puzzle)
        int globalCounter = 0;
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {

                globalCounter++;

                // If this is not the last iteration (has been handled before) and the value of the puzzle is not
                // equal to the counter: false
                if (globalCounter != PUZZLE_SIZE && puzzle[i][j] != globalCounter && puzzle[i][j] != 0) {
                    numberOfTilesMisplaced++;
                }
            }
        }

        puzzleInstance.setHeuristic(numberOfTilesMisplaced);
    }

    /**
     * Name of the heuristic for the filename
     * @return h1
     */
    @Override
    public String toString() {
        return "h1";
    }
}
