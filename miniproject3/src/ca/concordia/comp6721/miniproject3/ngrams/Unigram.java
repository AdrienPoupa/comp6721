package ca.concordia.comp6721.miniproject3.ngrams;

import ca.concordia.comp6721.miniproject3.FileUtil;
import ca.concordia.comp6721.miniproject3.languages.English;
import ca.concordia.comp6721.miniproject3.languages.French;
import ca.concordia.comp6721.miniproject3.languages.Language;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class Unigram extends AbstractNgram {

    private HashMap<Class<? extends Language>, HashMap<Character, Float>> probabilityMap;
    private HashMap<Class<? extends Language>, HashMap<Character, Integer>> langMap;

    public Unigram() {
        super();
        langMap = new HashMap<>();
        langMap.put(English.class, new HashMap<>());
        langMap.put(French.class, new HashMap<>());
    }

    @Override
    public void train() {
        Map<Class<? extends Language>, Long> numberOfChar = new HashMap<>();

        trainingFiles.forEach(((language, filename) -> {
            try {
                String text = FileUtil.readFile(new File("input/" + filename).getPath(), Charset.defaultCharset())
                        .replaceAll("[^a-zA-Z]+", "")
                        .toLowerCase();

                numberOfChar.put(language, text.chars().count());

                HashMap<Character, Integer> alphabetMap = langMap.get(language);

                // https://www.quora.com/How-can-I-write-a-Java-program-to-find-each-occurrence-of-a-character-in-a-string-which-is-given-as-an-input-from-a-console-without-using-any-built-in-functions
                for (char ch : text.toCharArray()) {
                    if (alphabetMap.containsKey(ch)) {
                        int val = alphabetMap.get(ch);
                        alphabetMap.put(ch, val + 1);
                    } else {
                        alphabetMap.put(ch, 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        probabilityMap = new HashMap<>();
        probabilityMap.put(English.class, new HashMap<>());
        probabilityMap.put(French.class, new HashMap<>());

        langMap.forEach((language, alphabetMap) -> {
            long totalChar = numberOfChar.get(language);
            alphabetMap.forEach((character, numberOfOccurences) -> {
                // Probability of the character
                // 0.5-smoothing: +0.5 on the numerator, +26 (number of different output) * 0.5 on the denominator
                float probability = (float)(numberOfOccurences + 0.5) / (float)(totalChar + 26 * 0.5);
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
                FileUtil.writeInFile("unigram" + languageInstance.getCode() + ".txt", output);
            } catch (IllegalAccessException | InstantiationException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}
