package ca.concordia.comp6721.miniproject1.solvers;

import ca.concordia.comp6721.miniproject1.Puzzle;

/**
 * Interface implemented by all the solvers
 */
public interface Solver {

    /**
     * Solve the Puzzle
     * @param puzzle puzzle to solve
     * @return true if solved, false if not
     */
    boolean solve(Puzzle puzzle);

    default void heuristic(Puzzle puzzle) { }
}
