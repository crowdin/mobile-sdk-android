package com.crowdin.platform.data.model

import com.google.gson.annotations.SerializedName

internal data class LanguagesInfo(
    @SerializedName("data")
    val data: List<LanguageInfoData>
)

internal data class LanguageInfoData(
    @SerializedName("data")
    val data: LanguageInfo
)

internal data class LanguageInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("twoLettersCode")
    val twoLettersCode: String,
    @SerializedName("threeLettersCode")
    val threeLettersCode: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("androidCode")
    val androidCode: String
)
