package com.crowdin.platform

/**
 * Provides information about loading state.
 */
interface LoadingStateListener {

    /**
     * Data from remote repository successfully loaded and stored in cache. It is safe
     * to use new resources after this event received.
     * Depending on file path count, this callback can be received few times for each successful
     * storage update.
     */
    fun onDataChanged()

    /**
     * Unexpected error during data loading.
     *
     * @param throwable error during data update process.
     */
    fun onFailure(throwable: Throwable)
}
