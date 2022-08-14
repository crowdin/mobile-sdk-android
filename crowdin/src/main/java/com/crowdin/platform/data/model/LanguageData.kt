package com.crowdin.platform.data.model

import com.crowdin.platform.util.convertToJson

internal class LanguageData(var language: String) {

    constructor() : this("")

    var resources: MutableList<StringData> = mutableListOf()
    var arrays: MutableList<ArrayData> = mutableListOf()
    var plurals: MutableList<PluralData> = mutableListOf()

    fun updateResources(languageData: LanguageData) {
        if (languageData.resources.isNotEmpty()) resources = languageData.resources
        if (languageData.arrays.isNotEmpty()) arrays = languageData.arrays
        if (languageData.plurals.isNotEmpty()) plurals = languageData.plurals
    }

    fun addNewResources(languageData: LanguageData) {
        resources.addAll(languageData.resources)
        arrays.addAll(languageData.arrays)
        plurals.addAll(languageData.plurals)
    }

    override fun toString(): String {
        return convertToJson(this)
    }
}
