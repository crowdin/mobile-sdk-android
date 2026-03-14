// FILE: helpers.kt
package androidx.compose.ui.res

@Suppress("UNUSED_PARAMETER")
fun pluralStringResource(id: Int, count: Int): String {
    return "original"
}

@Suppress("UNUSED_PARAMETER")
fun pluralStringResource(id: Int, count: Int, vararg formatArgs: Any): String {
    return "original-formatted"
}

// FILE: crowdinHelpers.kt
package com.crowdin.platform.compose

object R {
    object plurals {
        const val task_count = 1
    }
}

@Suppress("UNUSED_PARAMETER")
fun crowdinPluralString(resourceId: Int, quantity: Int): String {
    return "transformed"
}

@Suppress("UNUSED_PARAMETER")
fun crowdinPluralString(resourceId: Int, quantity: Int, vararg formatArgs: Any): String {
    return "transformed-formatted"
}

// FILE: test.kt
package test.box

import androidx.compose.ui.res.pluralStringResource
import com.crowdin.platform.compose.R

fun box(): String {
    val plainResult = pluralStringResource(R.plurals.task_count, 2)
    if (plainResult != "transformed") {
        return "FAIL plain: got $plainResult"
    }

    val formattedResult = pluralStringResource(R.plurals.task_count, 2, 2)
    return if (formattedResult == "transformed-formatted") "OK" else "FAIL formatted: got $formattedResult"
}

