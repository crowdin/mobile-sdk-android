package com.crowdin.platform.data.model

internal data class ManifestData(
    val files: List<String>,
    val timestamp: Long,
    val languages: List<String>,
    val language_mapping: Map<String, Map<String, String>>
)
