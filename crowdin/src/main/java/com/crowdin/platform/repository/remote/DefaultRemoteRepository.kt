package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.parser.Reader
import com.crowdin.platform.repository.remote.api.CrowdinApi
import com.crowdin.platform.utils.FilePathPlaceholder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*

internal class DefaultRemoteRepository(private val crowdinApi: CrowdinApi,
                                       private val reader: Reader,
                                       private val distributionKey: String?,
                                       private val filePaths: Array<out String>?) : RemoteRepository {

    companion object {
        private const val HEADER_ETAG = "ETag"
        private const val HEADER_ETAG_EMPTY = ""
    }

    private var eTagMap = mutableMapOf<String, String>()

    override fun fetchData(languageDataCallback: LanguageDataCallback) {
        if (distributionKey == null) return

        filePaths?.forEach {
            val filePath = validateFilePath(it)
            val eTag = eTagMap[filePath]
            crowdinApi.getFileUpdates(eTag ?: HEADER_ETAG_EMPTY, distributionKey, filePath)
                    .enqueue(object : Callback<ResponseBody> {

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val body = response.body()
                            val fileETag = response.headers().get(HEADER_ETAG)

                            if (response.code() == HttpURLConnection.HTTP_OK && body != null) {
                                if (fileETag != null) {
                                    eTagMap[filePath] = fileETag
                                }

                                val languageData = reader.parseInput(body.byteStream())
                                languageData.language = Locale.getDefault().language
                                languageDataCallback.onDataLoaded(languageData)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        }
                    })
        }
    }

    private fun validateFilePath(filePath: String): String {
        if (!filePath.contains("/")) {
            return "${FilePathPlaceholder.getLanguage()}/$filePath"
        }

        return filePath
    }
}
