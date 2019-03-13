package com.crowdin.platform.repository.remote.api

import com.google.gson.annotations.SerializedName

internal class LanguageData(val language: String) {

    val version: Int = 0
    @SerializedName("app_version")
    val appVersion: Int = 0
    lateinit var resources: Map<String, String>
    lateinit var arrays: List<ArrayData>
    lateinit var plurals: List<PluralData>
}
