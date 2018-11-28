package ca.concordia.comp6721.miniproject3.languages;

/**
 * English class language
 */
public class English implements Language {
    /**
     * Get language 2 letters code
     * @return string EN
     */
    @Override
    public String getCode() {
        return "EN";
    }

    /**
     * Get the name of the language
     * @return string English
     */
    @Override
    public String toString() {
        return "English";
    }
}
