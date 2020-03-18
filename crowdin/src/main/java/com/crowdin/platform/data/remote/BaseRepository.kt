package com.crowdin.platform.data.remote

import java.util.Locale

internal abstract class BaseRepository : RemoteRepository {

    internal companion object {
        const val HEADER_ETAG = "ETag"
        const val HEADER_ETAG_EMPTY = ""
        const val LANGUAGE_NAME = "%language%"
        const val TWO_LETTER_CODE = "%two_letters_code%"
        const val THREE_LETTER_CODE = "%three_letters_code%"
        const val LOCALE = "%locale%"
        const val LOCALE_WITH_UNDERSCORE = "%locale_with_underscore%"
        const val ANDROID_CODE = "%android_code%"
        val listExportPattern = listOf(
            LANGUAGE_NAME,
            TWO_LETTER_CODE,
            THREE_LETTER_CODE,
            LOCALE,
            LOCALE_WITH_UNDERSCORE,
            ANDROID_CODE
        )
    }

    protected var eTagMap = mutableMapOf<String, String>()

    protected fun validateFilePath(filePath: String, locale: Locale): String {
        var path = filePath
        val language = locale.language
        val languageThreeLetterCode = locale.isO3Language
        val languageName = locale.getDisplayLanguage(Locale.ENGLISH)
        val country = locale.country
        val formattedCode = if (country == language.toUpperCase(locale)) {
            language
        } else {
            "$language-$country"
        }

        var containsExportPattern = false

        for (element in listExportPattern) {
            if (path.contains(element)) {
                containsExportPattern = true
                break
            }
        }

        if (!containsExportPattern) {
            return if (path.startsWith("/")) {
                "/$formattedCode$path"
            } else {
                "/$formattedCode/$path"
            }
        }

        when {
            path.contains(LANGUAGE_NAME) -> path = path.replace(LANGUAGE_NAME, languageName)
            path.contains(TWO_LETTER_CODE) -> path = path.replace(TWO_LETTER_CODE, language)
            path.contains(THREE_LETTER_CODE) -> path =
                path.replace(THREE_LETTER_CODE, languageThreeLetterCode)
            path.contains(LOCALE) -> path = path.replace(LOCALE, "$language-$country")
            path.contains(LOCALE_WITH_UNDERSCORE) -> path =
                path.replace(LOCALE_WITH_UNDERSCORE, locale.toString())
            path.contains(ANDROID_CODE) -> path = path.replace(ANDROID_CODE, "$language-r$country")
        }

        return path
    }
}
