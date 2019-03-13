package com.crowdin.platform.repository

import com.crowdin.platform.repository.remote.api.LanguageData

internal interface LanguageDataCallback {

    fun onDataLoaded(languageData: LanguageData)
}