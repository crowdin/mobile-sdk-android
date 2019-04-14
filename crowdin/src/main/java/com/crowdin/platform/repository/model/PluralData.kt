package com.crowdin.platform.repository.model

internal class PluralData(var name: String = "",
                          var quantity: MutableMap<String, String> = mutableMapOf(),
                          var number: Int = -1,
                          var formatArgs: Array<out Any?> = arrayOf()) {

    fun updateResources(newPluralData: PluralData) {
        name = newPluralData.name
        quantity = newPluralData.quantity
        number = newPluralData.number
        formatArgs = newPluralData.formatArgs
    }
}
