package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.languages.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for Ngrams (Unigram, Bigram)
 */
abstract class AbstractNgram implements Ngram {

    // Map each language to its training file
    Map<Class<? extends Language>, String> trainingFiles;

    // Map each language to its hashmap
    HashMap<Class<? extends Language>, HashMap<Character, Integer>> langMap;

    // Setup the value of the delta smoothing
    static final double DELTA_SMOOTHING = 0.5;

    /**
     * Constructor, will be called from child classes
     */
    AbstractNgram() {
        // Setup the training files
        trainingFiles = new HashMap<>();
        trainingFiles.put(English.class, "trainEN.txt");
        trainingFiles.put(French.class, "trainFR.txt");
        trainingFiles.put(Spanish.class, "trainES.txt");
        // Uncomment the following line to enable italian detection
        //trainingFiles.put(Italian.class, "trainIT.txt");

        // Setup the langmap
        langMap = new HashMap<>();
        trainingFiles.forEach(((language, filename) -> langMap.put(language, new HashMap<>())));
    }
}
