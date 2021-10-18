package com.crowdin.platform.example

import android.content.Context
import java.lang.StringBuilder
import java.util.Locale

class LanguagePreferences(context: Context) {

    private var preferences =
        context.applicationContext.getSharedPreferences(GLOBAL_USER_PREF, Context.MODE_PRIVATE)

    fun setLanguageCode(languageCode: String) {
        preferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguageCode(): String {
        return preferences.getString(KEY_LANGUAGE, getDefaultLanguageCode())
            ?: getDefaultLanguageCode()
    }

    fun getDefaultLanguageCode(): String {
        val defaultLocale = Locale.getDefault()

        val sb = StringBuilder()
            .append(defaultLocale.language)

        if (defaultLocale.country.isNotEmpty()) {
            sb.append("-")
            sb.append(defaultLocale.country)
        }

        return sb.toString()
    }

    companion object {
        private const val GLOBAL_USER_PREF = "crowdin.example.global_user_pref"
        const val KEY_LANGUAGE = "language_key"
    }
}
