package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.toLanguageInfo
import com.crowdin.platform.data.parser.ReaderFactory
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.executeIO
import com.crowdin.platform.util.getMatchedCode
import java.net.HttpURLConnection
import okhttp3.ResponseBody
import retrofit2.Response

private const val XML_EXTENSION = ".xml"

internal class StringDataRemoteRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val distributionHash: String
) : CrowdingRepository(
    crowdinDistributionApi,
    distributionHash = distributionHash
) {

    private var preferredLanguageCode: String? = null

    override fun fetchData(
        languageCode: String?,
        supportedLanguages: LanguagesInfo?,
        languageDataCallback: LanguageDataCallback?
    ) {
        Log.v(Crowdin.CROWDIN_TAG, "StringDataRemoteRepository. Fetch data from Api started")

        preferredLanguageCode = languageCode
        crowdinLanguages = supportedLanguages
        getManifest({
            onManifestDataReceived(it, languageDataCallback)
        }, languageDataCallback)
    }

    override fun onManifestDataReceived(
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?
    ) {
        Log.v(
            Crowdin.CROWDIN_TAG,
            "StringDataRemoteRepository. Handling received manifest data. Preferred language: $preferredLanguageCode"
        )

        val supportedLanguages = manifest?.languages
        val customLanguages = manifest?.customLanguages
        if (preferredLanguageCode == null) {
            preferredLanguageCode = getMatchedCode(supportedLanguages, customLanguages)
            if (preferredLanguageCode == null) {
                languageDataCallback?.onFailure(Throwable("Can't find preferred Language"))
                return
            }
        } else {
            if (supportedLanguages?.contains(preferredLanguageCode) == false) {
                languageDataCallback?.onFailure(Throwable("Can't find preferred Language"))
                return
            }
        }

        // Combine all data before save to storage
        val languageData = LanguageData()
        val languageInfo = if (customLanguages?.contains(preferredLanguageCode) == true) {
            customLanguages[preferredLanguageCode]?.toLanguageInfo()
        } else {
            getLanguageInfo(preferredLanguageCode!!)
        }

        languageInfo ?: return

        languageData.language = languageInfo.locale
        manifest?.files?.forEach {
            val filePath = validateFilePath(it, languageInfo, preferredLanguageCode!!, manifest.languageMapping)
            val eTag = eTagMap[filePath]
            val result = requestStringData(
                eTag,
                distributionHash,
                filePath,
                manifest.timestamp,
                languageDataCallback
            )
            languageData.addNewResources(result)
        }

        languageDataCallback?.onDataLoaded(languageData)
    }

    private fun requestStringData(
        eTag: String?,
        distributionHash: String,
        filePath: String,
        timestamp: Long,
        languageDataCallback: LanguageDataCallback?
    ): LanguageData {
        var languageData = LanguageData()
        var result: Response<ResponseBody>? = null

        Log.v(
            Crowdin.CROWDIN_TAG,
            "${javaClass.simpleName}. Loading string data from $filePath"
        )

        executeIO {
            result = crowdinDistributionApi.getResourceFile(
                eTag ?: HEADER_ETAG_EMPTY,
                distributionHash,
                filePath,
                timestamp
            ).execute()
        }

        result?.let {
            val body = it.body()
            val code = it.code()
            when {
                code == HttpURLConnection.HTTP_OK && body != null -> {
                    languageData = onStringDataReceived(
                        it.headers()[HEADER_ETAG],
                        filePath,
                        body
                    )
                }
                code == HttpURLConnection.HTTP_FORBIDDEN -> {
                    val errorMessage =
                        "Translation file $filePath for locale $preferredLanguageCode not found in the distribution"
                    Log.e(Crowdin.CROWDIN_TAG, errorMessage)
                    languageDataCallback?.onFailure(Throwable(errorMessage))
                }
                code != HttpURLConnection.HTTP_NOT_MODIFIED -> {
                    Log.v(Crowdin.CROWDIN_TAG, "Not modified resourse file")
                    languageDataCallback?.onFailure(Throwable("Unexpected http error code $code"))
                }
                else -> {
                }
            }
        }

        return languageData
    }

    private fun onStringDataReceived(
        eTag: String?,
        filePath: String,
        body: ResponseBody
    ): LanguageData {
        eTag?.let { eTagMap.put(filePath, eTag) }

        val extension = if (filePath.contains(XML_EXTENSION)) {
            ReaderFactory.ReaderType.XML
        } else {
            ReaderFactory.ReaderType.JSON
        }
        val reader = ReaderFactory.createReader(extension)
        return reader.parseInput(body.byteStream())
    }
}
