package ca.concordia.comp6721.miniproject3;

import ca.concordia.comp6721.miniproject3.languages.Language;

import java.util.Objects;

/**
 * Handle sentences in a POJO
 */
public class Sentence {
    private Language unigramDetectedLanguage;

    private Language bigramDetectedLanguage;

    private Language language;

    private String sentence;

    /**
     * Constructor
     * @param sentence string the sentence
     */
    public Sentence(String sentence) {
        this.sentence = sentence;
    }

    /**
     * Get the language detected by the unigram model
     * @return Language the language detected by the unigram model
     */
    public Language getUnigramDetectedLanguage() {
        return unigramDetectedLanguage;
    }

    /**
     * Set the language detected by the unigram model
     * @param unigramDetectedLanguage the language detected by the unigram model
     */
    public void setUnigramDetectedLanguage(Language unigramDetectedLanguage) {
        this.unigramDetectedLanguage = unigramDetectedLanguage;
    }

    /**
     * Get the language detected by the bigram model
     * @return Language the language detected by the bigram model
     */
    public Language getBigramDetectedLanguage() {
        return bigramDetectedLanguage;
    }

    /**
     * Set the language detected by the bigram model
     * @param bigramDetectedLanguage the language detected by the bigram model
     */
    public void setBigramDetectedLanguage(Language bigramDetectedLanguage) {
        this.bigramDetectedLanguage = bigramDetectedLanguage;
    }

    /**
     * Get the language
     * @return Language the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Set the language
     * @param language the language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Get the string sentence
     * @return String the sentence
     */
    public String getSentence() {
        return sentence;
    }

    /**
     * Set the sentence
     * @param sentence string the sentence
     */
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    /**
     * Display the Sentence instance
     * @return String
     */
    @Override
    public String toString() {
        return "\nSentence{" +
                "sentence='" + sentence + '\'' +
                ", language=" + language +
                ", unigramDetectedLanguage=" + unigramDetectedLanguage +
                ", bigramDetectedLanguage=" + bigramDetectedLanguage +
                '}';
    }

    /**
     * Override equals
     * @param o object to test
     * @return boolean true or false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence1 = (Sentence) o;
        return Objects.equals(unigramDetectedLanguage, sentence1.unigramDetectedLanguage) &&
                Objects.equals(bigramDetectedLanguage, sentence1.bigramDetectedLanguage) &&
                Objects.equals(language, sentence1.language) &&
                Objects.equals(sentence, sentence1.sentence);
    }

    /**
     * Override hashcode
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(unigramDetectedLanguage, bigramDetectedLanguage, language, sentence);
    }
}
