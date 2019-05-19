package com.crowdin.platform.data

internal interface DistributionInfoCallback {

    fun onSuccess()
    fun onError(throwable: Throwable)
}
