package com.crowdin.platform.repository.model

import java.lang.StringBuilder

internal class StringData(
        var stringKey: String = "",
        var stringValue: String = "",
        var formatArgs: Array<out Any?> = arrayOf(),
        var def: StringBuilder = StringBuilder()) {

    fun updateResources(newStringData: StringData) {
        stringKey = newStringData.stringKey
        stringValue = newStringData.stringValue
        formatArgs = newStringData.formatArgs
        def = newStringData.def
    }
}