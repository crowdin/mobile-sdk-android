package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.ThreadUtils
import java.net.HttpURLConnection
import java.util.Locale
import okhttp3.ResponseBody

internal class MappingRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val reader: Reader,
    private val dataManager: DataManager,
    private val distributionHash: String,
    private val sourceLanguage: String
) : CrowdingRepository(
    crowdinDistributionApi,
    distributionHash
) {

    override fun fetchData(languageDataCallback: LanguageDataCallback?) {
        getManifest(languageDataCallback)
    }

    override fun onManifestDataReceived(
        manifest: ManifestData,
        languageDataCallback: LanguageDataCallback?
    ) {
        // Combine all data before save to storage
        val languageData = LanguageData(sourceLanguage)

        manifest.files.forEach {
            val filePath = validateFilePath(it, Locale(sourceLanguage))
            val eTag = eTagMap[filePath]

            val result = requestFileMapping(
                eTag,
                distributionHash,
                filePath,
                languageDataCallback
            )
            languageData.addNewResources(result)
        }

        dataManager.saveMapping(languageData)
    }

    private fun requestFileMapping(
        eTag: String?,
        distributionHash: String,
        filePath: String,
        languageDataCallback: LanguageDataCallback?
    ): LanguageData {
        var languageData = LanguageData()
        val result = crowdinDistributionApi.getMappingFile(
            eTag ?: HEADER_ETAG_EMPTY,
            distributionHash,
            filePath
        ).execute()
        val body = result.body()
        val code = result.code()
        when {
            code == HttpURLConnection.HTTP_OK && body != null -> {
                languageData = onMappingReceived(
                    result.headers()[HEADER_ETAG],
                    filePath,
                    body,
                    languageDataCallback
                )
            }
            code != HttpURLConnection.HTTP_NOT_MODIFIED -> {
                languageDataCallback?.onFailure(Throwable("Unexpected http error code $code"))
                Log.d(
                    MappingRepository::class.java.simpleName,
                    "${Throwable("Unexpected http error code $code")}"
                )
            }
        }

        return languageData
    }

    private fun onMappingReceived(
        eTag: String?,
        filePath: String,
        body: ResponseBody,
        languageDataCallback: LanguageDataCallback?
    ): LanguageData {
        eTag?.let { eTagMap.put(filePath, eTag) }

        val languageData = reader.parseInput(body.byteStream())
        ThreadUtils.executeOnMain { languageDataCallback?.onDataLoaded(languageData) }

        return languageData
    }
}
