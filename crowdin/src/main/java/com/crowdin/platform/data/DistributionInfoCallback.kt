package com.crowdin.platform.data

internal interface DistributionInfoCallback {

    /**
     * Distribution info loaded successfully.
     */
    fun onSuccess()

    /**
     * Unexpected error during distribution info loading.
     *
     * @param throwable error during loading process.
     */
    fun onError(throwable: Throwable)
}
