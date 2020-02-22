package com.crowdin.platform.data.model

data class TranslationResponse(
    val data: Translation
)

data class Translation(
    val url: String,
    val etag: String
)
