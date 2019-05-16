package com.crowdin.platform.data

import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.TextMetaData

internal fun getMappingValueForKey(textMetaData: TextMetaData, mappingData: LanguageData): String? {
    val resources = mappingData.resources
    val arrays = mappingData.arrays
    val plurals = mappingData.plurals

    when {
        textMetaData.hasAttributeKey -> {
            for (resource in resources) {
                if (resource.stringKey == textMetaData.textAttributeKey) {
                    return resource.stringValue
                }
            }
        }
        textMetaData.isArrayItem -> {
            for (array in arrays) {
                if (array.name == textMetaData.arrayName && textMetaData.isArrayItem) {
                    return array.values!![textMetaData.arrayIndex]
                }
            }
        }
        textMetaData.isPluralData -> {
            for (plural in plurals) {
                if (plural.name == textMetaData.pluralName) {
                    try {
                        return plural.quantity.values.first()
                    } catch (ex: NoSuchElementException) {
                        // element not found
                    }
                }
            }
        }
    }

    return null
}