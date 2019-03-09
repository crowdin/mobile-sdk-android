package com.crowdin.platform.api

internal data class ArrayData(val name: String?,
                              val values: Array<String>?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayData

        if (name != other.name) return false
        if (values != null) {
            if (other.values == null) return false
            if (!values.contentEquals(other.values)) return false
        } else if (other.values != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (values?.contentHashCode() ?: 0)
        return result
    }
}