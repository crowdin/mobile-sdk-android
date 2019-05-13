package com.crowdin.platform.data.remote

import java.util.*

internal open class BaseRepository : CrowdinRepository {

    internal companion object {
        const val HEADER_ETAG = "ETag"
        const val HEADER_ETAG_EMPTY = ""
        const val LOCALE = "%locale%"
        const val LOCALE_WITH_UNDERSCORE = "%locale_with_underscore%"
        const val ANDROID_CODE = "%android_code%"
    }

    protected var eTagMap = mutableMapOf<String, String>()

    protected fun validateFilePath(filePath: String): String {
        var path = filePath
        val locale = Locale.getDefault()
        val language = locale.language
        val country = locale.country

        when {
            path.contains(LOCALE) -> path = path.replace(LOCALE, "$language-$country")
            path.contains(LOCALE_WITH_UNDERSCORE) -> path = path.replace(LOCALE_WITH_UNDERSCORE, locale.toString())
            path.contains(ANDROID_CODE) -> path = path.replace(ANDROID_CODE, "$language-r$country")
        }

        if (!path.contains("/")) {
            return "/$language/$path"
        }

        return path
    }
}