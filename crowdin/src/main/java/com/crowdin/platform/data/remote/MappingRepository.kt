package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

internal class MappingRepository(private val crowdinDistributionApi: CrowdinDistributionApi,
                                 private val reader: Reader,
                                 private val stringDataManager: StringDataManager,
                                 private val distributionKey: String?,
                                 private val filePaths: Array<out String>?,
                                 private val sourceLanguage: String) : BaseRepository() {

    override fun getMapping() {
        if (distributionKey == null) return

        filePaths?.forEach {
            val filePath = validateFilePath(it)
                    .split("/")
                    .takeLast(1)
                    .run { "/$sourceLanguage/$it" }.toString()
            val eTag = eTagMap[filePath]
            requestData(eTag, distributionKey, filePath)
        }
    }

    private fun requestData(eTag: String?, distributionKey: String, filePath: String) {
        crowdinDistributionApi.getMappingFile(eTag ?: HEADER_ETAG_EMPTY, distributionKey, filePath)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()
                        when {
                            response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                                response.headers().get(HEADER_ETAG)?.let { eTag -> eTagMap.put(filePath, eTag) }
                                val languageData = reader.parseInput(body.byteStream())
                                languageData.language = sourceLanguage
                                reader.close()
                                stringDataManager.saveMapping(languageData)
                            }
                            response.code() != HttpURLConnection.HTTP_NOT_MODIFIED ->
                                Log.d(MappingRepository::class.java.simpleName,
                                        "${Throwable("Unexpected http error code ${response.code()}")}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                        Log.d(MappingRepository::class.java.simpleName, throwable.localizedMessage)
                    }
                })
    }
}