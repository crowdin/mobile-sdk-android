package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.LanguageDataCallback
import com.crowdin.platform.repository.parser.Reader
import com.crowdin.platform.repository.remote.api.CrowdinApi
import com.crowdin.platform.utils.ThreadUtils
import java.net.HttpURLConnection

internal class DefaultRemoteRepository(private val crowdinApi: CrowdinApi,
                                       private val reader: Reader) : RemoteRepository {

    override fun fetchData(distributionKey: String?, currentLocale: String, filePath: String, languageDataCallback: LanguageDataCallback) {
        ThreadUtils.runInBackgroundPool(Runnable {
            val response = crowdinApi.getFileUpdates(distributionKey, currentLocale, filePath).execute()
            val body = response.body()
            if (response.code() == HttpURLConnection.HTTP_OK && body != null) {
                val languageData = reader.parseInput(body.byteStream())
                languageData.language = currentLocale
                languageDataCallback.onDataLoaded(languageData)
            }
        }, false)
    }
}
