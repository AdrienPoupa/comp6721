package ca.concordia.comp6721.miniproject3;

import java.io.*;
import java.util.Objects;

/**
 * A Util class to handle formatting and writing the results in a text file
 */
public class FileUtil {
    /**
     * Append String in a file
     * @param filename name of the file
     * @param content content to add
     * @throws IOException if we cannot access the file
     */
    static void writeInFile(String filename, String content) throws IOException {
        try (FileWriter fw = new FileWriter("miniproject1/results/"+filename, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(content);
        }
    }

    /**
     * Delete files from the results folder
     * Keep the .gitkeep
     */
    static void deleteFiles() {
        File files = new File("miniproject1/results/");
        for (File file: Objects.requireNonNull(files.listFiles())) {
            if(!file.getName().equals(".gitkeep")) {
                file.delete();
            }
        }
    }

    /**
     * Delete a specific file from the results folder
     * Keep the .gitkeep
     * @param filename filename
     */
    static void deleteFileName(String filename) {
        File files = new File("miniproject1/results/");
        for (File file: Objects.requireNonNull(files.listFiles())) {
            if(file.getName().equals(filename)) {
                file.delete();
            }
        }
    }

    /**
     * Count number of lines in a file
     * Credits: https://stackoverflow.com/a/453067
     * @param filename name of the file
     * @return int number of lines
     */
    static int countLines(String filename) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
