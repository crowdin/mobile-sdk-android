// FILE: helpers.kt
package androidx.compose.ui.res

@Suppress("UNUSED_PARAMETER")
fun stringResource(id: Int, vararg formatArgs: Any): String {
    return "original"
}

// FILE: crowdinHelpers.kt
package com.crowdin.platform.compose

object R {
    object string {
        const val test_string = 1
    }
}

@Suppress("UNUSED_PARAMETER")
fun crowdinString(resourceId: Int, vararg formatArgs: Any): String {
    return "transformed"
}

// FILE: test.kt
package test.box

import androidx.compose.ui.res.stringResource
import com.crowdin.platform.compose.R

fun box(): String {
    // This stringResource call should be transformed to crowdinString by the compiler plugin
    val result = stringResource(R.string.test_string)

    // If transformation worked, result should be "transformed" not "original"
    return if (result == "transformed") "OK" else "FAIL: got $result"
}

