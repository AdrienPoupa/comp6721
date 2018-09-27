package ca.concordia.comp6721.miniproject1;

import ca.concordia.comp6721.miniproject1.solvers.DepthFirstSolver;
import ca.concordia.comp6721.miniproject1.solvers.Solver;
import ca.concordia.comp6721.miniproject1.solvers.bestfirstsolver.BestFirstSolverHammingDistance;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import static ca.concordia.comp6721.miniproject1.Puzzle.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("COMP6721 Mini-project 1");

        FileUtil.deleteFiles();

        // Test data: 1 0 3 7 5 2 6 4 9 10 11 8

        int[][] initialPuzzle = new int[ROW_SIZE][COL_SIZE];

        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                try {
                    int number = scanner.nextInt();
                    initialPuzzle[i][j] = number;
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
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                final int puzzleValue = initialPuzzle[i][j];
                expectedValues.removeIf(s -> s.equals(puzzleValue));
            }
        }

        // If the ArrayList is not empty, we have a problem
        if (!expectedValues.isEmpty()) {
            System.out.println("The numbers you entered are not valid");
            return;
        }

        Puzzle initialPuzzleInstance = new Puzzle(initialPuzzle);

        /*System.out.println("Puzzle that is going to be solved using DFS:");
        System.out.println(initialPuzzleInstance);*/

        Solver solver = new DepthFirstSolver();

        //solver.solve(initialPuzzleInstance);

        System.out.println("Puzzle that is going to be solved using BFS-h1 (Hamming Distance):");
        System.out.println(initialPuzzleInstance);

        solver = new BestFirstSolverHammingDistance();

        boolean solved = solver.solve(initialPuzzleInstance);

        if (solved) {
            System.out.println("The puzzle has been solved! Please have a look at the /results/puzzle-BFS-h1.txt file.");
        } else {
            System.out.println("The puzzle was not solvable");
        }
    }

}
