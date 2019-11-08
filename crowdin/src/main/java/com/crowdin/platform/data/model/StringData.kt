package com.crowdin.platform.data.model

internal data class StringData(
    var stringKey: String = "",
    var stringValue: String = "",
    var formatArgs: Array<out Any?> = arrayOf(),
    var default: StringBuilder = StringBuilder()
) {

    fun updateResources(newStringData: StringData) {
        stringKey = newStringData.stringKey
        stringValue = newStringData.stringValue
        formatArgs = newStringData.formatArgs
        default = newStringData.default
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StringData

        if (stringKey != other.stringKey) return false
        if (stringValue != other.stringValue) return false
        if (!formatArgs.contentEquals(other.formatArgs)) return false
        if (default != other.default) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stringKey.hashCode()
        result = 31 * result + stringValue.hashCode()
        result = 31 * result + formatArgs.contentHashCode()
        result = 31 * result + default.hashCode()
        return result
    }
}