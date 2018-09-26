package ca.concordia.comp6721.miniproject1;

import java.util.*;

public class Main {

    public static int ROW_SIZE = 3;
    public static int COL_SIZE = 4;
    public static int TABLE_SIZE = ROW_SIZE * COL_SIZE;

    public static void main(String[] args) {
        System.out.println("COMP6721 Mini-project 1");

        // Test data: 1 0 3 7 5 2 6 4 9 10 11 8

        int[][] initialTable = new int[ROW_SIZE][COL_SIZE];
        char[][] tableAlphabet = new char[ROW_SIZE][COL_SIZE];

        Scanner scanner = new Scanner(System.in);

        char alphabet = 'a';

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                try {
                    int number = scanner.nextInt();
                    initialTable[i][j] = number;
                    tableAlphabet[i][j] = alphabet;
                    alphabet++;
                } catch (InputMismatchException e) {
                    System.out.println("Please input numbers");
                    return;
                }
            }
        }

        tablePrettyPrint(initialTable);

        System.out.println();

        System.out.println(Arrays.deepToString(tableAlphabet));

    }

    /**
     * Print the table as a grid
     * Credits: https://stackoverflow.com/a/34846615
     * @param table Table we want to print
     */
    public static void tablePrettyPrint(int[][] table) {
        String lineSplit;
        StringJoiner splitJoiner = new StringJoiner("+", "|", "|");
        for (int index = 0; index < table[0].length; index++) {
            splitJoiner.add(String.format("%4s", "").replace(" ", "-"));
        }
        lineSplit = splitJoiner.toString();
        for (int[] row : table) {
            StringJoiner sj = new StringJoiner(" | ", "| ", " |");
            for (int col : row) {
                sj.add(String.format("%2d", col));
            }
            System.out.println(lineSplit);
            System.out.println(sj.toString());
        }
        System.out.println(lineSplit);
    }
}
