package ca.concordia.comp6721.miniproject3;

import ca.concordia.comp6721.miniproject3.ngrams.Ngram;
import ca.concordia.comp6721.miniproject3.ngrams.Unigram;

/**
 * The Main driver for the project
 */
public class Main {
    /**
     * Driver of the program
     * @param args arguments
     */
    public static void main(String[] args) {
        System.out.println("COMP6721 Mini-project 3");

        // Delete old files
        FileUtil.deleteFiles();

        Ngram ngram = new Unigram();
        ngram.train();
        ngram.output();
        ngram.predict();
    }
}
