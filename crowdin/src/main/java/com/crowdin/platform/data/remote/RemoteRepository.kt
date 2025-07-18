package com.crowdin.platform.data.remote

import android.content.res.Configuration
import androidx.annotation.WorkerThread
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.TicketResponseBody

/**
 * Repository of strings from network.
 */
internal interface RemoteRepository {
    /**
     * Fetch [LanguageData] for a specific language by language code.
     *
     * @param configuration device configuration for locale context.
     * @param languageCode code to be user for search data.
     * @param languageDataCallback delivers data back to caller.
     */
    fun fetchData(
        configuration: Configuration? = null,
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

    /**
     * Fetch ticket for WebSocket connection.
     */
    @WorkerThread
    fun getTicket(event: String): TicketResponseBody?
}
