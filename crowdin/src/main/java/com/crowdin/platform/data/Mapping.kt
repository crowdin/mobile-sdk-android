package com.crowdin.platform.data

import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.TextMetaData

internal fun getMappingValueForKey(
    textMetaData: TextMetaData,
    mappingData: LanguageData,
): Mapping {
    val resources = mappingData.resources
    val arrays = mappingData.arrays
    val plurals = mappingData.plurals

    when {
        textMetaData.hasAttributeKey || textMetaData.hasHintKey -> {
            for (resource in resources) {
                if (resource.stringKey == textMetaData.textAttributeKey) {
                    return Mapping(resource.stringValue)
                } else if (resource.stringKey == textMetaData.hintAttributeKey) {
                    return Mapping(resource.stringValue, true)
                }
            }
        }
        textMetaData.isArrayItem -> {
            for (array in arrays) {
                if (array.name == textMetaData.arrayName && textMetaData.isArrayItem) {
                    return Mapping(array.values!![textMetaData.arrayIndex])
                }
            }
        }
        textMetaData.isPluralData -> {
            for (plural in plurals) {
                if (plural.name == textMetaData.pluralName) {
                    try {
                        return Mapping(plural.quantity.values.first())
                    } catch (ex: NoSuchElementException) {
                        // element not found
                    }
                }
            }
        }
    }

    return Mapping(null)
}

data class Mapping(
    val value: String?,
    val isHint: Boolean = false,
)
