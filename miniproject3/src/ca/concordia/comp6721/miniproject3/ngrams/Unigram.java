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

public class Unigram extends AbstractNgram {

    private HashMap<Class<? extends Language>, HashMap<Character, Float>> probabilityMap;
    private Map<Class<? extends Language>, Long> numberOfChar;

    public Unigram() {
        super();
        probabilityMap = new HashMap<>();
        trainingFiles.forEach(((language, filename) -> {
            probabilityMap.put(language, new HashMap<>());
        }));
    }

    @Override
    public void train() {
        numberOfChar = new HashMap<>();

        trainingFiles.forEach(((language, filename) -> {
            try {
                String text = Util.cleanString(
                        Util.readFile(new File("input/" + filename).getPath(), Charset.defaultCharset()));

                numberOfChar.put(language, text.chars().count());

                Util.countAlphabet(text, langMap.get(language));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        langMap.forEach((language, alphabetMap) -> {
            long totalChar = numberOfChar.get(language);
            alphabetMap.forEach((character, numberOfOccurences) -> {
                // Probability of the character
                // 0.5-smoothing: +0.5 on the numerator, +26 (number of different output) * 0.5 on the denominator
                float probability = (float)(numberOfOccurences + DELTA_SMOOTHING) / (float)(totalChar + 26 * DELTA_SMOOTHING);
                HashMap<Character, Float> alphabetMap2 = probabilityMap.get(language);
                alphabetMap2.put(character, probability);
            });
        });
    }

    @Override
    public void output() {
        probabilityMap.forEach((language, alphabetMap) -> {
            StringBuilder stringBuilder = new StringBuilder();

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
                            float probability = Float.MIN_VALUE;
                            if (probabilityMap.get(language).containsKey(character)) {
                                probability = probabilityMap.get(language).get(character);
                            }
                            double score =+ finalScoreMap2.get(language) + Math.log10(probability);
                            finalScoreMap2.put(language, score);

                            finalOut.append(language.newInstance().toString().toUpperCase())
                                    .append(": P(").append(character).append(") = ").append(probability)
                                    .append(" ==> log prob of sentence so far: ").append(score).append("\n");
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
                }

                output.append("\n");

                Class<? extends Language> mostProbableLanguage = Collections.max(scoreMap.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
                Language languageInstance = mostProbableLanguage.newInstance();
                output.append("According to the unigram model, the sentence is in ").append(languageInstance.toString());

                output.append("\n----------------");

                Util.writeInFile("out" + i + ".txt", output.toString());

                // Get expected language as string
                String expectedLanguage = Files.readAllLines(Paths.get("input/sentencesLanguages.txt")).get(i - 1);
                // Convert it to the class and add it
                Class<?> classType = Class.forName("ca.concordia.comp6721.miniproject3.languages."+expectedLanguage);
                sentence.setActualLanguage((Language) classType.newInstance());

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
