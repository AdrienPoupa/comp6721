package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.languages.*;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractNgram implements Ngram {
    Map<Class<? extends Language>, String> trainingFiles = new HashMap<>();
    HashMap<Class<? extends Language>, HashMap<Character, Integer>> langMap;

    static final double DELTA_SMOOTHING = 0.5;

    AbstractNgram() {
        trainingFiles.put(English.class, "trainEN.txt");
        trainingFiles.put(French.class, "trainFR.txt");
        trainingFiles.put(Spanish.class, "trainES.txt");
        // Uncomment the following line to enable italian detection
        //trainingFiles.put(Italian.class, "trainIT.txt");
        langMap = new HashMap<>();
        trainingFiles.forEach(((language, filename) -> langMap.put(language, new HashMap<>())));
    }
}
