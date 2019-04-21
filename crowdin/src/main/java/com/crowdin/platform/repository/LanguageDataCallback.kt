package com.crowdin.platform.repository

import com.crowdin.platform.repository.model.LanguageData

/**
 * Callback for transferring language data.
 * @see LanguageData
 */
internal interface LanguageDataCallback {

    fun onDataLoaded(languageData: LanguageData)

    fun onFailure(throwable: Throwable)
}