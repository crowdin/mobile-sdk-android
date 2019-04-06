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
                                       private val reader: Reader) : RemoteRepository {

    override fun fetchData(distributionKey: String?, currentLocale: String, filePath: String, languageDataCallback: LanguageDataCallback) {
        if (distributionKey == null) return

        crowdinApi.getFileUpdates(distributionKey, currentLocale, filePath)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()
                        if (response.code() == HttpURLConnection.HTTP_OK && body != null) {
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
