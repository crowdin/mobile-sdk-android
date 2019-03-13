package com.crowdin.platform.repository.remote

import android.content.Context

import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.repository.LanguageDataCallback

/**
 * Repository of strings from network.
 */
internal interface RemoteRepository {

    /**
     * Save [LanguageData] for a specific language.
     * @param currentLocale        the default device Locale.
     * @param languageDataCallback delivers data back to caller.
     */
    fun fetchData(context: Context, currentLocale: String, languageDataCallback: LanguageDataCallback)
}
