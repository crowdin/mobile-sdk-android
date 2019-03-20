package com.crowdin.platform.repository.remote

import android.util.Log
import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.remote.api.CrowdinApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.concurrent.Executors


internal class DefaultRemoteRepository(private val crowdinApi: CrowdinApi,
                                       private val reader: XmlReader) : RemoteRepository {

    private val TAG: String = DefaultRemoteRepository::class.java.simpleName

    override fun fetchData(distributionKey: String?, currentLocale: String, filePath: String, languageDataCallback: LanguageDataCallback) {
        Executors.newSingleThreadExecutor().submit {
            crowdinApi.getFileUpdates(distributionKey, currentLocale, filePath)
                    .enqueue(object : Callback<ResponseBody> {

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val body = response.body()
                            if (response.code() == HttpURLConnection.HTTP_OK && body != null) {
                                val languageData = reader.parseInput(body.byteStream(), currentLocale)
                                if (languageData != null) {
                                    languageDataCallback.onDataLoaded(languageData)
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                            Log.d(TAG, "error: ${throwable.localizedMessage}")
                        }
                    })
        }
    }
}
