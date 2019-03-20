package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.remote.api.LanguageData

/**
 * Repository of strings from network.
 */
internal interface RemoteRepository {

    /**
     * Save [LanguageData] for a specific language.
     *
     * @param distributionKey       the hash to identify distribution.
     * @param currentLocale         the default device Locale.
     * @param filePath              file path defined on Crowdin platform.
     * @param languageDataCallback  delivers data back to caller.
     */
    fun fetchData(distributionKey: String?, currentLocale: String, filePath: String, languageDataCallback: LanguageDataCallback)
}
