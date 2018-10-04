package ca.concordia.comp6721.miniproject1;

import ca.concordia.comp6721.miniproject1.heuristics.HammingDistanceHeuristic;
import ca.concordia.comp6721.miniproject1.heuristics.Heuristic;
import ca.concordia.comp6721.miniproject1.heuristics.ManhattanDistanceHeuristic;
import ca.concordia.comp6721.miniproject1.heuristics.PermutationsHeuristic;
import ca.concordia.comp6721.miniproject1.solvers.AStarSolver;
import ca.concordia.comp6721.miniproject1.solvers.BestFirstSolver;
import ca.concordia.comp6721.miniproject1.solvers.DepthFirstSolver;
import ca.concordia.comp6721.miniproject1.solvers.Solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class Benchmark {

    /**
     * How long should we solve before timeout
     */
    private static int SECONDS_BEFORE_TIMEOUT = 2;
    private static int NUMBER_OF_PUZZLES = 100;

    /**
     * Driver of the program
     * @param args arguments
     */
    public static void main(String[] args) {
        System.out.println("COMP6721 Mini-project 1 Benchmark");

        // Delete previous benchmarks and traces
        FileUtil.deleteFiles();

        // Write header
        String header = "Puzzle Number,DFS,BFS-h1,BFS-h2,BFS-h3,As-h1,As-h2,As-h3";
        try {
            FileUtil.writeInFile("benchmark.csv", header);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < NUMBER_OF_PUZZLES + 1; i++) {
            System.out.println("Solving Puzzle "+i);
            Puzzle initialPuzzle = Puzzle.generateRandom();

            // Solve the puzzle
            solvePuzzle(initialPuzzle, i);
        }
    }

    private static void solvePuzzle(Puzzle puzzle, int counter) {
        List<Integer> executionTimes = new ArrayList<>();

        // Add the puzzle number
        executionTimes.add(counter);

        // Solve using DFS
        executionTimes.add(Benchmark.executeThread(new PuzzleDFSSolverCallable(puzzle)));

        // BFS with 3 heuristics
        executionTimes.add(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new BestFirstSolver(), new HammingDistanceHeuristic())));

        executionTimes.add(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new BestFirstSolver(), new ManhattanDistanceHeuristic())));

        executionTimes.add(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new BestFirstSolver(), new PermutationsHeuristic())));

        // A* with 3 heuristics
        executionTimes.add(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new AStarSolver(), new HammingDistanceHeuristic())));

        executionTimes.add(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new AStarSolver(), new ManhattanDistanceHeuristic())));

        executionTimes.add(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new AStarSolver(), new PermutationsHeuristic())));

        // Convert the list to a line to insert in the CSV
        StringBuilder stringBuilder  = new StringBuilder();
        Iterator<Integer> iterator = executionTimes.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()){
                stringBuilder.append(",");
            }
        }

        // Write line
        try {
            FileUtil.writeInFile("benchmark.csv", stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the solving thread
     */
    private static Integer executeThread(Callable<Integer> callable) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(callable);
        try {
            return future.get(SECONDS_BEFORE_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException ignored) {
        } finally {
            future.cancel(true);
            executor.shutdownNow();
        }
        return SECONDS_BEFORE_TIMEOUT * 1000;
    }

    /**
     * Solve using DFS
     * @param puzzle Puzzle to solve
     */
    private static int solveDFS(Puzzle puzzle) {
        long startTime = System.nanoTime();

        Solver solver = new DepthFirstSolver();

        solver.solve(puzzle, null);

        long stopTime = System.nanoTime();

        long timeElapsed = stopTime - startTime;

        timeElapsed = TimeUnit.MILLISECONDS.convert(timeElapsed, TimeUnit.NANOSECONDS);

        return (int) timeElapsed;
    }

    /**
     * Generic solver for BFS or A*
     * @param puzzle Puzzle to solve
     * @param solver solver
     * @param heuristic heuristic
     */
    private static int solve(Puzzle puzzle, Solver solver, Heuristic heuristic) {
        long startTime = System.nanoTime();

        solver.solve(puzzle, heuristic);

        long stopTime = System.nanoTime();

        long timeElapsed = stopTime - startTime;

        timeElapsed = TimeUnit.MILLISECONDS.convert(timeElapsed, TimeUnit.NANOSECONDS);

        return (int) timeElapsed;
    }

    /**
     * Wrapper for a Callable puzzle solving task
     */
    static class PuzzleSolverCallable implements Callable<Integer> {
        final Puzzle puzzle;
        final Solver solver;
        final Heuristic heuristic;

        PuzzleSolverCallable(Puzzle puzzle, Solver solver, Heuristic heuristic) {
            this.puzzle = puzzle;
            this.solver = solver;
            this.heuristic = heuristic;
        }

        @Override
        public Integer call() {
            return Benchmark.solve(puzzle, solver, heuristic);
        }
    }

    /**
     * Wrapper for a Callable puzzle DFS solving task
     */
    static class PuzzleDFSSolverCallable implements Callable<Integer> {
        final Puzzle puzzle;

        PuzzleDFSSolverCallable(Puzzle puzzle) {
            this.puzzle = puzzle;
        }

        @Override
        public Integer call() {
            return Benchmark.solveDFS(puzzle);
        }
    }
}
