package com.crowdin.platform.repository.local;

import com.crowdin.platform.api.LanguageData;

/**
 * Repository of strings.
 */
public interface LocalRepository {

    /**
     * Save {@link LanguageData} for a specific language.
     *
     * @param languageData new strings for the language.
     */
    void saveLanguageData(LanguageData languageData);

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
     * Get a string array for a current language & key.
     *
     * @param key the string resource id.
     * @return the string array if exists, otherwise NULL.
     */
    String[] getStringArray(String key);

    /**
     * Get a string value from plural resource defined by PluralRules for a current language.
     *
     * @param resourceKey the plural resource id.
     * @param quantityKey the key for quantity item defined in plural.
     * @return the string value if exists, otherwise NULL.
     */
    String getStringPlural(String resourceKey, String quantityKey);

    /**
     * Returns <tt>true</tt> if repository contains the specified data for language.
     *
     * @param language to compare.
     * @return true if exist, otherwise false.
     */
    boolean isExist(String language);
}
