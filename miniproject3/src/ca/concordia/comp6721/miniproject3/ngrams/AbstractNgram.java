package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.languages.English;
import ca.concordia.comp6721.miniproject3.languages.French;
import ca.concordia.comp6721.miniproject3.languages.Language;
import ca.concordia.comp6721.miniproject3.languages.Spanish;

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
        langMap = new HashMap<>();
        trainingFiles.forEach(((language, filename) -> langMap.put(language, new HashMap<>())));
    }
}
