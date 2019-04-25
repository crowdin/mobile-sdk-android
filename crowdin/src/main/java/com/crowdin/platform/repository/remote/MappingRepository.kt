package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.parser.Reader
import com.crowdin.platform.repository.remote.api.CrowdinApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.*

internal class MappingRepository(private val crowdinApi: CrowdinApi,
                                 private val reader: Reader,
                                 private val distributionKey: String?,
                                 private val filePaths: Array<out String>?) : BaseRepository() {

    override fun getMapping(mappingCallback: MappingCallback) {
        if (distributionKey == null) return

        filePaths?.forEach {
            val filePath = validateFilePath(it) + ".cids"
            val eTag = eTagMap[filePath]
            requestData(eTag, distributionKey, filePath, mappingCallback)
        }
    }

    private fun requestData(eTag: String?, distributionKey: String, filePath: String, mappingCallback: MappingCallback) {
        crowdinApi.getFileUpdates(eTag
                ?: HEADER_ETAG_EMPTY, distributionKey, filePath)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body()
                        when {
                            response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                                response.headers().get(HEADER_ETAG)?.let { eTag -> eTagMap.put(filePath, eTag) }
                                val languageData = reader.parseInput(body.byteStream())
                                languageData.language = Locale.getDefault().toString()

                                // TODO: update
                                mappingCallback.onSuccess()
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