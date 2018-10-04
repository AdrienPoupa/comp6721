package ca.concordia.comp6721.miniproject1;

import ca.concordia.comp6721.miniproject1.heuristics.HammingDistanceHeuristic;
import ca.concordia.comp6721.miniproject1.heuristics.Heuristic;
import ca.concordia.comp6721.miniproject1.heuristics.ManhattanDistanceHeuristic;
import ca.concordia.comp6721.miniproject1.heuristics.PermutationsHeuristic;
import ca.concordia.comp6721.miniproject1.solvers.AStarSolver;
import ca.concordia.comp6721.miniproject1.solvers.BestFirstSolver;
import ca.concordia.comp6721.miniproject1.solvers.DepthFirstSolver;
import ca.concordia.comp6721.miniproject1.solvers.Solver;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.*;

import static ca.concordia.comp6721.miniproject1.Puzzle.*;

public class Main {

    /**
     * Driver of the program
     * @param args arguments
     */
    public static void main(String[] args) {
        System.out.println("COMP6721 Mini-project 1");

        FileUtil.deleteFiles();

        // Test data: 1 0 3 7 5 2 6 4 9 10 11 8

        int[][] initialPuzzle = new int[ROW_SIZE][COL_SIZE];

        Scanner scanner = new Scanner(System.in);

        // Enter puzzle data
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                try {
                    int number = scanner.nextInt();
                    initialPuzzle[row][col] = number;
                } catch (InputMismatchException e) {
                    System.out.println("Please input numbers");
                    return;
                }
            }
        }

        // Check if we have all the numbers we expect: 0 to PUZZLE_SIZE - 1 (so 0 to 11 for a 3 * 4 puzzle)
        // First, create an ArrayList containing those numbers
        ArrayList<Integer> expectedValues = new ArrayList<>();
        for(int i = 0; i < PUZZLE_SIZE; i++) {
            expectedValues.add(i);
        }

        // Remove each of the numbers contained in the puzzle from the ArrayList
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                final int puzzleValue = initialPuzzle[row][col];
                expectedValues.removeIf(s -> s.equals(puzzleValue));
            }
        }

        // If the ArrayList is not empty, we have a problem
        if (!expectedValues.isEmpty()) {
            System.out.println("The numbers you entered are not valid");
            return;
        }

        // Create a puzzle instance based on the initial puzzle
        Puzzle initialPuzzleInstance = new Puzzle(initialPuzzle);

        System.out.println("Puzzle to solve:");
        System.out.println(initialPuzzleInstance);

        System.out.println();

        // Solve using DFS
        Main.dfs(initialPuzzleInstance);

        // BFS with 3 heuristics
        Main.solve(initialPuzzleInstance, new BestFirstSolver(), new HammingDistanceHeuristic());

        Main.solve(initialPuzzleInstance, new BestFirstSolver(), new ManhattanDistanceHeuristic());

        Main.solve(initialPuzzleInstance, new BestFirstSolver(), new PermutationsHeuristic());

        // A* with 3 heuristics
        Main.solve(initialPuzzleInstance, new AStarSolver(), new HammingDistanceHeuristic());

        Main.solve(initialPuzzleInstance, new AStarSolver(), new ManhattanDistanceHeuristic());

        Main.solve(initialPuzzleInstance, new AStarSolver(), new PermutationsHeuristic());
    }

    /**
     * Solve using DFS
     * @param puzzle Puzzle to solve
     */
    private static void dfs(Puzzle puzzle) {
        System.out.println("Puzzle is going to be solved using DFS with a cutoff of "+DepthFirstSolver.DEPTH_LIMIT);

        long startTime = System.nanoTime();

        Solver solver = new DepthFirstSolver();

        boolean solved = solver.solve(puzzle, null);

        long stopTime = System.nanoTime();

        long timeElapsed = stopTime - startTime;

        timeElapsed = TimeUnit.MILLISECONDS.convert(timeElapsed, TimeUnit.NANOSECONDS);

        if (solved) {
            System.out.println("The puzzle has been solved! Please have a look at the /results/puzzle"+solver.toString()+".txt file.");
            System.out.println("Number of moves: "+FileUtil.countLines("./results/puzzle"+solver.toString()+".txt"));
        } else {
            System.out.println("The puzzle was not solvable with a cutoff of "+DepthFirstSolver.DEPTH_LIMIT);
        }

        System.out.println("Time elapsed: "+ timeElapsed +" ms");

        System.out.println();
    }

    /**
     * Generic solver for BFS or A*
     * @param puzzle Puzzle to solve
     * @param solver solver
     * @param heuristic heuristic
     */
    private static void solve(Puzzle puzzle, Solver solver, Heuristic heuristic) {
        String filename = solver.toString() + "-" + heuristic.filename();
        System.out.println("Puzzle is going to be solved using "+filename+" ("+heuristic.toString()+")");

        long startTime = System.nanoTime();

        boolean solved = solver.solve(puzzle, heuristic);

        long stopTime = System.nanoTime();

        long timeElapsed = stopTime - startTime;

        timeElapsed = TimeUnit.MILLISECONDS.convert(timeElapsed, TimeUnit.NANOSECONDS);

        if (solved) {
            System.out.println("The puzzle has been solved! Please have a look at the /results/puzzle"+filename+".txt file.");
            System.out.println("Number of moves: "+FileUtil.countLines("./results/puzzle"+filename+".txt"));
        } else {
            System.out.println("The puzzle was not solvable");
        }

        System.out.println("Time elapsed: "+ timeElapsed +" ms");

        System.out.println();
    }

}
