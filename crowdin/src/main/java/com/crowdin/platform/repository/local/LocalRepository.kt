package com.crowdin.platform.repository.local

import com.crowdin.platform.repository.remote.api.LanguageData

/**
 * Repository of strings.
 */
internal interface LocalRepository {

    /**
     * Save [LanguageData] for a specific language.
     *
     * @param languageData new strings for the language.
     */
    fun saveLanguageData(languageData: LanguageData)

    /**
     * set a single string(key, value) for a specific language.
     *
     * @param language the string belongs to.
     * @param key      the key of the string which is the string resource id.
     * @param value    the new string.
     */
    fun setString(language: String, key: String, value: String)

    /**
     * Get a string for a language & key.
     *
     * @param language the language of the string.
     * @param key      the string resource id.
     * @return the string if exists, otherwise NULL.
     */
    fun getString(language: String, key: String): String?

    /**
     * Get all resource data for a specific language.
     *
     * @param language the lanugage of the strings.
     * @return the [LanguageData]. return null if there's no data.
     */
    fun getLanguageData(language: String): LanguageData?

    /**
     * Get a string array for a current language & key.
     *
     * @param key the string resource id.
     * @return the string array if exists, otherwise NULL.
     */
    fun getStringArray(key: String): Array<String>?

    /**
     * Get a string value from plural resource defined by PluralRules for a current language.
     *
     * @param resourceKey the plural resource id.
     * @param quantityKey the key for quantity item defined in plural.
     * @return the string value if exists, otherwise NULL.
     */
    fun getStringPlural(resourceKey: String, quantityKey: String): String?

    /**
     * Returns <tt>true</tt> if repository contains the specified data for language.
     *
     * @param language to compare.
     * @return true if exist, otherwise false.
     */
    fun isExist(language: String): Boolean
}
