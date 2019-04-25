package com.crowdin.platform.repository.remote

import com.crowdin.platform.repository.model.LanguageData

internal interface MappingCallback {

    fun onSuccess(languageData: LanguageData)

    fun onFailure(throwable: Throwable)
}
