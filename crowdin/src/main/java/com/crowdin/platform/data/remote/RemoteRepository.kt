package com.crowdin.platform.data.remote

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData

/**
 * Repository of strings from network.
 */
internal interface RemoteRepository {

    /**
     * Save [LanguageData] for a specific language.
     *
     * @param languageDataCallback delivers data back to caller.
     */
    fun fetchData(languageDataCallback: LanguageDataCallback? = null)
}
