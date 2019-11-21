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

    fun parseResult(textMetaData: TextMetaData) {
        when {
            textMetaData.hasAttributeKey -> {
                textAttributeKey = textMetaData.textAttributeKey
                stringsFormatArgs = textMetaData.stringsFormatArgs
                stringDefault = textMetaData.stringDefault
            }
            textMetaData.isArrayItem -> {
                arrayName = textMetaData.arrayName
                arrayIndex = textMetaData.arrayIndex
            }
            textMetaData.isPluralData -> {
                pluralName = textMetaData.pluralName
                pluralQuantity = textMetaData.pluralQuantity
                pluralFormatArgs = textMetaData.pluralFormatArgs
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextMetaData

        if (textAttributeKey != other.textAttributeKey) return false
        if (hintAttributeKey != other.hintAttributeKey) return false
        if (textOnAttributeKey != other.textOnAttributeKey) return false
        if (textOffAttributeKey != other.textOffAttributeKey) return false
        if (!stringsFormatArgs.contentEquals(other.stringsFormatArgs)) return false
        if (stringDefault != other.stringDefault) return false
        if (arrayName != other.arrayName) return false
        if (arrayIndex != other.arrayIndex) return false
        if (pluralName != other.pluralName) return false
        if (pluralQuantity != other.pluralQuantity) return false
        if (!pluralFormatArgs.contentEquals(other.pluralFormatArgs)) return false
        if (mappingValue != other.mappingValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = textAttributeKey.hashCode()
        result = 31 * result + hintAttributeKey.hashCode()
        result = 31 * result + textOnAttributeKey.hashCode()
        result = 31 * result + textOffAttributeKey.hashCode()
        result = 31 * result + stringsFormatArgs.contentHashCode()
        result = 31 * result + stringDefault.hashCode()
        result = 31 * result + arrayName.hashCode()
        result = 31 * result + arrayIndex
        result = 31 * result + pluralName.hashCode()
        result = 31 * result + pluralQuantity
        result = 31 * result + pluralFormatArgs.contentHashCode()
        result = 31 * result + mappingValue.hashCode()
        return result
    }
}
