package ca.concordia.comp6721.miniproject1.heuristics;

import ca.concordia.comp6721.miniproject1.Puzzle;

import java.util.Arrays;

/**
 * Sum of Permutations Inversions heuristic for the BestFirstSolver
 */
public class PermutationsHeuristic implements Heuristic {
    /**
     * Hamming distance: counts the number of tiles that are misplaced
     * @param puzzleInstance puzzle to evaluate
     */
    public void evaluate(Puzzle puzzleInstance) {

        int[][] puzzle = puzzleInstance.getPuzzle();

        int sumOfPermutations = 0;

        // Flatten the 2D array to a 1D array
        int[] flatPuzzle = Arrays.stream(puzzle)
                .flatMapToInt(Arrays::stream)
                .toArray();

        // Loop though the flatten puzzle
        for (int i = 0; i < flatPuzzle.length; i++) {
            int currentTile = flatPuzzle[i];

            // We only care about numbered tiles
            if (currentTile != 0) {
                // Create an array with the right tiles of the current tile
                int[] rightTiles = Arrays.copyOfRange(flatPuzzle, i + 1, flatPuzzle.length);

                // Count how many tiles on its right should be on its left in the goal state
                for (int rightTile : rightTiles) {
                    // If it's smaller than current tile (and != 0), it should be on the left: we increase the counter
                    if (rightTile < currentTile && rightTile != 0) {
                        sumOfPermutations++;
                    }
                }
            }
        }

        puzzleInstance.setHeuristic(sumOfPermutations);
    }

    /**
     * Name of the heuristic for the filename
     * @return h1
     */
    public String filename() {
        return "h3";
    }

    /**
     * Name of the heuristic
     * @return Hamming Distance
     */
    @Override
    public String toString() {
        return "Sum of Permutation Inversions";
    }
}
