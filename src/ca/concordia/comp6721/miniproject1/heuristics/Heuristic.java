package ca.concordia.comp6721.miniproject1.heuristics;

import ca.concordia.comp6721.miniproject1.Puzzle;

/**
 * Heuristic interface
 */
public interface Heuristic {

    /**
     * Apply the heuristic
     * @param puzzle Puzzle on which to apply the heuristic
     */
    void evaluate(Puzzle puzzle);
}
