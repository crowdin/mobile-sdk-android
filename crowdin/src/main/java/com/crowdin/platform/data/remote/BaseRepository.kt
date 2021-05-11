package com.crowdin.platform.data.remote

import com.crowdin.platform.data.model.LanguageInfo

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

    fun validateFilePath(
        filePath: String,
        languageInfo: LanguageInfo,
        formattedCode: String,
        languageMapping: Map<String, Map<String, String>>? = null
    ): String {
        var path = filePath

        if (containsLanguageMapping(path, formattedCode, languageMapping)) {
            return getMappedFilePath(path, formattedCode, languageMapping!!)
        }

        if (containsExportPattern(path)) {
            path = replacePatterns(
                path,
                languageInfo.name,
                languageInfo.twoLettersCode,
                languageInfo.threeLettersCode,
                languageInfo.locale,
                languageInfo.locale.replace("-", "_"),
                languageInfo.androidCode
            )
        } else {
            return getFormattedPath(path, formattedCode)
        }

        return path
    }

    private fun getMappedFilePath(
        path: String,
        formattedCode: String,
        languageMapping: Map<String, Map<String, String>>
    ): String {
        val startPatternIndex = path.indexOfFirst { it == '%' } + 1

        val endPatternIndex = path.indexOfLast { it == '%' }

        val pattern = path.substring(startPatternIndex, endPatternIndex)

        val mappingValue = languageMapping[formattedCode]?.getValue(pattern)

        if (mappingValue.isNullOrEmpty()) {
            return path
        }

        return StringBuilder()
            .append(path.substring(0, startPatternIndex - 1))
            .append(mappingValue)
            .append(path.substring(endPatternIndex + 1))
            .toString()
    }

    private fun containsLanguageMapping(
        path: String,
        formattedCode: String,
        languageMapping: Map<String, Map<String, String>>?
    ): Boolean {

        if (languageMapping == null) {
            return false
        }

        val pattern = path.substring(path.indexOfFirst { it == '%' } + 1,
            path.indexOfLast { it == '%' }
        )

        var result = false

        val mappingValue = languageMapping[formattedCode]?.getValue(pattern)

        if (!mappingValue.isNullOrEmpty()) {
            result = true
        }

        return result
    }

    private fun replacePatterns(
        filePath: String,
        name: String,
        twoLettersCode: String,
        threeLetterCode: String,
        locale: String,
        localeWithUnderscore: String,
        androidCode: String
    ): String {
        var path = filePath
        when {
            path.contains(LANGUAGE_NAME) -> path = path.replace(LANGUAGE_NAME, name)
            path.contains(TWO_LETTER_CODE) -> path =
                path.replace(TWO_LETTER_CODE, twoLettersCode)
            path.contains(THREE_LETTER_CODE) -> path =
                path.replace(THREE_LETTER_CODE, threeLetterCode)
            path.contains(LOCALE) -> path = path.replace(LOCALE, locale)
            path.contains(LOCALE_WITH_UNDERSCORE) -> path =
                path.replace(LOCALE_WITH_UNDERSCORE, localeWithUnderscore)
            path.contains(ANDROID_CODE) -> path = path.replace(ANDROID_CODE, androidCode)
        }

        return path
    }

    private fun getFormattedPath(path: String, formattedCode: String): String =
        if (path.startsWith("/")) {
            "/$formattedCode$path"
        } else {
            "/$formattedCode/$path"
        }

    private fun containsExportPattern(path: String): Boolean {
        for (element in listExportPattern) {
            if (path.contains(element)) {
                return true
            }
        }

        return false
    }
}
