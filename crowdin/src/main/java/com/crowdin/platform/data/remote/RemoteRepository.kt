package com.crowdin.platform.data.remote

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.util.getFormattedCode
import java.util.Locale

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
        languageCode: String = Locale.getDefault().getFormattedCode(),
        languageDataCallback: LanguageDataCallback? = null
    )
}
