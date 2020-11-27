package com.crowdin.platform.example

import android.content.Context
import java.util.Locale

class LanguagePreferences(context: Context) {

    private var preferences =
        context.applicationContext.getSharedPreferences(GLOBAL_USER_PREF, Context.MODE_PRIVATE)

    fun setLanguageCode(languageCode: String) {
        preferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguageCode(): String {
        val defaultLocale = Locale.getDefault()
        val defaultCode = "${defaultLocale.language}-${defaultLocale.country}"
        return preferences.getString(KEY_LANGUAGE, defaultCode) ?: defaultCode
    }

    companion object {
        private const val GLOBAL_USER_PREF = "crowdin.example.global_user_pref"
        const val KEY_LANGUAGE = "language_key"
    }
}
