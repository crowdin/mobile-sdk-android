package com.crowdin.platform.data.remote

import android.content.res.Configuration
import android.util.Log
import com.crowdin.platform.Crowdin
import com.crowdin.platform.Preferences
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.CustomLanguage
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguagesInfo
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.model.SyncData
import com.crowdin.platform.data.model.toLanguageInfo
import com.crowdin.platform.data.parser.ReaderFactory
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.executeIO
import com.crowdin.platform.util.getLocale
import com.crowdin.platform.util.getMatchedCode
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.HttpURLConnection

private const val XML_EXTENSION = ".xml"

internal class StringDataRemoteRepository(
    private val crowdinPreferences: Preferences,
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val distributionHash: String,
) : CrowdingRepository(
        crowdinDistributionApi,
        distributionHash,
    ) {
    private var preferredLanguageCode: String? = null

    override fun fetchData(
        configuration: Configuration?,
        languageCode: String?,
        supportedLanguages: LanguagesInfo?,
        languageDataCallback: LanguageDataCallback?,
    ) {
        Log.v(Crowdin.CROWDIN_TAG, "StringDataRemoteRepository. Fetch data from Api started")

        preferredLanguageCode = languageCode
        crowdinLanguages = supportedLanguages

        getManifest(languageDataCallback) {
            val syncData = crowdinPreferences.getData<SyncData>(DataManager.SYNC_DATA, SyncData::class.java)
            val timestamp = syncData?.timestamp
            val language = configuration.getLocale().language
            if (timestamp == it.timestamp && language == syncData.languageCode) {
                crowdinPreferences.setLastUpdate(System.currentTimeMillis())
                languageDataCallback?.onFailure(Throwable("Data is up to date"))
                return@getManifest
            }

            crowdinPreferences.saveData(DataManager.SYNC_DATA, SyncData(it.timestamp, language))
            onManifestDataReceived(configuration, it, languageDataCallback)
        }
    }

    override fun onManifestDataReceived(
        configuration: Configuration?,
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?,
    ) {
        Log.v(
            Crowdin.CROWDIN_TAG,
            "StringDataRemoteRepository. Handling received manifest data. Preferred language: $preferredLanguageCode",
        )

        val supportedLanguages = manifest?.languages
        val customLanguages = manifest?.customLanguages
        val preferredLanguageCode = getSafeLanguageCode(configuration, preferredLanguageCode, supportedLanguages, customLanguages)
        if (preferredLanguageCode == null) {
            languageDataCallback?.onFailure(Throwable("Can't find preferred Language"))
            return
        }

        // Combine all data before save to storage
        val languageData = LanguageData()
        val languageInfo =
            if (customLanguages?.contains(preferredLanguageCode) == true) {
                customLanguages[preferredLanguageCode]?.toLanguageInfo()
            } else {
                getLanguageInfo(preferredLanguageCode)
            } ?: return

        languageData.language = languageInfo.locale
        manifest?.content?.get(preferredLanguageCode)?.forEach { filePath ->
            val eTag = eTagMap[filePath]
            val result =
                requestStringData(
                    eTag = eTag,
                    distributionHash = distributionHash,
                    filePath = filePath,
                    timestamp = manifest.timestamp,
                    languageDataCallback = languageDataCallback,
                )
            languageData.addNewResources(result)
        }

        languageDataCallback?.onDataLoaded(languageData)
    }

    private fun getSafeLanguageCode(
        configuration: Configuration?,
        preferredLanguageCode: String?,
        supportedLanguages: List<String>?,
        customLanguages: Map<String, CustomLanguage>?,
    ): String? =
        if (preferredLanguageCode == null) {
            getMatchedCode(configuration, supportedLanguages, customLanguages)
        } else {
            if (supportedLanguages?.contains(preferredLanguageCode) == false) {
                null
            } else {
                preferredLanguageCode
            }
        }

    private fun requestStringData(
        eTag: String?,
        distributionHash: String,
        filePath: String,
        timestamp: Long,
        languageDataCallback: LanguageDataCallback?,
    ): LanguageData {
        var languageData = LanguageData()
        var response: Response<ResponseBody>? = null

        Log.v(
            Crowdin.CROWDIN_TAG,
            "${javaClass.simpleName}. Loading string data from $filePath",
        )

        executeIO {
            response =
                crowdinDistributionApi
                    .getResourceFile(
                        eTag = eTag ?: HEADER_ETAG_EMPTY,
                        distributionHash = distributionHash,
                        filePath = filePath,
                        timeStamp = timestamp,
                    ).execute()
        }
        val result = response ?: return languageData

        val body = result.body()
        val code = result.code()
        when {
            code == HttpURLConnection.HTTP_OK && body != null -> {
                languageData =
                    onStringDataReceived(
                        eTag = result.headers()[HEADER_ETAG],
                        filePath = filePath,
                        body = body,
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

            else -> {}
        }

        return languageData
    }

    private fun onStringDataReceived(
        eTag: String?,
        filePath: String,
        body: ResponseBody,
    ): LanguageData {
        eTag?.let { eTagMap.put(filePath, eTag) }

        return ReaderFactory
            .createReader(
                when {
                    filePath.contains(XML_EXTENSION) -> ReaderFactory.ReaderType.XML
                    else -> ReaderFactory.ReaderType.JSON
                },
            ).parseInput(body.byteStream())
    }
}
