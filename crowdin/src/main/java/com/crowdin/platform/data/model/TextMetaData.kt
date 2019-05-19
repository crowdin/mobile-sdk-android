package com.crowdin.platform.data.model

internal class TextMetaData {

    var textAttributeKey: String = ""
    var hintAttributeKey: String = ""
    var textOnAttributeKey: String = ""
    var textOffAttributeKey: String = ""
    var stringsFormatArgs: Array<out Any?> = arrayOf()
    var stringDefault: CharSequence = ""

    val hasAttributeKey: Boolean
        get() {
            return textAttributeKey.isNotEmpty()
        }

    val isArrayItem: Boolean
        get() {
            return arrayName.isNotEmpty() && arrayIndex != -1
        }

    var arrayName: String = ""
    var arrayIndex: Int = -1

    val isPluralData: Boolean
        get() {
            return pluralName.isNotEmpty() && pluralQuantity != -1
        }

    var pluralName: String = ""
    var pluralQuantity: Int = -1
    var pluralFormatArgs: Array<out Any?> = arrayOf()

    var mappingValue: String = ""

    fun parseResult(resultData: SearchResultData) {
        when {
            resultData.hasKey -> {
                textAttributeKey = resultData.stringKey
                stringsFormatArgs = resultData.stringsFormatArgs
                stringDefault = resultData.stringDefault
            }
            resultData.isArrayItem -> {
                arrayName = resultData.arrayName
                arrayIndex = resultData.arrayIndex
            }
            resultData.isPluralData -> {
                pluralName = resultData.pluralName
                pluralQuantity = resultData.pluralQuantity
                pluralFormatArgs = resultData.pluralFormatArgs
            }
        }
    }
}