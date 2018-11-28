package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.Sentence;

import java.util.List;

/**
 * Ngram prediction: Unigram, Bigram
 */
public interface Ngram {

    /**
     * Train the model
     */
    void train();

    /**
     * Output the probabilities
     */
    void output();

    /**
     * Predict the language for the given sentences
     * @param sentences list of sentences for the command line output
     */
    void predict(List<Sentence> sentences);
}
