package ca.concordia.comp6721.miniproject3;

import ca.concordia.comp6721.miniproject3.languages.Language;

import java.util.Objects;

public class Sentence {
    private Language unigramDetectedLanguage;

    private Language bigramDetectedLanguage;

    private Language actualLanguage;

    private String sentence;

    public Sentence(String sentence) {
        this.sentence = sentence;
    }

    public Language getUnigramDetectedLanguage() {
        return unigramDetectedLanguage;
    }

    public void setUnigramDetectedLanguage(Language unigramDetectedLanguage) {
        this.unigramDetectedLanguage = unigramDetectedLanguage;
    }

    public Language getBigramDetectedLanguage() {
        return bigramDetectedLanguage;
    }

    public void setBigramDetectedLanguage(Language bigramDetectedLanguage) {
        this.bigramDetectedLanguage = bigramDetectedLanguage;
    }

    public Language getActualLanguage() {
        return actualLanguage;
    }

    public void setActualLanguage(Language actualLanguage) {
        this.actualLanguage = actualLanguage;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    @Override
    public String toString() {
        return "\nSentence{" +
                "sentence='" + sentence + '\'' +
                ", actualLanguage=" + actualLanguage +
                ", unigramDetectedLanguage=" + unigramDetectedLanguage +
                ", bigramDetectedLanguage=" + bigramDetectedLanguage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence1 = (Sentence) o;
        return Objects.equals(unigramDetectedLanguage, sentence1.unigramDetectedLanguage) &&
                Objects.equals(bigramDetectedLanguage, sentence1.bigramDetectedLanguage) &&
                Objects.equals(actualLanguage, sentence1.actualLanguage) &&
                Objects.equals(sentence, sentence1.sentence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unigramDetectedLanguage, bigramDetectedLanguage, actualLanguage, sentence);
    }
}
