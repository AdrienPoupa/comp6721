package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.Sentence;

import java.util.List;

public interface Ngram {
    void train();

    void output();

    void predict(List<Sentence> sentences);
}
