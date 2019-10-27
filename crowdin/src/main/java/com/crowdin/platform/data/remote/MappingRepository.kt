package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

internal class MappingRepository(private val crowdinDistributionApi: CrowdinDistributionApi,
                                 private val reader: Reader,
                                 private val dataManager: DataManager,
                                 private val distributionHash: String,
                                 private val filePaths: Array<out String>?,
                                 private val sourceLanguage: String) : BaseRepository() {

    override fun fetchData(languageDataCallback: LanguageDataCallback?) {
        filePaths?.forEach {
            val filePath = validateFilePath(it)
                    .split("/")
                    .takeLast(1)
                    .run { "/$sourceLanguage/$it" }.toString()
            val eTag = eTagMap[filePath]
            requestData(eTag, distributionHash, filePath, languageDataCallback)
        }
    }

    private fun requestData(eTag: String?, distributionHash: String, filePath: String,
                            languageDataCallback: LanguageDataCallback?) {
        crowdinDistributionApi.getMappingFile(eTag ?: HEADER_ETAG_EMPTY, distributionHash, filePath)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()
                        when {
                            response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                                response.headers()[HEADER_ETAG]?.let { eTag -> eTagMap.put(filePath, eTag) }
                                val languageData =
                                        reader.parseInput(body.byteStream(), XmlPullParserFactory.newInstance())
                                languageData.language = sourceLanguage
                                reader.close()
                                dataManager.saveMapping(languageData)
                                languageDataCallback?.onDataLoaded(languageData)
                            }
                            response.code() != HttpURLConnection.HTTP_NOT_MODIFIED -> {
                                languageDataCallback?.onFailure(Throwable("Unexpected http error code ${response.code()}"))
                                Log.d(MappingRepository::class.java.simpleName,
                                        "${Throwable("Unexpected http error code ${response.code()}")}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                        languageDataCallback?.onFailure(throwable)
                        Log.d(MappingRepository::class.java.simpleName, throwable.localizedMessage)
                    }
                })
    }
}