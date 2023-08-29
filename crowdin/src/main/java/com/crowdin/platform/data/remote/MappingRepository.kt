package com.crowdin.platform.data.remote

import android.util.Log
import androidx.annotation.WorkerThread
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.DataManager.Companion.MANIFEST_DATA
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.executeIO
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.HttpURLConnection

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

    override fun fetchData(
        languageCode: String?,
        supportedLanguages: LanguagesInfo?,
        languageDataCallback: LanguageDataCallback?
    ) {
        Log.v(Crowdin.CROWDIN_TAG, "MappingRepository. Fetch data from Api started")

        getManifest({
            onManifestDataReceived(it, languageDataCallback)
        }, languageDataCallback)
    }

    @WorkerThread
    override fun onManifestDataReceived(
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?
    ) {
        dataManager.saveData(MANIFEST_DATA, manifest)
        // Combine all data before save to storage
        val languageData = LanguageData(sourceLanguage)
        crowdinLanguages = dataManager.getSupportedLanguages()
        manifest?.mapping?.forEach { filePath ->
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
        var result: Response<ResponseBody>? = null

        executeIO {
            result = crowdinDistributionApi.getMappingFile(
                eTag ?: HEADER_ETAG_EMPTY,
                distributionHash,
                filePath
            ).execute()
        }

        result?.let {
            val body = it.body()
            val code = it.code()
            when {
                code == HttpURLConnection.HTTP_OK && body != null -> {
                    languageData = onMappingReceived(
                        it.headers()[HEADER_ETAG],
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
                else -> {
                }
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
        languageDataCallback?.onDataLoaded(languageData)

        return languageData
    }
}
