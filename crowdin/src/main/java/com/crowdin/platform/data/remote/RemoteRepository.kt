package com.crowdin.platform.data.remote

import androidx.annotation.WorkerThread
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData

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
        supportedLanguages: LanguagesInfo? = null,
        languageDataCallback: LanguageDataCallback? = null,
    )

    fun getManifest(
        languageDataCallback: LanguageDataCallback? = null,
        function: (ManifestData) -> Unit,
    )

    /**
     * Fetch all supported languages.
     */
    @WorkerThread
    fun getSupportedLanguages(): LanguagesInfo?
}
