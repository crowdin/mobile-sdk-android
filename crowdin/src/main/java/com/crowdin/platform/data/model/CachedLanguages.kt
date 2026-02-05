package com.crowdin.platform.data.model

internal data class CachedLanguages(
    val languages: SupportedLanguages,
    val manifestTimestamp: Long,
)
