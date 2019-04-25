package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.model.LanguageData

/**
 * Repository of strings from network.
 */
internal interface RemoteRepository {

    /**
     * Save [LanguageData] for a specific language. Executes on a background thread.
     *
     * @param languageDataCallback  delivers data back to caller.
     */
    fun fetchData(languageDataCallback: LanguageDataCallback) {}
}
