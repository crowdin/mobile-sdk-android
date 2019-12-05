package com.crowdin.platform.data.remote

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.util.Locale
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class StringDataRemoteRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val reader: Reader,
    private val distributionHash: String
) : BaseRepository() {

    override fun fetchData(languageDataCallback: LanguageDataCallback?) {
        getManifest(languageDataCallback)
    }

    private fun getManifest(languageDataCallback: LanguageDataCallback?) {
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
                                manifest.files.forEach {
                                    val filePath = validateFilePath(it, Locale.getDefault())
                                    val eTag = eTagMap[filePath]
                                    requestData(
                                        eTag,
                                        distributionHash,
                                        filePath,
                                        languageDataCallback
                                    )
                                }
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

    private fun requestData(
        eTag: String?,
        distributionHash: String,
        filePath: String,
        languageDataCallback: LanguageDataCallback?
    ) {
        crowdinDistributionApi.getResourceFile(
            eTag ?: HEADER_ETAG_EMPTY,
            distributionHash,
            filePath
        ).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                when {
                    response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                        response.headers()[HEADER_ETAG]?.let { eTag -> eTagMap.put(filePath, eTag) }
                        val languageData =
                            reader.parseInput(body.byteStream(), XmlPullParserFactory.newInstance())
                        languageData.language = Locale.getDefault().toString()
                        languageDataCallback?.onDataLoaded(languageData)
                        reader.close()
                    }
                    response.code() != HttpURLConnection.HTTP_NOT_MODIFIED ->
                        languageDataCallback?.onFailure(Throwable("Unexpected http error code ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                languageDataCallback?.onFailure(throwable)
            }
        })
    }
}
