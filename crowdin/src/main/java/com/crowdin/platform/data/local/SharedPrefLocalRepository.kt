package com.crowdin.platform.data.local

import android.content.Context
import android.content.SharedPreferences
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.model.TextMetaData
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * A LocalRepository which saves/loads the strings in Shared Preferences.
 * it also keeps the strings in memory by using MemoryLocalRepository internally for faster access.
 */
internal class SharedPrefLocalRepository internal constructor(
    context: Context,
    private val memoryLocalRepository: MemoryLocalRepository
) : LocalRepository {

    companion object {
        private const val SHARED_PREF_NAME = "com.crowdin.platform.string.repository"
    }

    private lateinit var sharedPreferences: SharedPreferences

    init {
        initSharedPreferences(context)
        loadStrings()
    }

    override fun saveLanguageData(languageData: LanguageData) {
        memoryLocalRepository.saveLanguageData(languageData)
        val data = memoryLocalRepository.getLanguageData(languageData.language) ?: return
        saveData(data)
    }

    override fun setString(language: String, key: String, value: String) {
        memoryLocalRepository.setString(language, key, value)
        val languageData = memoryLocalRepository.getLanguageData(language) ?: return
        saveData(languageData)
    }

    override fun setStringData(language: String, stringData: StringData) {
        memoryLocalRepository.setStringData(language, stringData)
        val languageData = memoryLocalRepository.getLanguageData(language) ?: return
        saveData(languageData)
    }

    override fun setArrayData(language: String, arrayData: ArrayData) {
        memoryLocalRepository.setArrayData(language, arrayData)
        val languageData = memoryLocalRepository.getLanguageData(language) ?: return
        saveData(languageData)
    }

    override fun setPluralData(language: String, pluralData: PluralData) {
        memoryLocalRepository.setPluralData(language, pluralData)
        val languageData = memoryLocalRepository.getLanguageData(language) ?: return
        saveData(languageData)
    }

    override fun getString(language: String, key: String): String? =
        memoryLocalRepository.getString(language, key)

    override fun getLanguageData(language: String): LanguageData? =
        memoryLocalRepository.getLanguageData(language)

    override fun getStringArray(key: String): Array<String>? =
        memoryLocalRepository.getStringArray(key)

    override fun getStringPlural(resourceKey: String, quantityKey: String): String? =
        memoryLocalRepository.getStringPlural(resourceKey, quantityKey)

    override fun isExist(language: String): Boolean = memoryLocalRepository.isExist(language)

    override fun containsKey(key: String): Boolean = memoryLocalRepository.containsKey(key)

    override fun getTextData(text: String): TextMetaData = memoryLocalRepository.getTextData(text)

    override fun saveData(type: String, data: Any?) {
        memoryLocalRepository.saveData(type, data)
        if (data == null) {
            sharedPreferences.edit().remove(type).apply()
        } else {
            val json = Gson().toJson(data)
            sharedPreferences.edit().putString(type, json).apply()
        }
    }

    override fun <T> getData(type: String, classType: Type): T? {
        val data = memoryLocalRepository.getData<T>(type, classType::class.java)
        if (data == null) {
            val info = sharedPreferences.getString(type, null)
            info?.let { return Gson().fromJson(info, classType) }
            return null
        } else {
            return data
        }
    }

    private fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun loadStrings() {
        val strings = sharedPreferences.all

        for ((_, value1) in strings) {
            if (value1 !is String) {
                continue
            }

            try {
                val languageData = deserializeKeyValues(value1)
                memoryLocalRepository.saveLanguageData(languageData)
            } catch (ex: Exception) {
                // not language data
            }
        }
    }

    private fun saveData(languageData: LanguageData) {
        val json = Gson().toJson(languageData)
        sharedPreferences.edit()
            .putString(languageData.language, json)
            .apply()
    }

    private fun deserializeKeyValues(content: String): LanguageData =
        Gson().fromJson(content, LanguageData::class.java)
}
