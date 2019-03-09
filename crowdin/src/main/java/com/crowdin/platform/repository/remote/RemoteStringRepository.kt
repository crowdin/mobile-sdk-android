package com.crowdin.platform.repository.remote

import android.content.Context

import com.crowdin.platform.api.CrowdinApi
import com.crowdin.platform.repository.LanguageDataCallback

internal class RemoteStringRepository(private val crowdinApi: CrowdinApi) : RemoteRepository {

    override fun fetchData(context: Context, language: String, languageDataCallback: LanguageDataCallback) {
        // TODO: API call
        StringsLoaderTask(context, languageDataCallback).run()
    }
}
