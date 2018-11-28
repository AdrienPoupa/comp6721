package ca.concordia.comp6721.miniproject3.languages;

/**
 * Spanish class language
 */
public class Spanish implements Language {
    /**
     * Get language 2 letters code
     * @return string ES
     */
    @Override
    public String getCode() {
        return "ES";
    }

    /**
     * Get the name of the language
     * @return string Spanish
     */
    @Override
    public String toString() {
        return "Spanish";
    }
}
