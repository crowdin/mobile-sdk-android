package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.parser.Reader
import com.crowdin.platform.repository.remote.api.CrowdinApi
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
        private const val LOCALE = "%locale%"
        private const val LOCALE_WITH_UNDERSCORE = "%locale_with_underscore%"
        private const val ANDROID_CODE = "%android_code%"
    }

    private var eTagMap = mutableMapOf<String, String>()

    override fun fetchData(languageDataCallback: LanguageDataCallback) {
        if (distributionKey == null) return

        filePaths?.forEach {
            val filePath = validateFilePath(it)
            val eTag = eTagMap[filePath]
            requestData(eTag, distributionKey, filePath, languageDataCallback)
        }
    }

    private fun requestData(eTag: String?, distributionKey: String, filePath: String, languageDataCallback: LanguageDataCallback) {
        crowdinApi.getFileUpdates(eTag ?: HEADER_ETAG_EMPTY, distributionKey, filePath)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()
                        when {
                            response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                                response.headers().get(HEADER_ETAG)?.let { eTag -> eTagMap.put(filePath, eTag) }
                                val languageData = reader.parseInput(body.byteStream())
                                languageData.language = Locale.getDefault().toString()
                                languageDataCallback.onDataLoaded(languageData)
                                reader.close()
                            }
                            response.code() == HttpURLConnection.HTTP_NOT_MODIFIED -> languageDataCallback.onSuccess()
                            else -> languageDataCallback.onFailure(Throwable("Unexpected http error code ${response.code()}"))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                        languageDataCallback.onFailure(throwable)
                    }
                })
    }

    private fun validateFilePath(filePath: String): String {
        var path = filePath
        val locale = Locale.getDefault()
        val language = locale.language
        val country = locale.country

        when {
            path.contains(LOCALE) -> path = path.replace(LOCALE, "$language-$country")
            path.contains(LOCALE_WITH_UNDERSCORE) -> path = path.replace(LOCALE_WITH_UNDERSCORE, locale.toString())
            path.contains(ANDROID_CODE) -> path = path.replace(ANDROID_CODE, "$language-r$country")
        }

        if (!path.contains("/")) {
            return "/$language/$path"
        }

        return path
    }
}
