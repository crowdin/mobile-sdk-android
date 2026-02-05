package com.crowdin.platform.data.remote

import android.content.res.Configuration
import android.util.Log
import androidx.annotation.WorkerThread
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageDetails
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.SupportedLanguages
import com.crowdin.platform.data.model.TicketRequestBody
import com.crowdin.platform.data.model.TicketResponseBody
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.executeIO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

internal abstract class CrowdingRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val distributionHash: String,
) : BaseRepository() {
    var crowdinApi: CrowdinApi? = null
    var crowdinLanguages: SupportedLanguages? = null

    override fun getManifest(
        languageDataCallback: LanguageDataCallback?,
        function: (ManifestData) -> Unit,
    ) {
        Log.v(
            Crowdin.CROWDIN_TAG,
            "${javaClass.simpleName}. Loading resource manifest from Api started. Hash: $distributionHash",
        )

        crowdinDistributionApi
            .getResourceManifest(distributionHash)
            .enqueue(
                object : Callback<ManifestData> {
                    override fun onResponse(
                        call: Call<ManifestData>,
                        response: Response<ManifestData>,
                    ) {
                        Log.v(Crowdin.CROWDIN_TAG, "${javaClass.simpleName}. Manifest received. Body: ${response.body()}")

                        val body = response.body()
                        when {
                            response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                                try {
                                    ThreadUtils.runInBackgroundPool({
                                        synchronized(this) {
                                            function.invoke(body)
                                        }
                                    }, true)
                                } catch (throwable: Throwable) {
                                    languageDataCallback?.onFailure(throwable)
                                }
                            }

                            response.code() == HttpURLConnection.HTTP_FORBIDDEN -> {
                                languageDataCallback?.onFailure(
                                    Throwable("Unable to download translations from the distribution. Please check your distribution hash"),
                                )
                            }

                            else -> languageDataCallback?.onFailure(Throwable("Network operation failed ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<ManifestData>,
                        throwable: Throwable,
                    ) {
                        Log.e(Crowdin.CROWDIN_TAG, "Error while loading manifest", throwable)
                        languageDataCallback?.onFailure(throwable)
                    }
                },
            )
    }

    @WorkerThread
    abstract fun onManifestDataReceived(
        configuration: Configuration? = null,
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?,
    )

    override fun getSupportedLanguages(): SupportedLanguages? {
        Log.v(Crowdin.CROWDIN_TAG, "Getting supported languages from Api started")
        var languages: SupportedLanguages? = null
        executeIO {
            val response = crowdinDistributionApi.getLanguages(distributionHash)?.execute()?.body()
            if (response != null) {
                languages = response
            }
        }
        Log.v(Crowdin.CROWDIN_TAG, "Supported languages from Api: $languages")

        return languages
    }

    @WorkerThread
    override fun getTicket(event: String): TicketResponseBody? {
        var result: TicketResponseBody? = null
        executeIO { result = crowdinApi?.getTicket(TicketRequestBody(event))?.execute()?.body() }
        return result
    }

    fun getLanguageInfo(sourceLanguage: String): LanguageDetails? = crowdinLanguages?.get(sourceLanguage)
}
