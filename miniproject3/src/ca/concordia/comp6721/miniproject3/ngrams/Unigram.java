package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.Sentence;
import ca.concordia.comp6721.miniproject3.Util;
import ca.concordia.comp6721.miniproject3.languages.Language;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Unigram prediction
 */
public class Unigram extends AbstractNgram {

    // HashMap associating language => char => probability
    private HashMap<Class<? extends Language>, HashMap<Character, Float>> probabilityMap;

    // Number of characters for a language
    private Map<Class<? extends Language>, Long> numberOfChar;

    /**
     * Unigram constructor
     */
    public Unigram() {

        // Call to parent (abstract) constructor
        super();

        // Setup the maps
        probabilityMap = new HashMap<>();
        numberOfChar = new HashMap<>();

        trainingFiles.forEach(((language, filename) -> {
            probabilityMap.put(language, new HashMap<>());
        }));
    }

    /**
     * Train the unigram model
     */
    @Override
    public void train() {
        // For all languages
        trainingFiles.forEach(((language, filename) -> {
            try {
                String text = Util.cleanString(
                        Util.readFile(new File("input/" + filename).getPath(), Charset.defaultCharset()));

                // Count the total number of characters
                numberOfChar.put(language, text.chars().count());

                // Count the number of each character
                Util.countAlphabet(text, langMap.get(language));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        // Compute the probabilities for each character of each language
        langMap.forEach((language, alphabetMap) -> {
            long totalChar = numberOfChar.get(language);
            alphabetMap.forEach((character, numberOfOccurences) -> {
                // Probability of the character
                // delta-smoothing: +delta on the numerator, +numberOfLetters
                // (number of different outputs, hence 26 for English or French, 25 for Spanish) * delta on the denominator
                int numberOfLetters = alphabetMap.size();
                float probability = (float)(numberOfOccurences + DELTA_SMOOTHING) /
                        (float)(totalChar + numberOfLetters * DELTA_SMOOTHING);

                HashMap<Character, Float> alphabetMap2 = probabilityMap.get(language);
                alphabetMap2.put(character, probability);
            });
        });
    }

    /**
     * Output the probabilities for the bigrams
     */
    @Override
    public void output() {
        probabilityMap.forEach((language, alphabetMap) -> {
            StringBuilder stringBuilder = new StringBuilder();

            // Use the scientific notation
            NumberFormat scientificNotation = new DecimalFormat("0.####E0");

            alphabetMap.forEach((character, probability) -> stringBuilder.append("(")
                    .append(character)
                    .append(") = ")
                    .append(scientificNotation.format(probability))
                    .append("\n"));

            String output = stringBuilder.toString();
            try {
                Language languageInstance = language.newInstance();
                Util.writeInFile("unigram" + languageInstance.getCode() + ".txt", output);
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

            int i = 1;
            StringBuilder output;
            for(String line: lines) {
                // Init probabilities at 0
                scoreMap = new HashMap<>();
                HashMap<Class<? extends Language>, Double> finalScoreMap1 = scoreMap;
                trainingFiles.forEach(((language, filename) -> finalScoreMap1.put(language, 0.0)));

                // Start creating the StringBuilder that will be written into the output file
                output = new StringBuilder();
                output.append(line).append("\n").append("\n");

                // Create the sentence
                Sentence sentence = new Sentence(line);

                // Clean the sentence
                String cleanLine = Util.cleanString(line);

                output.append("UNIGRAM MODEL:").append("\n");
                for (char character: cleanLine.toCharArray()) {
                    output.append("\n").append("UNIGRAM: ").append(character).append("\n");
                    StringBuilder finalOut = output;

                    HashMap<Class<? extends Language>, Double> finalScoreMap2 = scoreMap;
                    probabilityMap.forEach((language, alphabetMap) -> {
                        try {
                            // Get the probability for the current unigram (character)
                            // We init the probability at the minimum value, because if a character is not in the map,
                            // there is a high probability that it is not in the current language
                            // Thus, setting a low probability value for this character will decrease the final score
                            float probability = Float.MIN_VALUE;

                            // If the current character exists in the probability map
                            if (probabilityMap.get(language).containsKey(character)) {
                                // Get the probability
                                probability = probabilityMap.get(language).get(character);
                            }

                            // Add the probability to the current score
                            // We use log10 to avoid underflow
                            double score =+ finalScoreMap2.get(language) + Math.log10(probability);

                            finalScoreMap2.put(language, score);

                            // Add to the output file
                            finalOut.append(language.newInstance().toString().toUpperCase())
                                    .append(": P(").append(character).append(") = ").append(probability)
                                    .append(" ==> log prob of sentence so far: ").append(score).append("\n");
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
                }

                output.append("\n");

                // Write the output file
                Class<? extends Language> mostProbableLanguage = Collections.max(scoreMap.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
                Language languageInstance = mostProbableLanguage.newInstance();
                output.append("According to the unigram model, the sentence is in ").append(languageInstance.toString());

                output.append("\n----------------");

                Util.writeInFile("out" + i + ".txt", output.toString());

                // Get expected language as string
                String expectedLanguage = Files.readAllLines(Paths.get("input/sentencesLanguages.txt")).get(i - 1);
                // Convert it to the class and add it
                Class<?> classType = Class.forName("ca.concordia.comp6721.miniproject3.languages."+expectedLanguage);
                sentence.setLanguage((Language) classType.newInstance());

                // Add the unigram result to the sentence array list
                sentence.setUnigramDetectedLanguage(languageInstance);
                sentences.add(sentence);

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
