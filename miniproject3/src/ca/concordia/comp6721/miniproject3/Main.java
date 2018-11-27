package ca.concordia.comp6721.miniproject3;

import ca.concordia.comp6721.miniproject3.ngrams.Bigram;
import ca.concordia.comp6721.miniproject3.ngrams.Ngram;
import ca.concordia.comp6721.miniproject3.ngrams.Unigram;

import java.util.ArrayList;
import java.util.List;

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
        Util.deleteFiles();

        // Init the list of sentences
        List<Sentence> sentences = new ArrayList<>();

        System.out.println("Running the Unigram model...");
        Ngram ngram = new Unigram();
        ngram.train();
        ngram.output();
        ngram.predict(sentences);

        System.out.println("Running the Bigram model...");
        ngram = new Bigram();
        ngram.train();
        ngram.output();
        ngram.predict(sentences);

        System.out.println("Output files generated");

        // Generate the result table
        CommandLineTable commandLineTable = new CommandLineTable();
        commandLineTable.setHeaders("#", "Sentence", "Language", "Unigram", "", "Bigram", "");

        int i = 0;
        int unigramCorrect = 0;
        int bigramCorrect = 0;
        for(Sentence sentence: sentences) {

            String unigramResult = "✕";
            if (sentence.getActualLanguage().toString().equals(sentence.getUnigramDetectedLanguage().toString())) {
                unigramResult = "✓";
                unigramCorrect++;
            }

            String bigramResult = "✕";
            if (sentence.getActualLanguage().toString().equals(sentence.getBigramDetectedLanguage().toString())) {
                bigramResult = "✓";
                bigramCorrect++;
            }

            commandLineTable.addRow(String.valueOf(i + 1), sentence.getSentence(), sentence.getActualLanguage().toString(),
                    sentence.getUnigramDetectedLanguage().toString(), unigramResult,
                    sentence.getBigramDetectedLanguage().toString(), bigramResult);

            i++;
        }

        commandLineTable.print();

        float unigramAccuracy = (float)unigramCorrect/i * 100;
        float bigramAccuracy = (float)bigramCorrect/i * 100;
        System.out.println("Unigram accuracy: " + unigramCorrect + "/" + i + " = " + unigramAccuracy + "%");
        System.out.println("Bigram accuracy: " + bigramCorrect + "/" + i + " = " + bigramAccuracy + "%");
    }
}
