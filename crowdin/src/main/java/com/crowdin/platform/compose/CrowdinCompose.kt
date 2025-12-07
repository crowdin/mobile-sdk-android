package com.crowdin.platform.compose

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
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
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun crowdinString(
    resourceId: Int,
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

    return remember(state.value, *formatArgs) {
        try {
            if (formatArgs.isNotEmpty()) {
                String.format(state.value, *formatArgs)
            } else {
                state.value
            }
        } catch (e: Exception) {
            Log.w(Crowdin.CROWDIN_TAG, "Failed to format string for resource $resourceId", e)
            state.value // Return unformatted string on error
        }
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
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun crowdinString(
    resourceId: Int,
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