package com.crowdin.platform.util

import com.crowdin.platform.data.model.LanguageData
import com.google.gson.Gson

internal fun convertToJson(languageData: LanguageData): String {
    val language = languageData.language
    val stringMap = mutableMapOf<String, String>()
    languageData.resources.forEach { stringMap[it.stringKey] = it.stringValue }
    val arraysMap = mutableMapOf<String, Array<String>>()
    languageData.arrays.forEach { arraysMap[it.name] = it.values!! }
    val pluralMap = mutableMapOf<String, MutableMap<String, String>>()
    languageData.plurals.forEach { pluralMap[it.name] = it.quantity }

    return Gson().toJson(ResourcesData(language, stringMap, arraysMap, pluralMap))
}

data class ResourcesData(
    val language: String,
    val strings: MutableMap<String, String>,
    val arrays: MutableMap<String, Array<String>>,
    val plurals: MutableMap<String, MutableMap<String, String>>,
)
