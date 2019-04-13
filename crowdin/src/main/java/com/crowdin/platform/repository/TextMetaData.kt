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

    fun parseResult(resultData: SearchResultData) {
        when {
            resultData.hasKey -> textAttributeKey = resultData.key
            resultData.isArrayItem -> {
                arrayName = resultData.arrayName
                arrayIndex = resultData.arrayIndex
            }
        }
    }
}