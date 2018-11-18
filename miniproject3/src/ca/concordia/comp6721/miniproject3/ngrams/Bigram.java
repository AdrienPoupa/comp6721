package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.Util;
import ca.concordia.comp6721.miniproject3.languages.Language;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Bigram extends AbstractNgram {
    // Language, P(currentChar|previousChar) => HashMap<currentChar, HashMap<previousChar, frequence>>>
    private HashMap<Class<? extends Language>, List<String>> bigramsMap;
    private HashMap<Class<? extends Language>, HashMap<String, HashMap<String, Float>>> probabilityMap;
    private HashMap<Class<? extends Language>, HashMap<String, HashMap<String, Integer>>> bigramOccurencesMap;

    public Bigram() {
        super();
        bigramsMap = new HashMap<>();
        bigramOccurencesMap = new HashMap<>();
        probabilityMap = new HashMap<>();
        trainingFiles.forEach(((language, filename) -> {
            bigramOccurencesMap.put(language, new HashMap<>(new HashMap<>()));
            probabilityMap.put(language, new HashMap<>(new HashMap<>()));
        }));
    }

    @Override
    public void train() {
        trainingFiles.forEach(((language, filename) -> {
            try {
                String text = Util.cleanString(
                        Util.readFile(new File("input/" + filename).getPath(), Charset.defaultCharset()));

                List<String> bigrams = Util.explodeString(text);
                bigramsMap.put(language, bigrams);

                Util.countAlphabet(text, langMap.get(language));

                HashMap<String, HashMap<String, Integer>> bigramMap = bigramOccurencesMap.get(language);

                bigrams.forEach(bigram -> {
                    // Compute P(currentChar|previousChar)
                    // eg: for bigram "wh", P(h|w)
                    String previousChar = bigram.substring(0, 1); // w
                    String currentChar  = bigram.substring(1, 2); // h
                    if (bigramMap.containsKey(currentChar)) {
                        HashMap<String, Integer> previousCharMap = bigramMap.get(currentChar);
                        if (previousCharMap.containsKey(previousChar)) {
                            int val = previousCharMap.get(previousChar);
                            previousCharMap.put(previousChar, val + 1);
                        } else {
                            previousCharMap.put(previousChar, 1);
                        }
                    } else {
                        HashMap<String, Integer> previousCharMap = new HashMap<>();
                        previousCharMap.put(previousChar, 1);
                        bigramMap.put(currentChar, previousCharMap);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        // Calculate the probabilities
        HashMap<Class<? extends Language>, Set<String>> bigramsSet = new HashMap<>();
        trainingFiles.forEach(((language, filename) -> {
            bigramsSet.put(language, new HashSet<>(bigramsMap.get(language)));
        }));
        bigramOccurencesMap.forEach((language, bigramMap) ->
                bigramMap.forEach((currentChar, previousChars) ->
                        previousChars.forEach((previousChar, numberOfOccurrences) -> {

                            // Compute P(currentChar|previousChar)
                            if (bigramOccurencesMap.get(language).containsKey(currentChar)
                                && bigramOccurencesMap.get(language).get(currentChar).containsKey(previousChar)) {

                                int previousCharCurrentChar = bigramOccurencesMap.get(language)
                                                                                 .get(currentChar)
                                                                                 .get(previousChar);

                                int totalNumberOfBigrams = bigramsMap.get(language).size();

                                int numberOfDifferentBigrams = bigramsSet.get(language).size();

                                float probability = (float)(previousCharCurrentChar + DELTA_SMOOTHING) /
                                                    (float)(totalNumberOfBigrams + DELTA_SMOOTHING * numberOfDifferentBigrams);

                                if (probabilityMap.get(language).containsKey(currentChar)) {
                                    probabilityMap.get(language).get(currentChar).put(previousChar, probability);
                                } else {
                                    HashMap<String, Float> probabilitySubSubMap = new HashMap<>();
                                    probabilitySubSubMap.put(previousChar, probability);

                                    probabilityMap.get(language).put(currentChar, probabilitySubSubMap);
                                }
                            }
                        })));
    }

    @Override
    public void output() {
        probabilityMap.forEach((language, bigramMap) -> {
            StringBuilder stringBuilder = new StringBuilder();
            NumberFormat scientificNotation = new DecimalFormat("0.####E0");

            bigramMap.forEach((currentChar, previousChars) ->
                    previousChars.forEach((previousChar, probability) -> {
                        stringBuilder.append("(")
                                .append(currentChar)
                                .append("|")
                                .append(previousChar)
                                .append(") = ")
                                .append(scientificNotation.format(probability))
                                .append("\n");
                            }));
            String output = stringBuilder.toString();
            try {
                Language languageInstance = language.newInstance();
                Util.writeInFile("bigram" + languageInstance.getCode() + ".txt", output);
            } catch (IllegalAccessException | InstantiationException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void predict() {
        try {
            List<String> lines = Util.readLines(new File("input/sentences.txt"));

            HashMap<Class<? extends Language>, Double> scoreMap;

            int i = 1;
            StringBuilder output;
            for(String line: lines) {
                // Init probabilities at 0
                scoreMap = new HashMap<>();
                HashMap<Class<? extends Language>, Double> finalScoreMap1 = scoreMap;
                trainingFiles.forEach(((language, filename) -> finalScoreMap1.put(language, 0.0)));

                // Start creating the StringBuilder that will be written into the output file
                output = new StringBuilder();

                // Clean the sentence
                String cleanLine = Util.cleanString(line);

                List<String> bigrams = Util.explodeString(cleanLine);

                output.append("BIGRAM MODEL:").append("\n");
                StringBuilder finalOut = output;

                HashMap<Class<? extends Language>, Double> finalScoreMap2 = scoreMap;
                bigrams.forEach(bigram -> {
                    // Compute P(currentChar|previousChar)
                    // eg: for bigram "wh", P(h|w)
                    String previousChar = bigram.substring(0, 1); // w
                    String currentChar  = bigram.substring(1, 2); // h
                    finalOut.append("\n").append("BIGRAM: ").append(previousChar).append(currentChar).append("\n");
                    trainingFiles.forEach(((language, filename) -> {
                        HashMap<String, HashMap<String, Float>> probabilitySubMap = probabilityMap.get(language);
                        float probability = Float.MIN_VALUE;
                        if (probabilitySubMap.containsKey(currentChar)) {
                            HashMap<String, Float> probabilitySubSubMap = probabilitySubMap.get(currentChar);
                            if (probabilitySubSubMap.containsKey(previousChar)) {
                                probability = probabilitySubSubMap.get(previousChar);
                            }
                        }
                        double score =+ finalScoreMap2.get(language) + Math.log10(probability);
                        finalScoreMap2.put(language, score);

                        try {
                            finalOut.append(language.newInstance().toString().toUpperCase())
                                    .append(": P(").append(currentChar).append("|").append(previousChar).append(") = ").append(probability)
                                    .append(" ==> log prob of sentence so far: ").append(score).append("\n");
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }));
                });
                output.append("\n");

                Class<? extends Language> mostProbableLanguage = Collections.max(scoreMap.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
                output.append("According to the bigram model, the sentence is in ").append(mostProbableLanguage.newInstance().toString());

                Util.writeInFile("out" + i + ".txt", output.toString());

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
