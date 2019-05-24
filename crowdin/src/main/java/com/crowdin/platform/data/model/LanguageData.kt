package com.crowdin.platform.data.model

internal class LanguageData(var language: String) {

    constructor() : this("")

    var resources: MutableList<StringData> = mutableListOf()
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
