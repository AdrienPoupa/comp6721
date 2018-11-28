package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.Sentence;
import ca.concordia.comp6721.miniproject3.Util;
import ca.concordia.comp6721.miniproject3.languages.Language;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Bigram prediction
 */
public class Bigram extends AbstractNgram {
    // Language, P(currentChar|previousChar) => HashMap<currentChar, HashMap<previousChar, frequence>>>
    
    // HashMap associating language => bigrams
    private HashMap<Class<? extends Language>, List<String>> bigramsMap;
    
    // HashMap associating language => currentChar => previousChar => probability
    private HashMap<Class<? extends Language>, HashMap<String, HashMap<String, Float>>> probabilityMap;

    // HashMap associating language => currentChar => previousChar => number of occurrences
    private HashMap<Class<? extends Language>, HashMap<String, HashMap<String, Integer>>> bigramOccurrencesMap;

    /**
     * Bigram constructor
     */
    public Bigram() {
        // Call to parent (abstract) constructor
        super();
        
        // Setup the maps
        bigramsMap = new HashMap<>();
        
        bigramOccurrencesMap = new HashMap<>();
        
        probabilityMap = new HashMap<>();
        
        trainingFiles.forEach(((language, filename) -> {
            bigramOccurrencesMap.put(language, new HashMap<>(new HashMap<>()));
            probabilityMap.put(language, new HashMap<>(new HashMap<>()));
        }));
    }

    /**
     * Train the bigram model
     */
    @Override
    public void train() {
        // For each language that is supported
        trainingFiles.forEach(((language, filename) -> {
            try {
                // Clean the string
                String text = Util.cleanString(
                        Util.readFile(new File("input/" + filename).getPath(), Charset.defaultCharset()));

                // Get the bigrams present in the training file, put them in the bigramsMap
                List<String> bigrams = Util.explodeString(text);
                bigramsMap.put(language, bigrams);

                // Get the submap of bigramOccurrencesMap
                // It will be used to add the number of occurrences for each bigram
                HashMap<String, HashMap<String, Integer>> bigramMap = bigramOccurrencesMap.get(language);

                // For each bigram present in the training file
                bigrams.forEach(bigram -> {
                    // Given a bigram, compute the number of its occurrences
                    // eg: for bigram "wh"
                    String previousChar = bigram.substring(0, 1); // w
                    String currentChar  = bigram.substring(1, 2); // h

                    // The bigramMap already has occurrences of "h"
                    if (bigramMap.containsKey(currentChar)) {
                        // Get the "h" submap
                        HashMap<String, Integer> previousCharMap = bigramMap.get(currentChar);

                        // The h submap already has occurrences of "w"
                        if (previousCharMap.containsKey(previousChar)) {
                            // Increment the number of existing occurrences of bigram "wh"
                            int val = previousCharMap.get(previousChar);
                            previousCharMap.put(previousChar, val + 1);
                        } else {
                            // Create the entry "w" so we create the "wh" bigram
                            previousCharMap.put(previousChar, 1);
                        }
                    } else {
                        // This is the first time we see a "h" in second position (eg "Xh" where "X" is any letter)
                        // We create a new submap for "h" and another one for "w", start the counter at 1
                        // It can be increased later when the bigram is encountered again
                        HashMap<String, Integer> previousCharMap = new HashMap<>();
                        previousCharMap.put(previousChar, 1);
                        bigramMap.put(currentChar, previousCharMap);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        // Setup the Bigram set
        // Here we use a set because we want to have all the UNIQUE bigrams
        // So we convert our bigram map into a set that will contain all the bigrams of the language only once
        HashMap<Class<? extends Language>, Set<String>> bigramsSet = new HashMap<>();
        trainingFiles.forEach(((language, filename) -> bigramsSet.put(language, new HashSet<>(bigramsMap.get(language)))));

        // Calculate the probabilities
        // Compute P(currentChar|previousChar)
        // eg: for bigram "wh", P(h|w)
        bigramOccurrencesMap.forEach((language, bigramMap) ->
                bigramMap.forEach((currentChar, previousChars) ->
                        previousChars.forEach((previousChar, numberOfOccurrences) -> {

                            // Compute P(currentChar|previousChar)
                            if (bigramOccurrencesMap.get(language).containsKey(currentChar)
                                && bigramOccurrencesMap.get(language).get(currentChar).containsKey(previousChar)) {

                                // Get the number of occurences of the bigram "wh"
                                int previousCharCurrentChar = bigramOccurrencesMap.get(language)
                                                                                 .get(currentChar)
                                                                                 .get(previousChar);

                                // Get the total number of bigrams for the language
                                int totalNumberOfBigrams = bigramsMap.get(language).size();

                                // Get the number of different bigrams for the language
                                int numberOfDifferentBigrams = bigramsSet.get(language).size();

                                // Actually calculate the probability
                                float probability = (float)(previousCharCurrentChar + DELTA_SMOOTHING) /
                                                    (float)(totalNumberOfBigrams + DELTA_SMOOTHING * numberOfDifferentBigrams);

                                // Update the probability map with the probability
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

    /**
     * Output the probabilities for the unigrams
     */
    @Override
    public void output() {
        // For each probability that we computed
        probabilityMap.forEach((language, bigramMap) -> {
            StringBuilder stringBuilder = new StringBuilder();

            // We want the scientific notation
            NumberFormat scientificNotation = new DecimalFormat("0.####E0");

            // Create a new line like (a|a) = 1.2786E-4
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

            // Write the probability to the file
            try {
                Language languageInstance = language.newInstance();
                Util.writeInFile("bigram" + languageInstance.getCode() + ".txt", output);
            } catch (IllegalAccessException | InstantiationException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Predict the language for the given sentences
     * @param sentences list of sentences for the command line output
     */
    @Override
    public void predict(List<Sentence> sentences) {
        try {
            List<String> lines = Util.readLines(new File("input/sentences.txt"));

            HashMap<Class<? extends Language>, Double> scoreMap;

            // For each sentence
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

                // Get the sentence from the ArrayList
                Sentence sentence = sentences.get(i - 1);

                // Get the bigrams that are in the sentence
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

                        // Get the probability for the current bigram
                        // We init the probability at the minimum value, because if a bigram is not in the map,
                        // there is a high probability that it is not in the current language
                        // Thus, setting a low probability value for this bigram will decrease the final score
                        float probability = Float.MIN_VALUE;
                        // The current char ("h" in bigram "wh") is in the map
                        if (probabilitySubMap.containsKey(currentChar)) {
                            HashMap<String, Float> probabilitySubSubMap = probabilitySubMap.get(currentChar);
                            // The previous char ("w" in bigram "wh") is in the submap
                            if (probabilitySubSubMap.containsKey(previousChar)) {
                                // Get the probability that we computed before
                                probability = probabilitySubSubMap.get(previousChar);
                            }
                        }

                        // Add the probability to the current score
                        // We use log10 to avoid underflow
                        double score =+ finalScoreMap2.get(language) + Math.log10(probability);
                        finalScoreMap2.put(language, score);

                        // Add to the output file
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

                // Write the output file
                Class<? extends Language> mostProbableLanguage = Collections.max(scoreMap.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
                Language languageInstance = mostProbableLanguage.newInstance();
                output.append("According to the bigram model, the sentence is in ").append(languageInstance.toString());

                Util.writeInFile("out" + i + ".txt", output.toString());

                // Add the unigram result to the sentence array list
                sentence.setBigramDetectedLanguage(languageInstance);
                sentences.set(i - 1, sentence);

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
