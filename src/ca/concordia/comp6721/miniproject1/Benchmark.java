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
import java.util.*;
import java.util.concurrent.*;

/**
 * Evaluate the performance of the solvers using this benchmark
 */
public class Benchmark {

    /**
     * How long should we solve before timeout
     */
    private static int SECONDS_BEFORE_TIMEOUT = 5;
    private static int NUMBER_OF_PUZZLES = 10;

    /**
     * Driver of the program
     * @param args arguments
     */
    public static void main(String[] args) {
        System.out.println("COMP6721 Mini-project 1 Benchmark");

        // Delete previous benchmarks and traces
        FileUtil.deleteFiles();

        // Write header
        String header = "Puzzle Number,Puzzle,DFS-Duration,DFS-Moves,BFS-h1-Duration,BFS-h1-Moves,BFS-h2-Duration,BFS-h2-Moves," +
                "BFS-h3-Duration,BFS-h3-Moves,As-h1-Duration,As-h1-Moves,As-h2-Duration,As-h2-Moves,As-h3-Duration,As-h3-Moves";
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
        List<String> line = new ArrayList<>();

        // Add the puzzle number
        line.add(String.valueOf(counter));

        // Display the puzzle as a list of numbers, like 1-2-3-4-5-6-7-8-9-10-11-0
        line.add(Arrays.stream(puzzle.getPuzzle())
                .flatMapToInt(Arrays::stream).mapToObj(String::valueOf)
                .reduce((a, b) -> a.concat("-").concat(b))
                .get());

        Integer[] threadResults;

        // Solve using DFS
        Arrays.stream(Benchmark.executeThread(new PuzzleDFSSolverCallable(puzzle))).forEach(e -> line.add(String.valueOf(e)));

        // BFS with 3 heuristics
        Arrays.stream(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new BestFirstSolver(), new HammingDistanceHeuristic()))).forEach(e -> line.add(String.valueOf(e)));

        Arrays.stream(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new BestFirstSolver(), new ManhattanDistanceHeuristic()))).forEach(e -> line.add(String.valueOf(e)));

        Arrays.stream(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new BestFirstSolver(), new PermutationsHeuristic()))).forEach(e -> line.add(String.valueOf(e)));

        // A* with 3 heuristics
        Arrays.stream(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new AStarSolver(), new HammingDistanceHeuristic()))).forEach(e -> line.add(String.valueOf(e)));

        Arrays.stream(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new AStarSolver(), new ManhattanDistanceHeuristic()))).forEach(e -> line.add(String.valueOf(e)));

        Arrays.stream(Benchmark.executeThread(new PuzzleSolverCallable(puzzle, new AStarSolver(), new PermutationsHeuristic()))).forEach(e -> line.add(String.valueOf(e)));

        // Convert the list to a line to insert in the CSV
        StringBuilder stringBuilder  = new StringBuilder();
        Iterator<String> iterator = line.iterator();
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
    private static Integer[] executeThread(Callable<Integer[]> callable) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer[]> future = executor.submit(callable);
        try {
            return future.get(SECONDS_BEFORE_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException ignored) {
        } finally {
            future.cancel(true);
            executor.shutdownNow();
        }
        return new Integer[]{SECONDS_BEFORE_TIMEOUT * 1000, 0};
    }

    /**
     * Solve using DFS
     * @param puzzle Puzzle to solve
     */
    private static Integer[] solveDFS(Puzzle puzzle) {
        long startTime = System.nanoTime();

        Solver solver = new DepthFirstSolver();

        boolean solved = solver.solve(puzzle, null);

        long stopTime = System.nanoTime();

        long timeElapsed = stopTime - startTime;

        timeElapsed = TimeUnit.MILLISECONDS.convert(timeElapsed, TimeUnit.NANOSECONDS);

        int numberOfMoves = 0;
        if (solved) {
            numberOfMoves = FileUtil.countLines("./results/puzzleDFS.txt");
        }

        return new Integer[] {(int) timeElapsed, numberOfMoves};
    }

    /**
     * Generic solver for BFS or A*
     * @param puzzle Puzzle to solve
     * @param solver solver
     * @param heuristic heuristic
     */
    private static Integer[] solve(Puzzle puzzle, Solver solver, Heuristic heuristic) {
        long startTime = System.nanoTime();

        solver.solve(puzzle, heuristic);

        long stopTime = System.nanoTime();

        long timeElapsed = stopTime - startTime;

        timeElapsed = TimeUnit.MILLISECONDS.convert(timeElapsed, TimeUnit.NANOSECONDS);

        int numberOfMoves = FileUtil.countLines("./results/puzzle"+solver.toString() + "-" + heuristic.filename()+".txt");

        return new Integer[] {(int) timeElapsed, numberOfMoves};
    }

    /**
     * Wrapper for a Callable puzzle solving task
     */
    static class PuzzleSolverCallable implements Callable<Integer[]> {
        final Puzzle puzzle;
        final Solver solver;
        final Heuristic heuristic;

        PuzzleSolverCallable(Puzzle puzzle, Solver solver, Heuristic heuristic) {
            this.puzzle = puzzle;
            this.solver = solver;
            this.heuristic = heuristic;
        }

        @Override
        public Integer[] call() {
            return Benchmark.solve(puzzle, solver, heuristic);
        }
    }

    /**
     * Wrapper for a Callable puzzle DFS solving task
     */
    static class PuzzleDFSSolverCallable implements Callable<Integer[]> {
        final Puzzle puzzle;

        PuzzleDFSSolverCallable(Puzzle puzzle) {
            this.puzzle = puzzle;
        }

        @Override
        public Integer[] call() {
            return Benchmark.solveDFS(puzzle);
        }
    }
}
