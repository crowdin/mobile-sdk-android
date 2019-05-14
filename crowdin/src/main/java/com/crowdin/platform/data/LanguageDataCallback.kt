package com.crowdin.platform.data

import com.crowdin.platform.data.model.LanguageData

/**
 * Callback for transferring language data.
 */
internal interface LanguageDataCallback {

    /**
     * Data loaded successfully.
     *
     * @see LanguageData
     */
    fun onDataLoaded(languageData: LanguageData)

    /**
     * Unexpected error during data loading.
     *
     * @param throwable error during data update process.
     */
    fun onFailure(throwable: Throwable)
}