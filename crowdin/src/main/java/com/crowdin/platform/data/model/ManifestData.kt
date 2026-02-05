package com.crowdin.platform.data.model

import com.google.gson.annotations.SerializedName

data class ManifestData(
    @SerializedName("files")
    val files: List<String>,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("language_mapping")
    val languageMapping: Map<String, Map<String, String>>,
    @SerializedName("content")
    val content: Map<String, List<String>>,
    @SerializedName("mapping")
    val mapping: List<String>,
)
