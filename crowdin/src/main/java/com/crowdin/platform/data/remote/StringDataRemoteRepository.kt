package com.crowdin.platform.data.remote

import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*

internal class StringDataRemoteRepository(private val crowdinDistributionApi: CrowdinDistributionApi,
                                          private val reader: Reader,
                                          private val distributionHash: String,
                                          private val filePaths: Array<out String>?) : BaseRepository() {

    override fun fetchData(languageDataCallback: LanguageDataCallback?) {
        filePaths?.forEach {
            val filePath = validateFilePath(it)
            val eTag = eTagMap[filePath]
            requestData(eTag, distributionHash, filePath, languageDataCallback)
        }
    }

    private fun requestData(eTag: String?, distributionHash: String, filePath: String,
                            languageDataCallback: LanguageDataCallback?) {
        crowdinDistributionApi.getResourceFile(eTag
                ?: HEADER_ETAG_EMPTY, distributionHash, filePath)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()
                        when {
                            response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                                response.headers()[HEADER_ETAG]?.let { eTag -> eTagMap.put(filePath, eTag) }
                                val languageData = reader.parseInput(body.byteStream())
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
