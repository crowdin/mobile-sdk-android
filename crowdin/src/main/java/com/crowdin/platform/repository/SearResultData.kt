package com.crowdin.platform.repository

/**
 * Stores data related to text founded in local repository
 */
internal class SearchResultData {

    val hasKey: Boolean
        get() {
            return key.isNotEmpty()
        }
    var key: String = ""

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
}