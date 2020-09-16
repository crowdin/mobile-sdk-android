package com.crowdin.platform.data

import androidx.annotation.WorkerThread
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
    @WorkerThread
    fun onDataLoaded(languageData: LanguageData)

    /**
     * Unexpected error during data loading.
     *
     * @param throwable error during data update process.
     */
    @WorkerThread
    fun onFailure(throwable: Throwable)
}
