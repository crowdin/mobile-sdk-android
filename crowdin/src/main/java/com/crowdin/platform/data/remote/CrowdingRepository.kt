package com.crowdin.platform.data.remote

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.google.gson.Gson
import java.net.HttpURLConnection
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal abstract class CrowdingRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val distributionHash: String
) : BaseRepository() {

    fun getManifest(languageDataCallback: LanguageDataCallback?) {
        crowdinDistributionApi.getResourceManifest(distributionHash)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val body = response.body()
                    when {
                        response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                            try {
                                val manifest =
                                    Gson().fromJson(body.string(), ManifestData::class.java)
                                onManifestDataReceived(manifest, languageDataCallback)
                            } catch (throwable: Throwable) {
                                languageDataCallback?.onFailure(throwable)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                    languageDataCallback?.onFailure(throwable)
                }
            })
    }

    abstract fun onManifestDataReceived(
        manifest: ManifestData,
        languageDataCallback: LanguageDataCallback?
    )
}
