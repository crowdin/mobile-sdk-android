package com.crowdin.platform.data.local

import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.model.TextMetaData
import java.lang.reflect.Type

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
     * Set a single string(key, value) for a specific language.
     *
     * @param language the string belongs to.
     * @param key the key of the string which is the string resource id.
     * @param value the new string.
     */
    fun setString(language: String, key: String, value: String)

    /**
     * Set a string data for a specific language.
     *
     * @param language the string belongs to.
     * @param stringData the new string data.
     */
    fun setStringData(language: String, stringData: StringData)

    /**
     * Set a string array data for a specific language.
     *
     * @param language the string belongs to.
     * @param arrayData the new string array data.
     */
    fun setArrayData(language: String, arrayData: ArrayData)

    /**
     * Set a plural data for a specific language.
     *
     * @param language the string belongs to.
     * @param pluralData the new plural data.
     */
    fun setPluralData(language: String, pluralData: PluralData)

    /**
     * Get a string for a language & key.
     *
     * @param language the language of the string.
     * @param key the string resource id.
     * @return the string if exists, otherwise NULL.
     */
    fun getString(language: String, key: String): String?

    /**
     * Get all resource data for a specific language.
     *
     * @param language the language of the strings.
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

    /**
     * Retrieves text related meta data.
     *
     * @param text searchable text.
     * @return TextMetaData meta data.
     */
    fun getTextData(text: String): TextMetaData = TextMetaData()

    /**
     * Save any data object to local storage.
     *
     * @param type of data.
     * @param data to be stored.
     */
    fun saveData(type: String, data: Any?)

    /**
     * Retrieve any data object from locale storage.
     *
     * @param type of data.
     * @param classType of data.
     */
    fun getData(type: String, classType: Type): Any?
}
