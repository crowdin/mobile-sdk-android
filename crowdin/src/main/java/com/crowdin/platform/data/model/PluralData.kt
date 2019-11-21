package com.crowdin.platform.data.model

internal data class PluralData(
    var name: String = "",
    var quantity: MutableMap<String, String> = mutableMapOf(),
    var number: Int = -1,
    var formatArgs: Array<out Any?> = arrayOf()
) {

    fun updateResources(newPluralData: PluralData) {
        name = newPluralData.name
        quantity = newPluralData.quantity
        number = newPluralData.number
        formatArgs = newPluralData.formatArgs
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PluralData

        if (name != other.name) return false
        if (quantity != other.quantity) return false
        if (number != other.number) return false
        if (!formatArgs.contentEquals(other.formatArgs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + quantity.hashCode()
        result = 31 * result + number
        result = 31 * result + formatArgs.contentHashCode()
        return result
    }
}
