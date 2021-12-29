package com.crowdin.platform.data.remote

import android.util.Log
import androidx.annotation.WorkerThread
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.BuildTranslationRequest
import com.crowdin.platform.data.model.FileResponse
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.Translation
import com.crowdin.platform.data.model.toLanguageInfo
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.data.remote.api.CrowdinTranslationApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.executeIO
import com.crowdin.platform.util.getMatchedCode
import okhttp3.ResponseBody

internal class TranslationDataRepository(
    crowdinDistributionApi: CrowdinDistributionApi,
    private val crowdinTranslationApi: CrowdinTranslationApi,
    private val reader: Reader,
    private val dataManager: DataManager,
    distributionHash: String
) : CrowdingRepository(
    crowdinDistributionApi,
    distributionHash
) {

    private var preferredLanguageCode: String? = null

    override fun fetchData(
        languageCode: String?,
        supportedLanguages: LanguagesInfo?,
        languageDataCallback: LanguageDataCallback?
    ) {
        Log.v(Crowdin.CROWDIN_TAG, "TranslationRepository. Fetch data from Api started")

        preferredLanguageCode = languageCode
        getManifest({
            onManifestDataReceived(it, languageDataCallback)
        }, languageDataCallback)
    }

    @WorkerThread
    override fun onManifestDataReceived(
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?
    ) {
        Log.v(Crowdin.CROWDIN_TAG, "Manifest data received")

        val supportedLanguages = manifest?.languages
        val customLanguages = manifest?.customLanguages
        if (preferredLanguageCode == null) {
            preferredLanguageCode = getMatchedCode(supportedLanguages, customLanguages) ?: return
        } else {
            if (supportedLanguages?.contains(preferredLanguageCode) == false) {
                return
            }
        }

        val languagesInfo = dataManager.getSupportedLanguages()
        crowdinLanguages = languagesInfo
        val languageInfo = if (customLanguages?.contains(preferredLanguageCode) == true) {
            customLanguages[preferredLanguageCode]?.toLanguageInfo()
        } else {
            getLanguageInfo(preferredLanguageCode!!)
        }

        languageInfo?.let { info ->
            dataManager.getData<DistributionInfoResponse.DistributionData>(
                DataManager.DISTRIBUTION_DATA,
                DistributionInfoResponse.DistributionData::class.java
            )?.project?.id?.let {
                manifest?.files?.let { files ->
                    getFiles(
                        it,
                        files,
                        info.locale,
                        languageDataCallback
                    )
                }
            }
        }
    }

    private fun getFiles(
        id: String,
        files: List<String>,
        locale: String,
        languageDataCallback: LanguageDataCallback?
    ) {
        executeIO {
            Log.v(Crowdin.CROWDIN_TAG, "Get files started from project id: $id")

            crowdinApi?.getFiles(id)?.execute()?.body()?.let {
                Log.v(Crowdin.CROWDIN_TAG, "Get files. Done.")
                onFilesReceived(files, it, id, locale, languageDataCallback)
            }
        }
    }

    private fun onFilesReceived(
        files: List<String>,
        body: FileResponse,
        projectId: String,
        locale: String,
        languageDataCallback: LanguageDataCallback? = null
    ) {
        val languageData = LanguageData(locale)
        loop@ for (file in files) {
            for (fileData in body.data) {
                if (fileData.data.path == file) {
                    val eTag = eTagMap[file]
                    val result = requestBuildTranslation(
                        eTag ?: HEADER_ETAG_EMPTY,
                        projectId,
                        fileData.data.id,
                        file
                    )

                    languageData.addNewResources(result)
                    continue@loop
                }
            }
        }

        ThreadUtils.executeOnMain {
            dataManager.refreshData(languageData)
            languageDataCallback?.onDataLoaded(languageData)
        }
    }

    private fun requestBuildTranslation(
        eTag: String,
        projectId: String,
        stringId: Long,
        file: String
    ): LanguageData {
        var languageData = LanguageData()
        executeIO {
            crowdinApi?.getTranslation(
                eTag,
                projectId,
                stringId,
                BuildTranslationRequest(preferredLanguageCode!!)
            )?.execute()?.body()?.let {
                languageData = onTranslationReceived(it.data, file)
            }
        }

        return languageData
    }

    private fun onTranslationReceived(translation: Translation, file: String): LanguageData {
        eTagMap[file] = translation.etag
        var languageData = LanguageData()
        executeIO {
            crowdinTranslationApi.getTranslationResource(translation.url).execute().body()?.let {
                languageData = onStringDataReceived(it)
            }
        }

        return languageData
    }

    private fun onStringDataReceived(body: ResponseBody): LanguageData {
        return reader.parseInput(body.byteStream())
    }
}
