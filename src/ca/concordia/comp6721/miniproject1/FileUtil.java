package ca.concordia.comp6721.miniproject1;

import java.io.*;
import java.util.Objects;

import static ca.concordia.comp6721.miniproject1.Puzzle.COL_SIZE;
import static ca.concordia.comp6721.miniproject1.Puzzle.ROW_SIZE;

/**
 * A Util class to handle formatting and writing the results in a text file
 */
public class FileUtil {
    /**
     * Maps a number to a letter, ie A = 1, B = 2 etc
     * Credits: https://stackoverflow.com/a/10813256
     * @param i integer for which we want the char
     * @return
     */
    private static String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'A' - 1)) : null;
    }

    /**
     * Get the String line ready to be written in the file
     * @param puzzle puzzle to search in
     * @param isFirstLine Do we want to write the first line?
     * @return String to write, like: e [1, 2, 3, 4, 0, 5, 6, 7, 8, 9, 10, 11]
     */
    public static String getLine(int[][] puzzle, boolean isFirstLine) {
        StringBuilder line = new StringBuilder();

        // Add the move letter
        // If it's the first line, display 0 as the letter
        if (isFirstLine) {
            line.append("0");
        } else {
            // Else, the letter is the place of the 0
            // ie: if 0 lies in second position, we have a "b"
            int globalCounter = 1;
            for (int i = 0; i < ROW_SIZE; i++) {
                for (int j = 0; j < COL_SIZE; j++) {
                    if (puzzle[i][j] == 0) {
                        String alphabet = getCharForNumber(globalCounter);
                        if (alphabet != null) {
                            line.append(alphabet.toLowerCase());
                        }
                    }
                    globalCounter++;
                }
            }
        }

        // First bracket after the letter
        line.append(" [");

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                line.append(puzzle[i][j]);
                line.append(", ");
            }
        }

        // Delete the extra ", " at the end
        line.deleteCharAt(line.length() - 1);
        line.deleteCharAt(line.length() - 1);

        // Add the final bracket
        line.append("]");

        // Return the string
        return line.toString();
    }

    /**
     * Append String in a FileUtil
     * @param filename name of the file
     * @param content content to add
     * @throws IOException if we cannot access the file
     */
    public static void writeInFile(String filename, String content) throws IOException {
        try (FileWriter fw = new FileWriter("results/"+filename+".txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(content);
        }
    }

    /**
     * Delete files from the results folder
     * Keep the .gitkeep
     */
    public static void deleteFiles() {
        File files = new File("results/");
        for (File file: Objects.requireNonNull(files.listFiles())) {
            if(!file.getName().equals(".gitkeep")) {
                file.delete();
            }
        }
    }
}