package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.remote.api.LanguageData

/**
 * Repository of strings from network.
 */
internal interface RemoteRepository {

    /**
     * Save [LanguageData] for a specific language. Executes on a background thread.
     *
     * @param currentLocale         the default device Locale.
     * @param languageDataCallback  delivers data back to caller.
     */
    fun fetchData(currentLocale: String, languageDataCallback: LanguageDataCallback)
}
