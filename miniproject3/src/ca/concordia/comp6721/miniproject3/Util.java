package ca.concordia.comp6721.miniproject3;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;

/**
 * A Util class to handle formatting and writing the results in a text file
 */
public class Util {
    /**
     * Append String in a file
     * @param filename name of the file
     * @param content content to add
     * @throws IOException if we cannot access the file
     */
    public static void writeInFile(String filename, String content) throws IOException {
        try (FileWriter fw = new FileWriter("output/"+filename, true);
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
        File files = new File("output/");
        for (File file: Objects.requireNonNull(files.listFiles())) {
            if(!file.getName().equals(".gitkeep")) {
                file.delete();
            }
        }
    }

    /**
     * Read a file
     * Credits: https://stackoverflow.com/a/326440
     * @param path path of the file
     * @param encoding encoding of the file
     * @return String content of the file
     * @throws IOException exception thrown if file cannot be read
     */
    public static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /**
     * Read a file, return a list of string for each line
     * Credits: http://www.java2s.com/Tutorial/Java/0180__File/ReadLinesreadfiletolistofstrings.htm
     * @param file path of the file
     * @return list of strings
     * @throws Exception exception
     */
    public static List<String> readLines(File file) throws Exception {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> results = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            results.add(line);
            line = reader.readLine();
        }
        return results;
    }

    /**
     * Clean a string by removing everything that is not characters a-zA-Z then lowercase it
     * @param text text to clean
     * @return text cleaned
     */
    public static String cleanString(String text) {
        text = Util.stripAccents(text);
        return text.replaceAll("[^a-zA-Z]+", "")
                .toLowerCase();
    }

    /**
     * Remove accents from a string
     * Credits: https://stackoverflow.com/a/15190787
     * @param s String to clean
     * @return String cleaned
     */
    public static String stripAccents(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    /**
     * Split a string every n character
     * @param text string to split
     * @return list of strings
     */
    public static List<String> explodeString(String text) {
        List<String> parts = new ArrayList<>();
        char[] charTable = text.toCharArray();
        char previousCharacter = charTable[0];
        char [] subChar = Arrays.copyOfRange(charTable, 1, charTable.length);
        for (char character : subChar) {
            parts.add(previousCharacter+String.valueOf(character));
            previousCharacter = character;
        }
        return parts;
    }

    /**
     * Count the number of letters and store them in a map
     * Credits: https://www.quora.com/How-can-I-write-a-Java-program-to-find-each-occurrence-of-a-character-in-a-string
     * -which-is-given-as-an-input-from-a-console-without-using-any-built-in-functions
     * @param text text in which to count
     */
    public static void countAlphabet(String text, HashMap<Character, Integer> map) {
        for (char ch : text.toCharArray()) {
            if (map.containsKey(ch)) {
                int val = map.get(ch);
                map.put(ch, val + 1);
            } else {
                map.put(ch, 1);
            }
        }
    }
}
