package com.crowdin.platform.data.model

import com.google.gson.annotations.SerializedName

data class ManifestData(
    @SerializedName("files")
    val files: List<String>,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("languages")
    val languages: List<String>,

    @SerializedName("custom_languages")
    val customLanguages: Map<String, CustomLanguage>,

    @SerializedName("language_mapping")
    val languageMapping: Map<String, Map<String, String>>,

    @SerializedName("content")
    val content: Map<String, List<String>>,

    @SerializedName("mapping")
    val mapping: List<String>
)

data class CustomLanguage(
    @SerializedName("name")
    val name: String,
    @SerializedName("two_letters_code")
    val twoLettersCode: String,
    @SerializedName("three_letters_code")
    val threeLettersCode: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("android_code")
    val androidCode: String,
    @SerializedName("locale_with_underscore")
    val localeWithUnderscore: String
)
