package com.crowdin.platform.repository

internal class TextMetaData {

    var textAttributeKey: String = ""
    var hintAttributeKey: String = ""
    var textOnAttributeKey: String = ""
    var textOffAttributeKey: String = ""

    val hasAttributeKey: Boolean
        get() {
            return textAttributeKey.isNotEmpty()
        }

    val isArrayItem: Boolean
        get() {
            return arrayName != null && arrayName!!.isNotEmpty() && arrayIndex != -1
        }
    var arrayName: String? = ""
    var arrayIndex: Int = -1

    val isPluralData: Boolean
        get() {
            return pluralName != null && pluralName!!.isNotEmpty() && pluralQuantity != -1
        }
    var pluralName: String? = ""
    var pluralQuantity: Int = -1
    var pluralFormatArgs: Array<out Any?> = arrayOf()

    fun parseResult(resultData: SearchResultData) {
        when {
            resultData.hasKey -> textAttributeKey = resultData.key
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