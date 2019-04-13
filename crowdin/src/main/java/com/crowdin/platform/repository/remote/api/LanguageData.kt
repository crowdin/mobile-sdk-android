package com.crowdin.platform.repository.remote.api

internal class LanguageData(var language: String) {
    constructor() : this("")

    var resources: MutableMap<String, String> = mutableMapOf()
    var arrays: MutableList<ArrayData> = mutableListOf()
    var plurals: MutableList<PluralData> = mutableListOf()

    fun updateResources(languageData: LanguageData) {
        when {
            languageData.resources.isNotEmpty() -> resources = languageData.resources
            languageData.arrays.isNotEmpty() -> arrays = languageData.arrays
            languageData.plurals.isNotEmpty() -> plurals = languageData.plurals
        }
    }
}
