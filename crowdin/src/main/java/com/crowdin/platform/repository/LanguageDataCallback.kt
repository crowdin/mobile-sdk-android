package com.crowdin.platform.repository

import com.crowdin.platform.api.LanguageData

internal interface LanguageDataCallback {

    fun onDataLoaded(languageData: LanguageData)
}