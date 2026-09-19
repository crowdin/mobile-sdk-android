package com.crowdin.platform.example.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.crowdin.platform.compose.crowdinString

/**
 * Bumped by [com.crowdin.platform.example.MainActivity] whenever the SDK reports that new
 * translations have been downloaded. Reading it inside [localizedString] makes every piece of
 * text in the app recompose with the freshly delivered translation.
 */
val LocalTranslationsVersion = compositionLocalOf { 0 }

/**
 * Resolves a string through the Crowdin SDK.
 *
 * Use this instead of `stringResource` everywhere in the app:
 * - with real-time preview enabled, `crowdinString` re-renders the text as it is typed
 *   in the Crowdin Editor;
 * - after an over-the-air update, [LocalTranslationsVersion] triggers the recomposition.
 */
@Composable
fun localizedString(
    @StringRes id: Int,
): String {
    LocalTranslationsVersion.current
    return crowdinString(id)
}

@Composable
fun localizedString(
    @StringRes id: Int,
    vararg formatArgs: Any,
): String {
    LocalTranslationsVersion.current
    return crowdinString(id, *formatArgs)
}
