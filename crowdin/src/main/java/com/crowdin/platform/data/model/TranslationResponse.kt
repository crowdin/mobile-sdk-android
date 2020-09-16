package com.crowdin.platform.data.model

internal data class TranslationResponse(
    val data: Translation
)

internal data class Translation(
    val url: String,
    val etag: String
)
