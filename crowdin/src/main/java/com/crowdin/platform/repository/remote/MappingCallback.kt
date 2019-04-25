package com.crowdin.platform.repository.remote

internal interface MappingCallback {

    fun onSuccess()

    fun onFailure(throwable: Throwable)
}
