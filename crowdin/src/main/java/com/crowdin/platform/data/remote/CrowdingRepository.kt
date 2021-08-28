package com.crowdin.platform.data.remote

import android.util.Log
import androidx.annotation.WorkerThread
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageInfo
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.executeIO
import java.net.HttpURLConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal abstract class CrowdingRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val distributionHash: String
) : BaseRepository() {

    var crowdinApi: CrowdinApi? = null
    var crowdinLanguages: LanguagesInfo? = null

    override fun getManifest(
        function: (ManifestData) -> Unit,
        languageDataCallback: LanguageDataCallback?
    ) {

        Log.v(
            Crowdin.CROWDIN_TAG,
            "${javaClass.simpleName}. Loading resource manifest from Api started. Hash: $distributionHash"
        )

        crowdinDistributionApi.getResourceManifest(distributionHash)
            .enqueue(object : Callback<ManifestData> {

                override fun onResponse(
                    call: Call<ManifestData>,
                    response: Response<ManifestData>
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
                            languageDataCallback?.onFailure(Throwable("Unable to download translations from the distribution. Please check your distribution hash"))
                        }
                        else -> {
                            languageDataCallback?.onFailure(Throwable("Network operation failed ${response.code()}"))
                        }
                    }
                }

                override fun onFailure(call: Call<ManifestData>, throwable: Throwable) {
                    Log.e(Crowdin.CROWDIN_TAG, "Error while loading manifest", throwable)
                    languageDataCallback?.onFailure(throwable)
                }
            })
    }

    @WorkerThread
    abstract fun onManifestDataReceived(
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?
    )

    override fun getSupportedLanguages(): LanguagesInfo? {
        Log.v(Crowdin.CROWDIN_TAG, "Getting supported languages from Api started")

        var info: LanguagesInfo? = null
        executeIO { info = crowdinApi?.getLanguagesInfo()?.execute()?.body() }

        Log.v(Crowdin.CROWDIN_TAG, "Supported languages from Api: $info")

        return info
    }

    fun getLanguageInfo(sourceLanguage: String): LanguageInfo? {
        crowdinLanguages?.data?.forEach {
            val languageInfo = it.data
            if (languageInfo.id == sourceLanguage) {
                return languageInfo
            }
        }

        return null
    }
}
