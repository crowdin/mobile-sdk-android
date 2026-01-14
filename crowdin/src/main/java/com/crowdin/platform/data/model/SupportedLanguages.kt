package com.crowdin.platform.data.model

import com.google.gson.annotations.SerializedName

/**
 * Map of supported languages from distribution API.
 * Key: language code (e.g., "de", "de-BE", "it", "tra")
 * Value: language details
 */
typealias SupportedLanguages = Map<String, LanguageDetails>

data class LanguageDetails(
    @SerializedName("name")
    val name: String,
    @SerializedName("locale")
    val locale: String,
)
