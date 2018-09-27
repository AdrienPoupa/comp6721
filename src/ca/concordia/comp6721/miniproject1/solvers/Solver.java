package ca.concordia.comp6721.miniproject1.solvers;

import ca.concordia.comp6721.miniproject1.Puzzle;

/**
 * Interface implemented by all the solvers
 */
public interface Solver {
    boolean solve(Puzzle initialPuzzle);
}
