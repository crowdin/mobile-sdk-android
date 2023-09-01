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
import java.net.HttpURLConnection
import okhttp3.ResponseBody
import retrofit2.Response

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

        getManifest(languageDataCallback) {
            onManifestDataReceived(it, languageDataCallback)
        }
    }

    @WorkerThread
    override fun onManifestDataReceived(manifest: ManifestData?, languageDataCallback: LanguageDataCallback?) {
        dataManager.saveData(MANIFEST_DATA, manifest)

        val languageData = LanguageData(sourceLanguage)
        crowdinLanguages = dataManager.getSupportedLanguages()
        manifest?.mapping?.forEach { filePath ->
            val eTag = eTagMap[filePath]
            val result = requestFileMapping(
                eTag = eTag,
                distributionHash = distributionHash,
                filePath = filePath,
                languageDataCallback = languageDataCallback
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
        var response: Response<ResponseBody>? = null

        executeIO {
            response = crowdinDistributionApi.getMappingFile(
                eTag = eTag ?: HEADER_ETAG_EMPTY,
                distributionHash = distributionHash,
                filePath = filePath
            ).execute()
        }

        val result = response ?: return languageData

        val body = result.body()
        val code = result.code()
        when {
            code == HttpURLConnection.HTTP_OK && body != null -> {
                languageData = onMappingReceived(
                    eTag = result.headers()[HEADER_ETAG],
                    filePath = filePath,
                    body = body,
                    languageDataCallback = languageDataCallback
                )
            }

            code != HttpURLConnection.HTTP_NOT_MODIFIED -> {
                languageDataCallback?.onFailure(Throwable("Unexpected http error code $code"))
                Log.d(
                    MappingRepository::class.java.simpleName,
                    "${Throwable("Unexpected http error code $code")}"
                )
            }

            else -> {}
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
