package com.crowdin.platform.data

internal interface DistributionInfoCallback {

    /**
     * Distribution info response received.
     */
    fun onResponse()

    /**
     * Unexpected error during distribution info loading.
     *
     * @param throwable error during loading process.
     */
    fun onError(throwable: Throwable)
}
