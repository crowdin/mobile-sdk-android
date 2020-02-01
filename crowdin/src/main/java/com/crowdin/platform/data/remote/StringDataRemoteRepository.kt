package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.parser.Reader
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.ThreadUtils
import java.net.HttpURLConnection
import java.util.Locale
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParserFactory

internal class StringDataRemoteRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val reader: Reader,
    private val distributionHash: String
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
        ThreadUtils.runInBackgroundPool(Runnable {
            val languageData = LanguageData(Locale.getDefault().toString())

            manifest.files.forEach {
                val filePath = validateFilePath(it, Locale.getDefault())
                val eTag = eTagMap[filePath]
                val result = requestStringData(
                    eTag,
                    distributionHash,
                    filePath,
                    languageDataCallback
                )
                languageData.addNewResources(result)
            }

            ThreadUtils.executeOnMain { languageDataCallback?.onDataLoaded(languageData) }
        }, true)
    }

    private fun requestStringData(
        eTag: String?,
        distributionHash: String,
        filePath: String,
        languageDataCallback: LanguageDataCallback?
    ): LanguageData {
        var languageData = LanguageData()
        val result = crowdinDistributionApi.getResourceFile(
            eTag ?: HEADER_ETAG_EMPTY,
            distributionHash,
            filePath
        ).execute()
        val body = result.body()
        val code = result.code()
        when {
            code == HttpURLConnection.HTTP_OK && body != null -> {
                languageData = onStringDataReceived(
                    result.headers()[HEADER_ETAG],
                    filePath,
                    body
                )
            }
            code != HttpURLConnection.HTTP_NOT_MODIFIED ->
                languageDataCallback?.onFailure(Throwable("Unexpected http error code $code"))
        }
        result.errorBody()?.let {
            languageDataCallback?.onFailure(Throwable("Unexpected http error code $code"))
            Log.d(MappingRepository::class.java.simpleName, "Unexpected http error code $code")
        }

        return languageData
    }

    private fun onStringDataReceived(
        eTag: String?,
        filePath: String,
        body: ResponseBody
    ): LanguageData {
        eTag?.let { eTagMap.put(filePath, eTag) }

        val languageData = reader.parseInput(body.byteStream(), XmlPullParserFactory.newInstance())
        reader.close()

        return languageData
    }
}
