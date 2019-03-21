package com.crowdin.platform.repository.remote.api

internal class LanguageData(var language: String) {
    constructor() : this("")

    var resources: Map<String, String> = mutableMapOf()
    var arrays: List<ArrayData> = mutableListOf()
    var plurals: List<PluralData> = mutableListOf()

    fun updateResources(languageData: LanguageData) {
        when {
            !languageData.resources.isEmpty() -> resources = languageData.resources
            !languageData.arrays.isEmpty() -> arrays = languageData.arrays
            !languageData.plurals.isEmpty() -> plurals = languageData.plurals
        }
    }
}
