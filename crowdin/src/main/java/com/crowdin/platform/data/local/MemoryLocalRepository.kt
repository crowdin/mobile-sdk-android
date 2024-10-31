package com.crowdin.platform.data.local

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.model.TextMetaData
import com.crowdin.platform.util.getFormattedCode
import java.lang.reflect.Type
import java.util.IllegalFormatException
import java.util.Locale

/**
 * A LocalRepository which keeps the stringsData ONLY in memory.
 */
internal class MemoryLocalRepository : LocalRepository {
    private val stringsData = LinkedHashMap<String, LanguageData>()
    private val generalData = mutableMapOf<String, Any?>()

    override fun saveLanguageData(languageData: LanguageData) {
        when (val data = stringsData[languageData.language]) {
            null -> stringsData[languageData.language] = languageData
            else -> data.updateResources(languageData)
        }
    }

    override fun setString(
        language: String,
        key: String,
        value: String,
    ) {
        val newStringData = StringData(key, value)
        saveStringData(language, newStringData)
    }

    override fun setStringData(
        language: String,
        stringData: StringData,
    ) {
        saveStringData(language, stringData)
    }

    override fun setArrayData(
        language: String,
        arrayData: ArrayData,
    ) {
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

    override fun setPluralData(
        language: String,
        pluralData: PluralData,
    ) {
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

    override fun getString(
        language: String,
        key: String,
    ): String? {
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
        stringsData[Locale.getDefault().getFormattedCode()]?.arrays?.forEach { array ->
            if (array.name == key) {
                return array.values
            }
        }
        return null
    }

    override fun getStringPlural(
        resourceKey: String,
        quantityKey: String,
    ): String? {
        stringsData[Locale.getDefault().getFormattedCode()]?.plurals?.forEach { pluralData ->
            if (pluralData.name == resourceKey) {
                return pluralData.quantity[quantityKey]
            }
        }
        return null
    }

    override fun isExist(language: String): Boolean = stringsData[language] != null

    override fun containsKey(key: String): Boolean {
        stringsData[Locale.getDefault().getFormattedCode()]?.let { languageData ->
            languageData.resources.forEach {
                if (it.stringKey == key) {
                    return true
                }
            }
            languageData.plurals.forEach {
                if (it.name == key) {
                    return true
                }
            }
            languageData.arrays.forEach {
                if (it.name == key) {
                    return true
                }
            }
        }

        return false
    }

    override fun getTextData(text: String): TextMetaData {
        val textMetaData = TextMetaData()

        val languageData = stringsData[Locale.getDefault().getFormattedCode()]
        searchInResources(languageData, text, textMetaData)

        val languageReserveData =
            stringsData[Locale.getDefault().getFormattedCode() + DataManager.SUF_COPY]
        searchInResources(languageReserveData, text, textMetaData)

        return textMetaData
    }

    override fun saveData(
        type: String,
        data: Any?,
    ) {
        generalData[type] = data
    }

    override fun <T> getData(
        type: String,
        classType: Type,
    ): T? = generalData[type] as T

    private fun searchInResources(
        languageData: LanguageData?,
        text: String,
        searchResultData: TextMetaData,
    ) {
        searchInStrings(languageData, text, searchResultData)
        searchInArrays(languageData, text, searchResultData)
        searchInPlurals(languageData, text, searchResultData)
    }

    private fun searchInPlurals(
        languageData: LanguageData?,
        text: String,
        searchResultData: TextMetaData,
    ) {
        languageData?.plurals?.forEach { pluralData ->
            val pluralName = pluralData.name
            pluralData.quantity.forEach {
                try {
                    if (it.value == text ||
                        (
                            pluralData.formatArgs.isNotEmpty() &&
                                it.value ==
                                String.format(
                                    text,
                                    pluralData.formatArgs,
                                )
                        )
                    ) {
                        searchResultData.pluralName = pluralName
                        searchResultData.pluralQuantity = pluralData.number
                        searchResultData.pluralFormatArgs = pluralData.formatArgs
                        return
                    }
                } catch (ex: IllegalFormatException) {
                    return
                }
            }
        }
    }

    private fun searchInArrays(
        languageData: LanguageData?,
        text: String,
        textMetaData: TextMetaData,
    ) {
        languageData?.arrays?.forEach { arrayData ->
            val arrayName = arrayData.name
            arrayData.values?.forEachIndexed { index, item ->
                if (item == text) {
                    textMetaData.arrayName = arrayName
                    textMetaData.arrayIndex = index
                    return
                }
            }
        }
    }

    private fun searchInStrings(
        languageData: LanguageData?,
        text: String,
        textMetaData: TextMetaData,
    ) {
        languageData?.resources?.forEach {
            if (it.stringValue == text) {
                textMetaData.textAttributeKey = it.stringKey
                textMetaData.stringsFormatArgs = it.formatArgs
                textMetaData.stringDefault = it.default
                return
            }
        }
    }

    private fun getMatch(
        resources: MutableList<StringData>,
        newStringData: StringData,
    ): StringData? {
        resources.forEach {
            if (it.stringKey == newStringData.stringKey) {
                return it
            }
        }
        return null
    }

    private fun saveStringData(
        language: String,
        newStringData: StringData,
    ) {
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
