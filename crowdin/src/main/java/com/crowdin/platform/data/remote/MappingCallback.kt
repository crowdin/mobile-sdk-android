package com.crowdin.platform.data.remote

import com.crowdin.platform.data.model.LanguageData

internal interface MappingCallback {

    fun onSuccess(languageData: LanguageData)

    fun onFailure(throwable: Throwable)
}
