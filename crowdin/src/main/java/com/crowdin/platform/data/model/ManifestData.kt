package com.crowdin.platform.data.model

data class ManifestData(
    val files: List<String>,
    val timestamp: Long,
    val languages: List<String>
)
