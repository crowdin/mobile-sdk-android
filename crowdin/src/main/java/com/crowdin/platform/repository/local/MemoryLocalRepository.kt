package com.crowdin.platform.repository.local

import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.utils.LocaleUtils
import java.util.*

/**
 * A LocalRepository which keeps the stringsData ONLY in memory.
 *
 * it's not ThreadSafe.
 */
internal class MemoryLocalRepository : LocalRepository {

    private val stringsData = LinkedHashMap<String, LanguageData>()

    override fun saveLanguageData(languageData: LanguageData) {
        stringsData[languageData.language] = languageData
    }

    override fun setString(language: String, key: String, value: String) {
        val data = stringsData[language]
        if (data == null) {
            stringsData[language] = LanguageData(language)
        } else {
            data.resources.plus(Pair(key, value))
        }
    }

    override fun getString(language: String, key: String): String? {
        val languageData = stringsData[language]
        return if (languageData == null || !languageData.resources.containsKey(key)) {
            null
        } else languageData.resources[key]
    }

    override fun getStrings(language: String): LanguageData? {
        return if (!stringsData.containsKey(language)) {
            null
        } else stringsData[language]

    }

    override fun getStringArray(key: String): Array<String>? {
        val languageData = stringsData[LocaleUtils.currentLanguage]
        if (languageData != null) {
            for (array in languageData.arrays) {
                if (array.name == key) {
                    return array.values
                }
            }
        }

        return null
    }

    override fun getStringPlural(resourceKey: String, quantityKey: String): String? {
        val languageData = stringsData[LocaleUtils.currentLanguage]
        if (languageData != null) {
            for (pluralData in languageData.plurals) {
                if (pluralData.name == resourceKey) {
                    return pluralData.quantity[quantityKey]
                }
            }
        }

        return null
    }

    override fun isExist(language: String): Boolean {
        return stringsData[language] != null
    }
}