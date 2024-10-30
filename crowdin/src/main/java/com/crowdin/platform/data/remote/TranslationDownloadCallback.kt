package com.crowdin.platform.data.remote

/**
 * Translation download state.
 */
interface TranslationDownloadCallback {
    /**
     * Data loaded successfully.
     */
    fun onSuccess()

    /**
     * Unexpected error during data loading.
     *
     * @param throwable error during data update process.
     */
    fun onFailure(throwable: Throwable)
}
