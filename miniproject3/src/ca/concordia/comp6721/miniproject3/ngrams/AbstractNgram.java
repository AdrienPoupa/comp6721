package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.languages.English;
import ca.concordia.comp6721.miniproject3.languages.French;
import ca.concordia.comp6721.miniproject3.languages.Language;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNgram implements Ngram {
    Map<Class<? extends Language>, String> trainingFiles = new HashMap<>();

    AbstractNgram() {
        trainingFiles.put(English.class, "trainEN.txt");
        trainingFiles.put(French.class, "trainFR.txt");
    }
}
