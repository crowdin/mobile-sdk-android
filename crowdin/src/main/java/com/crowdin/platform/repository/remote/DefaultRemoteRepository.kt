package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.parser.Reader
import com.crowdin.platform.repository.remote.api.CrowdinApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

internal class DefaultRemoteRepository(private val crowdinApi: CrowdinApi,
                                       private val reader: Reader,
                                       private val distributionKey: String?,
                                       private val filePaths: Array<out String>?) : RemoteRepository {

    companion object {
        private const val HEADER_ETAG = "ETag"
        private const val HEADER_ETAG_EMPTY = ""
    }

    private var eTagMap = mutableMapOf<String, String>()

    override fun fetchData(currentLocale: String, languageDataCallback: LanguageDataCallback) {
        if (distributionKey == null) return

        filePaths?.forEach {
            val eTag = eTagMap[it]
            crowdinApi.getFileUpdates(eTag ?: HEADER_ETAG_EMPTY, distributionKey, currentLocale, it)
                    .enqueue(object : Callback<ResponseBody> {

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val body = response.body()
                            val fileETag = response.headers().get(HEADER_ETAG)

                            if (response.code() == HttpURLConnection.HTTP_OK && body != null) {
                                if (fileETag != null) {
                                    eTagMap[it] = fileETag
                                }

                                val languageData = reader.parseInput(body.byteStream())
                                languageData.language = currentLocale
                                languageDataCallback.onDataLoaded(languageData)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        }
                    })
        }
    }
}
