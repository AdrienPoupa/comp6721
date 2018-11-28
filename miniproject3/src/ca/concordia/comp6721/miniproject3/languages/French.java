package ca.concordia.comp6721.miniproject3.languages;

/**
 * French class language
 */
public class French implements Language {
    /**
     * Get language 2 letters code
     * @return string FR
     */
    @Override
    public String getCode() {
        return "FR";
    }

    /**
     * Get the name of the language
     * @return string French
     */
    @Override
    public String toString() {
        return "French";
    }
}
