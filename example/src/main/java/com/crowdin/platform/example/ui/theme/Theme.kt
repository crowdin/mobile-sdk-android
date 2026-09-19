package com.crowdin.platform.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColors =
    lightColorScheme(
        primary = LightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = LightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        secondary = LightSecondary,
        onSecondary = LightOnSecondary,
        secondaryContainer = LightSecondaryContainer,
        onSecondaryContainer = LightOnSecondaryContainer,
        tertiary = LightTertiary,
        onTertiary = LightOnTertiary,
        tertiaryContainer = LightTertiaryContainer,
        onTertiaryContainer = LightOnTertiaryContainer,
        error = LightError,
        onError = LightOnError,
        errorContainer = LightErrorContainer,
        onErrorContainer = LightOnErrorContainer,
        background = LightSurface,
        onBackground = LightOnSurface,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        surfaceDim = LightSurfaceDim,
        surfaceBright = LightSurfaceBright,
        surfaceContainerLowest = LightSurfaceContainerLowest,
        surfaceContainerLow = LightSurfaceContainerLow,
        surfaceContainer = LightSurfaceContainer,
        surfaceContainerHigh = LightSurfaceContainerHigh,
        surfaceContainerHighest = LightSurfaceContainerHighest,
        outline = LightOutline,
        outlineVariant = LightOutlineVariant,
        inverseSurface = LightInverseSurface,
        inverseOnSurface = LightInverseOnSurface,
        inversePrimary = LightInversePrimary,
    )

private val DarkColors =
    darkColorScheme(
        primary = DarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = DarkPrimaryContainer,
        onPrimaryContainer = DarkOnPrimaryContainer,
        secondary = DarkSecondary,
        onSecondary = DarkOnSecondary,
        secondaryContainer = DarkSecondaryContainer,
        onSecondaryContainer = DarkOnSecondaryContainer,
        tertiary = DarkTertiary,
        onTertiary = DarkOnTertiary,
        tertiaryContainer = DarkTertiaryContainer,
        onTertiaryContainer = DarkOnTertiaryContainer,
        error = DarkError,
        onError = DarkOnError,
        errorContainer = DarkErrorContainer,
        onErrorContainer = DarkOnErrorContainer,
        background = DarkSurface,
        onBackground = DarkOnSurface,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        surfaceDim = DarkSurfaceDim,
        surfaceBright = DarkSurfaceBright,
        surfaceContainerLowest = DarkSurfaceContainerLowest,
        surfaceContainerLow = DarkSurfaceContainerLow,
        surfaceContainer = DarkSurfaceContainer,
        surfaceContainerHigh = DarkSurfaceContainerHigh,
        surfaceContainerHighest = DarkSurfaceContainerHighest,
        outline = DarkOutline,
        outlineVariant = DarkOutlineVariant,
        inverseSurface = DarkInverseSurface,
        inverseOnSurface = DarkInverseOnSurface,
        inversePrimary = DarkInversePrimary,
    )

private val LocalCategoryTones = staticCompositionLocalOf { LightCategoryTones }

@Composable
fun CrowdinTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalCategoryTones provides if (darkTheme) DarkCategoryTones else LightCategoryTones,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            content = content,
        )
    }
}

/**
 * Stable color key for a category name. The same name always gets the same tone.
 */
@Composable
@ReadOnlyComposable
fun categoryTone(name: String): CategoryTone {
    val tones = LocalCategoryTones.current
    if (name.isEmpty()) return tones[0]
    // Plain hashCode() puts similar names in the same bucket ("Business", "Insurance" and
    // "Banking" all collide), so the hash is mixed before it is reduced to a tone index.
    val mixed = (name.hashCode().toLong() and 0xFFFFFFFFL) * 2654435761L
    return tones[((mixed shr 16) % tones.size).toInt()]
}
