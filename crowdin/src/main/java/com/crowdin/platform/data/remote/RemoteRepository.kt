package com.crowdin.platform.data.remote

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData

/**
 * Repository of strings from network.
 */
internal interface RemoteRepository {

    /**
     * Fetch [LanguageData] for a specific language by language code.
     *
     * @param languageCode code to be user for search data.
     * @param languageDataCallback delivers data back to caller.
     */
    fun fetchData(
        languageCode: String? = null,
        languageDataCallback: LanguageDataCallback? = null
    )
}
