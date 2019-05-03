package com.crowdin.platform.data

import com.crowdin.platform.data.model.LanguageData

/**
 * Callback for transferring language data.
 * @see LanguageData
 */
internal interface LanguageDataCallback {

    fun onDataLoaded(languageData: LanguageData)

    fun onFailure(throwable: Throwable)
}