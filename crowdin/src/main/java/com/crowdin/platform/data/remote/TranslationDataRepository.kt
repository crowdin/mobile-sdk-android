package com.crowdin.platform.data.remote

import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.BuildTranslationRequest
import com.crowdin.platform.data.model.FileResponse
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.Translation
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.data.remote.api.CrowdinTranslationApi
import com.crowdin.platform.data.remote.api.DistributionInfoResponse
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.executeIO
import com.crowdin.platform.util.getFormattedCode
import java.util.Locale
import okhttp3.ResponseBody

internal class TranslationDataRepository(
    crowdinDistributionApi: CrowdinDistributionApi,
    private val crowdinApi: CrowdinApi,
    private val crowdinTranslationApi: CrowdinTranslationApi,
    private val reader: Reader,
    private val dataManager: DataManager,
    distributionHash: String
) : CrowdingRepository(
    crowdinDistributionApi,
    crowdinApi,
    distributionHash
) {

    override fun fetchData(languageCode: String?, languageDataCallback: LanguageDataCallback?) {
        getManifest(languageDataCallback)
    }

    override fun onManifestDataReceived(
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?
    ) {
        dataManager.getData<DistributionInfoResponse.DistributionData>(
            DataManager.DISTRIBUTION_DATA,
            DistributionInfoResponse.DistributionData::class.java
        )?.project?.id?.let { manifest?.files?.let { files -> getFiles(it, files) } }
    }

    private fun getFiles(id: String, files: List<String>) {
        executeIO {
            crowdinApi.getFiles(id).execute().body()
                ?.let { onFilesReceived(files, it, id) }
        }
    }

    private fun onFilesReceived(files: List<String>, body: FileResponse, projectId: String) {
        val languageData = LanguageData(Locale.getDefault().getFormattedCode())

        files.forEach { file ->
            body.data.forEach {
                if ("/${it.data.name}" == file) {
                    val eTag = eTagMap[file]
                    val result = requestBuildTranslation(
                        eTag ?: HEADER_ETAG_EMPTY,
                        projectId,
                        it.data.id,
                        file
                    )

                    languageData.addNewResources(result)
                }
            }
        }

        ThreadUtils.executeOnMain { dataManager.refreshData(languageData) }
    }

    private fun requestBuildTranslation(
        eTag: String,
        projectId: String,
        stringId: Long,
        file: String
    ): LanguageData {
        var languageData = LanguageData()
        val formattedCode = Locale.getDefault().getFormattedCode()
        executeIO {
            crowdinApi.getTranslation(
                eTag,
                projectId,
                stringId,
                BuildTranslationRequest(formattedCode)
            ).execute().body()?.let {
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
