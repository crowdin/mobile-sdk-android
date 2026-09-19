package com.crowdin.platform.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Material 3 color roles generated from the Crowdin green seed (#1B873F).
 * Every color in the app comes from these roles - no raw hex values in the UI code.
 */

// Light scheme
internal val LightPrimary = Color(0xFF2A6B43)
internal val LightOnPrimary = Color(0xFFFFFFFF)
internal val LightPrimaryContainer = Color(0xFFADF2BF)
internal val LightOnPrimaryContainer = Color(0xFF00210F)
internal val LightSecondary = Color(0xFF4F6353)
internal val LightOnSecondary = Color(0xFFFFFFFF)
internal val LightSecondaryContainer = Color(0xFFD2E8D4)
internal val LightOnSecondaryContainer = Color(0xFF0D1F13)
internal val LightTertiary = Color(0xFF3A6469)
internal val LightOnTertiary = Color(0xFFFFFFFF)
internal val LightTertiaryContainer = Color(0xFFBDEAF0)
internal val LightOnTertiaryContainer = Color(0xFF001F23)
internal val LightError = Color(0xFFBA1A1A)
internal val LightOnError = Color(0xFFFFFFFF)
internal val LightErrorContainer = Color(0xFFFFDAD6)
internal val LightOnErrorContainer = Color(0xFF410002)
internal val LightSurface = Color(0xFFF7FBF4)
internal val LightOnSurface = Color(0xFF191D18)
internal val LightSurfaceVariant = Color(0xFFDCE5DB)
internal val LightOnSurfaceVariant = Color(0xFF414941)
internal val LightSurfaceDim = Color(0xFFD7DBD4)
internal val LightSurfaceBright = Color(0xFFF7FBF4)
internal val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
internal val LightSurfaceContainerLow = Color(0xFFF1F5EE)
internal val LightSurfaceContainer = Color(0xFFEBEFE8)
internal val LightSurfaceContainerHigh = Color(0xFFE5EAE3)
internal val LightSurfaceContainerHighest = Color(0xFFE0E4DD)
internal val LightOutline = Color(0xFF717971)
internal val LightOutlineVariant = Color(0xFFC1C9BF)
internal val LightInverseSurface = Color(0xFF2D322C)
internal val LightInverseOnSurface = Color(0xFFEFF2EA)
internal val LightInversePrimary = Color(0xFF91D5A4)

// Dark scheme
internal val DarkPrimary = Color(0xFF91D5A4)
internal val DarkOnPrimary = Color(0xFF00391E)
internal val DarkPrimaryContainer = Color(0xFF0C522F)
internal val DarkOnPrimaryContainer = Color(0xFFADF2BF)
internal val DarkSecondary = Color(0xFFB6CCB8)
internal val DarkOnSecondary = Color(0xFF223526)
internal val DarkSecondaryContainer = Color(0xFF384B3C)
internal val DarkOnSecondaryContainer = Color(0xFFD2E8D4)
internal val DarkTertiary = Color(0xFFA1CED4)
internal val DarkOnTertiary = Color(0xFF00363B)
internal val DarkTertiaryContainer = Color(0xFF1F4D52)
internal val DarkOnTertiaryContainer = Color(0xFFBDEAF0)
internal val DarkError = Color(0xFFFFB4AB)
internal val DarkOnError = Color(0xFF690005)
internal val DarkErrorContainer = Color(0xFF93000A)
internal val DarkOnErrorContainer = Color(0xFFFFDAD6)
internal val DarkSurface = Color(0xFF10150F)
internal val DarkOnSurface = Color(0xFFE0E4DD)
internal val DarkSurfaceVariant = Color(0xFF414941)
internal val DarkOnSurfaceVariant = Color(0xFFC1C9BF)
internal val DarkSurfaceDim = Color(0xFF10150F)
internal val DarkSurfaceBright = Color(0xFF363A34)
internal val DarkSurfaceContainerLowest = Color(0xFF0B0F0A)
internal val DarkSurfaceContainerLow = Color(0xFF191D18)
internal val DarkSurfaceContainer = Color(0xFF1D211C)
internal val DarkSurfaceContainerHigh = Color(0xFF272B26)
internal val DarkSurfaceContainerHighest = Color(0xFF323631)
internal val DarkOutline = Color(0xFF8B938B)
internal val DarkOutlineVariant = Color(0xFF414941)
internal val DarkInverseSurface = Color(0xFFE0E4DD)
internal val DarkInverseOnSurface = Color(0xFF2D322C)
internal val DarkInversePrimary = Color(0xFF2A6B43)

/**
 * Color key used to tell categories apart in lists and on task cards.
 * Categories have no color column in the database, so the tone is derived
 * from the category name and stays stable for as long as the name does.
 */
data class CategoryTone(
    val container: Color,
    val onContainer: Color,
    val accent: Color,
)

internal val LightCategoryTones =
    listOf(
        CategoryTone(Color(0xFFDEE1FF), Color(0xFF1B2A6B), Color(0xFF4759B8)),
        CategoryTone(Color(0xFFEFDBFF), Color(0xFF320051), Color(0xFF7E4A9E)),
        CategoryTone(Color(0xFFBDEAF0), Color(0xFF001F23), Color(0xFF39666B)),
        CategoryTone(Color(0xFFFFDCC2), Color(0xFF301400), Color(0xFF8C4E1E)),
        CategoryTone(Color(0xFFFFD9E2), Color(0xFF3E001D), Color(0xFF984061)),
        CategoryTone(Color(0xFFD7E8B2), Color(0xFF1A2000), Color(0xFF556B2B)),
    )

internal val DarkCategoryTones =
    listOf(
        CategoryTone(Color(0xFF3D4878), Color(0xFFDEE1FF), Color(0xFFBCC2FF)),
        CategoryTone(Color(0xFF553A6B), Color(0xFFEFDBFF), Color(0xFFDEB8FB)),
        CategoryTone(Color(0xFF1F4D52), Color(0xFFBDEAF0), Color(0xFFA1CED4)),
        CategoryTone(Color(0xFF6B3A12), Color(0xFFFFDCC2), Color(0xFFFFB77C)),
        CategoryTone(Color(0xFF6B394C), Color(0xFFFFD9E2), Color(0xFFFFB1C7)),
        CategoryTone(Color(0xFF3C4E19), Color(0xFFD7E8B2), Color(0xFFBBCC97)),
    )
