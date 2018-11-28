package ca.concordia.comp6721.miniproject3.languages;

/**
 * Italian class language
 */
public class Italian implements Language {
    /**
     * Get language 2 letters code
     * @return string IT
     */
    @Override
    public String getCode() {
        return "IT";
    }

    /**
     * Get the name of the language
     * @return string Italian
     */
    @Override
    public String toString() {
        return "Italian";
    }
}
