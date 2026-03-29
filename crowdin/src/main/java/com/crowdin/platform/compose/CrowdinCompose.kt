package com.crowdin.platform.compose

import android.util.Log
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.crowdin.platform.Crowdin

/**
 * Retrieves a plain localized string with real-time updates and recomposition support.
 *
 * **Key Features:**
 * - **Real-time Updates**: Automatically recomposes when translations change via Crowdin platform
 * - **Plain Text**: Returns a simple String
 * - **Safe Fallback**: Falls back to standard `stringResource()` when Compose support is disabled
 * - **Memory Efficient**: Uses lifecycle-aware state management with automatic cleanup
 *
 * **When to Use:**
 * - Plain text that should update in real-time during development/testing
 * - UI elements like buttons, labels, titles that need live translation updates
 *
 * **Performance Notes:**
 * - Subscribes to real-time updates (causes recomposition on changes)
 * - Automatically registers/unregisters watchers based on composition lifecycle
 * - Zero overhead when Compose support is disabled (direct `stringResource()` call)
 *
 * @param resourceId The string resource ID from your app's resources
 * @param formatArgs Optional format arguments for String.format() style placeholders
 * @return The localized string as a plain String
 *
 */
@Composable
fun crowdinString(
    @StringRes resourceId: Int,
    vararg formatArgs: Any,
): String {
    // Use LocalConfiguration to respect composition-local overrides
    LocalConfiguration.current

    if (!Crowdin.isRealTimeComposeEnabled()) {
        return stringResource(resourceId, *formatArgs)
    }

    val repository =
        Crowdin.getComposeRepository()
            ?: return stringResource(resourceId, *formatArgs)

    val state = repository.getStringState(resourceId)

    DisposableEffect(resourceId) {
        repository.registerWatcher(resourceId)
        onDispose { repository.deRegisterWatcher(resourceId) }
    }

    return remember(state.value, resourceId, *formatArgs) {
        formatCrowdinText(state.value, resourceId, formatArgs)
    }
}

/**
 * Retrieves a plain localized string with real-time updates and recomposition support.
 *
 * **Key Features:**
 * - **Real-time Updates**: Automatically recomposes when translations change via Crowdin platform
 * - **Plain Text**: Returns a simple String
 * - **Safe Fallback**: Falls back to standard `stringResource()` when Compose support is disabled
 * - **Memory Efficient**: Uses lifecycle-aware state management with automatic cleanup
 *
 * **When to Use:**
 * - Plain text that should update in real-time during development/testing
 * - UI elements like buttons, labels, titles that need live translation updates
 *
 * **Performance Notes:**
 * - Subscribes to real-time updates (causes recomposition on changes)
 * - Automatically registers/unregisters watchers based on composition lifecycle
 * - Zero overhead when Compose support is disabled (direct `stringResource()` call)
 *
 * @param resourceId The string resource ID from your app's resources
 * @return The localized string as a plain String
 *
 */
@Composable
fun crowdinString(
    @StringRes resourceId: Int,
): String {
    // Use LocalConfiguration to respect composition-local overrides
    LocalConfiguration.current

    if (!Crowdin.isRealTimeComposeEnabled()) {
        return stringResource(resourceId)
    }

    val repository =
        Crowdin.getComposeRepository()
            ?: return stringResource(resourceId)

    val state = repository.getStringState(resourceId)

    DisposableEffect(resourceId) {
        repository.registerWatcher(resourceId)
        onDispose { repository.deRegisterWatcher(resourceId) }
    }

    return remember(state.value) { state.value }
}

/**
 * Retrieves a localized plural string with real-time updates and recomposition support.
 *
 * **Key Features:**
 * - **Real-time Updates**: Automatically recomposes when plural translations change via Crowdin
 * - **Plural-aware**: Resolves the correct plural form for the provided quantity
 * - **Safe Fallback**: Falls back to standard `pluralStringResource()` when Compose support is disabled
 * - **Memory Efficient**: Uses lifecycle-aware state management with automatic cleanup
 *
 * **When to Use:**
 * - Quantity-based UI text such as counters, badges, and summaries
 * - Pluralized strings that should update in real-time during development/testing
 *
 * @param resourceId The plural resource ID from your app's resources
 * @param quantity The quantity used to resolve the plural form
 * @param formatArgs Optional format arguments for String.format() style placeholders
 * @return The localized plural string as a plain String
 */
@Composable
fun crowdinPluralString(
    @PluralsRes resourceId: Int,
    quantity: Int,
    vararg formatArgs: Any,
): String {
    LocalConfiguration.current

    if (!Crowdin.isRealTimeComposeEnabled()) {
        return pluralStringResource(resourceId, quantity, *formatArgs)
    }

    val repository =
        Crowdin.getComposeRepository()
            ?: return pluralStringResource(resourceId, quantity, *formatArgs)

    val pluralForm = repository.getPluralForm(quantity)

    val state = repository.getPluralState(resourceId, quantity, pluralForm)

    DisposableEffect(resourceId) {
        repository.registerPluralResourceWatcher(resourceId)
        onDispose { repository.deRegisterPluralResourceWatcher(resourceId) }
    }

    return remember(state.value, resourceId, quantity, *formatArgs) {
        formatCrowdinText(state.value, resourceId, formatArgs)
    }
}

/**
 * Retrieves a localized plural string with real-time updates and recomposition support.
 *
 * @param resourceId The plural resource ID from your app's resources
 * @param quantity The quantity used to resolve the plural form
 * @return The localized plural string as a plain String
 */
@Composable
fun crowdinPluralString(
    @PluralsRes resourceId: Int,
    quantity: Int,
): String {
    LocalConfiguration.current

    if (!Crowdin.isRealTimeComposeEnabled()) {
        return pluralStringResource(resourceId, quantity)
    }

    val repository =
        Crowdin.getComposeRepository()
            ?: return pluralStringResource(resourceId, quantity)

    val pluralForm = repository.getPluralForm(quantity)

    val state = repository.getPluralState(resourceId, quantity, pluralForm)

    DisposableEffect(resourceId) {
        repository.registerPluralResourceWatcher(resourceId)
        onDispose { repository.deRegisterPluralResourceWatcher(resourceId) }
    }


    return remember(state.value, quantity) { state.value }
}

private fun formatCrowdinText(
    rawValue: String,
    resourceId: Int,
    formatArgs: Array<out Any>,
): String =
    try {
        if (formatArgs.isNotEmpty()) {
            String.format(rawValue, *formatArgs)
        } else {
            rawValue
        }
    } catch (e: Exception) {
        Log.w(Crowdin.CROWDIN_TAG, "Failed to format string for resource $resourceId", e)
        rawValue
    }
