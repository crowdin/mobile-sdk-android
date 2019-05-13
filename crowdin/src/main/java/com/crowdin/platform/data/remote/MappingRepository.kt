package com.crowdin.platform.data.remote

import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*

internal class MappingRepository(private val crowdinDistributionApi: CrowdinDistributionApi,
                                 private val reader: Reader,
                                 private val distributionKey: String?,
                                 private val filePaths: Array<out String>?) : BaseRepository() {

    override fun getMapping(sourceLanguage: String, mappingCallback: MappingCallback) {
        if (distributionKey == null) return

        filePaths?.forEach {
            val filePath = validateFilePath(it)
                    .split("/")
                    .takeLast(1)
                    .run { "/$sourceLanguage/$it" }.toString()
            val eTag = eTagMap[filePath]
            requestData(eTag, distributionKey, filePath, mappingCallback)
        }
    }

    private fun requestData(eTag: String?, distributionKey: String, filePath: String, mappingCallback: MappingCallback) {
        crowdinDistributionApi.getMappingFile(eTag ?: HEADER_ETAG_EMPTY, distributionKey, filePath)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()
                        when {
                            response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                                response.headers().get(HEADER_ETAG)?.let { eTag -> eTagMap.put(filePath, eTag) }
                                val languageData = reader.parseInput(body.byteStream())
                                languageData.language = Locale.getDefault().toString() + StringDataManager.SUF_MAPPING

                                mappingCallback.onSuccess(languageData)
                                reader.close()
                            }
                            response.code() != HttpURLConnection.HTTP_NOT_MODIFIED ->
                                mappingCallback.onFailure(Throwable("Unexpected http error code ${response.code()}"))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                        mappingCallback.onFailure(throwable)
                    }
                })
    }
}