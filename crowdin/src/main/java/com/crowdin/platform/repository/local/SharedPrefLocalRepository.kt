package com.crowdin.platform.repository.local

import android.content.Context
import android.content.SharedPreferences
import com.crowdin.platform.repository.model.*
import com.google.gson.Gson

/**
 * A LocalRepository which saves/loads the strings in Shared Preferences.
 * it also keeps the strings in memory by using MemoryLocalRepository internally for faster access.
 */
internal class SharedPrefLocalRepository internal constructor(context: Context) : LocalRepository {

    companion object {
        private const val SHARED_PREF_NAME = "com.crowdin.platform.string.repository"
        private const val COOKIE_CSRF_TOKEN = "cookie.csrf_token"
    }

    private lateinit var sharedPreferences: SharedPreferences
    private val memoryLocalRepository = MemoryLocalRepository()

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

    override fun getString(language: String, key: String): String? = memoryLocalRepository.getString(language, key)

    override fun getLanguageData(language: String): LanguageData? = memoryLocalRepository.getLanguageData(language)

    override fun getStringArray(key: String): Array<String>? = memoryLocalRepository.getStringArray(key)

    override fun getStringPlural(resourceKey: String, quantityKey: String): String? =
            memoryLocalRepository.getStringPlural(resourceKey, quantityKey)

    override fun isExist(language: String): Boolean = memoryLocalRepository.isExist(language)

    override fun getTextData(text: String): SearchResultData = memoryLocalRepository.getTextData(text)

    override fun saveCookies(csrfToken: String) {
        memoryLocalRepository.saveCookies(csrfToken)
        sharedPreferences.edit().putString(COOKIE_CSRF_TOKEN, csrfToken).apply()
    }

    override fun getCookies(): String? = memoryLocalRepository.getCookies()
            ?: sharedPreferences.getString(COOKIE_CSRF_TOKEN, null)

    private fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun loadStrings() {
        val strings = sharedPreferences.all

        for ((_, value1) in strings) {
            if (value1 !is String) {
                continue
            }

            val languageData = deserializeKeyValues(value1)
            memoryLocalRepository.saveLanguageData(languageData)
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
