package com.crowdin.platform.data.local

import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.model.*
import java.util.*

/**
 * A LocalRepository which keeps the stringsData ONLY in memory.
 */
internal class MemoryLocalRepository : LocalRepository {

    private val stringsData = LinkedHashMap<String, LanguageData>()
    private var csrfToken: String? = null

    override fun saveLanguageData(languageData: LanguageData) {
        when (val data = stringsData[languageData.language]) {
            null -> stringsData[languageData.language] = languageData
            else -> data.updateResources(languageData)
        }
    }

    override fun setString(language: String, key: String, value: String) {
        val newStringData = StringData(key, value)
        saveStringData(language, newStringData)
    }

    override fun setStringData(language: String, stringData: StringData) {
        saveStringData(language, stringData)
    }

    override fun setArrayData(language: String, arrayData: ArrayData) {
        when (val data = stringsData[language]) {
            null -> {
                val languageData = LanguageData(language)
                languageData.arrays.add(arrayData)
                stringsData[language] = languageData
            }
            else -> {
                var index = -1
                data.arrays.forEachIndexed { position, array ->
                    if (array.name == arrayData.name) {
                        index = position
                    }
                }

                when {
                    index != -1 -> data.arrays[index] = arrayData
                    else -> data.arrays.add(arrayData)
                }
            }
        }
    }

    override fun setPluralData(language: String, pluralData: PluralData) {
        when (val data = stringsData[language]) {
            null -> {
                val languageData = LanguageData(language)
                languageData.plurals.add(pluralData)
                stringsData[language] = languageData
            }
            else -> {
                var isExist = false
                data.plurals.forEach { plural ->
                    if (plural.name == pluralData.name) {
                        isExist = true
                        plural.updateResources(pluralData)
                    }
                }
                if (!isExist) {
                    data.plurals.add(pluralData)
                }
            }
        }
    }

    override fun getString(language: String, key: String): String? {
        val languageData = stringsData[language] ?: return null
        languageData.resources.forEach {
            if (it.stringKey == key) {
                return it.stringValue
            }
        }

        return null
    }

    override fun getLanguageData(language: String): LanguageData? =
            when {
                !stringsData.containsKey(language) -> null
                else -> stringsData[language]
            }

    override fun getStringArray(key: String): Array<String>? {
        stringsData[Locale.getDefault().toString()]?.arrays?.forEach { array ->
            if (array.name == key) {
                return array.values
            }
        }

        return null
    }

    override fun getStringPlural(resourceKey: String, quantityKey: String): String? {
        stringsData[Locale.getDefault().toString()]?.plurals?.forEach { pluralData ->
            if (pluralData.name == resourceKey) {
                return pluralData.quantity[quantityKey]
            }
        }
        return null
    }

    override fun isExist(language: String): Boolean {
        return stringsData[language] != null
    }

    override fun getTextData(text: String): SearchResultData {
        val searchResultData = SearchResultData()

        val languageData = stringsData[Locale.getDefault().toString()]
        searchInResources(languageData, text, searchResultData)

        val languageReserveData = stringsData[Locale.getDefault().language + StringDataManager.SUF_COPY]
        searchInResources(languageReserveData, text, searchResultData)

        return searchResultData
    }

    override fun saveCookies(csrfToken: String) {
        this.csrfToken = csrfToken
    }

    override fun getCookies(): String? = csrfToken

    private fun searchInResources(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        searchInStrings(languageData, text, searchResultData)
        searchInArrays(languageData, text, searchResultData)
        searchInPlurals(languageData, text, searchResultData)
    }

    private fun searchInPlurals(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        languageData?.plurals?.forEach { pluralData ->
            val pluralName = pluralData.name
            pluralData.quantity.forEach {
                if (it.value == text ||
                        (pluralData.formatArgs.isNotEmpty()
                                && it.value == String.format(text, pluralData.formatArgs))) {
                    searchResultData.pluralName = pluralName
                    searchResultData.pluralQuantity = pluralData.number
                    searchResultData.pluralFormatArgs = pluralData.formatArgs
                    return
                }
            }
        }
    }

    private fun searchInArrays(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        languageData?.arrays?.forEach { arrayData ->
            val arrayName = arrayData.name
            arrayData.values?.forEachIndexed { index, item ->
                if (item == text) {
                    searchResultData.arrayName = arrayName
                    searchResultData.arrayIndex = index
                    return
                }
            }
        }
    }

    private fun searchInStrings(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        languageData?.resources?.forEach {
            if (it.stringValue == text) {
                searchResultData.stringKey = it.stringKey
                searchResultData.stringValue = it.stringValue
                searchResultData.stringsFormatArgs = it.formatArgs
                searchResultData.stringDefault = it.def
                return
            }
        }
    }

    private fun getMatch(resources: MutableList<StringData>, newStringData: StringData): StringData? {
        resources.forEach {
            if (it.stringKey == newStringData.stringKey) {
                return it
            }
        }
        return null
    }

    private fun saveStringData(language: String, newStringData: StringData) {
        when (val data = stringsData[language]) {
            null -> {
                val languageData = LanguageData(language)
                languageData.resources.add(newStringData)
                stringsData[language] = languageData
            }
            else -> {
                val stringData = getMatch(data.resources, newStringData)
                if (stringData == null) {
                    data.resources.add(newStringData)
                } else {
                    stringData.updateResources(newStringData)
                }
            }
        }
    }
}