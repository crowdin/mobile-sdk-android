package com.crowdin.platform.repository.local;

import com.crowdin.platform.api.LanguageData;

/**
 * Repository of strings.
 */
public interface StringRepository {

    /**
     * Save {@link LanguageData} for a specific language.
     *
     * @param language     the strings belongs to.
     * @param languageData new strings for the language.
     */
    void saveLanguageData(String language, LanguageData languageData);

    /**
     * set a single string(key, value) for a specific language.
     *
     * @param language the string belongs to.
     * @param key      the key of the string which is the string resource id.
     * @param value    the new string.
     */
    void setString(String language, String key, String value);

    /**
     * Get a string for a language & key.
     *
     * @param language the language of the string.
     * @param key      the string resource id.
     * @return the string if exists, otherwise NULL.
     */
    String getString(String language, String key);

    /**
     * Get all resource data for a specific language.
     *
     * @param language the lanugage of the strings.
     * @return the {@link LanguageData}. return null if there's no data.
     */
    LanguageData getStrings(String language);

    /**
     * Get a string array for a language & key.
     *
     * @param language the language of the string.
     * @param key      the string resource id.
     * @return the string array if exists, otherwise NULL.
     */
    String[] getStringArray(String language, String key);
}
