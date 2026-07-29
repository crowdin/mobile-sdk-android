package com.crowdin.platform

import com.crowdin.platform.data.model.LanguageDetails
import com.crowdin.platform.data.model.SupportedLanguages
import com.crowdin.platform.util.getMatchedCode
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Locale

class ExtensionsTest {
    private lateinit var defaultLocale: Locale

    private val supportedLanguages: SupportedLanguages =
        mapOf(
            "es" to LanguageDetails("Spanish", "es-ES"),
            "es-419" to LanguageDetails("Spanish, Latin America", "es-419"),
            "fr" to LanguageDetails("French", "fr-FR"),
        )

    @Before
    fun setUp() {
        defaultLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun whenLatinAmericanSpanishLocaleAndBothSpanishVariantsPresent_shouldMatchEs419() {
        Locale.setDefault(Locale("es", "MX"))

        val result = getMatchedCode(null, listOf("es", "es-419", "fr"), supportedLanguages)

        assertEquals("es-419", result)
    }

    @Test
    fun whenAnyLatinAmericanRegion_shouldMatchEs419() {
        val latinAmericanRegions =
            listOf(
                "AR",
                "BO",
                "BR",
                "BZ",
                "CL",
                "CO",
                "CR",
                "CU",
                "DO",
                "EC",
                "GT",
                "HN",
                "MX",
                "NI",
                "PA",
                "PE",
                "PR",
                "PY",
                "SV",
                "US",
                "UY",
                "VE",
            )

        latinAmericanRegions.forEach { region ->
            Locale.setDefault(Locale("es", region))

            val result = getMatchedCode(null, listOf("es", "es-419"), supportedLanguages)

            assertEquals("Region $region should resolve to es-419", "es-419", result)
        }
    }

    @Test
    fun whenCastilianSpanishLocale_shouldMatchEs() {
        Locale.setDefault(Locale("es", "ES"))

        val result = getMatchedCode(null, listOf("es", "es-419"), supportedLanguages)

        assertEquals("es", result)
    }

    @Test
    fun whenLatinAmericanSpanishLocaleAndOnlyEsPresent_shouldFallbackToEs() {
        Locale.setDefault(Locale("es", "MX"))

        val result = getMatchedCode(null, listOf("es", "fr"), supportedLanguages)

        assertEquals("es", result)
    }

    @Test
    fun whenLatinAmericanSpanishLocaleAndOnlyEs419Present_shouldMatchEs419() {
        Locale.setDefault(Locale("es", "PA"))

        val result = getMatchedCode(null, listOf("es-419", "fr"), supportedLanguages)

        assertEquals("es-419", result)
    }

    @Test
    fun whenEuropeanSpanishRegionAndBothSpanishVariantsPresent_shouldNotMatchEs419() {
        // Equatorial Guinea keeps the default `es` parent in CLDR
        Locale.setDefault(Locale("es", "GQ"))

        val result = getMatchedCode(null, listOf("es", "es-419"), supportedLanguages)

        assertEquals("es", result)
    }

    @Test
    fun whenExactCodePresentInList_shouldMatchExactCode() {
        Locale.setDefault(Locale("fr", "CA"))

        val result = getMatchedCode(null, listOf("fr", "fr-CA"), supportedLanguages)

        assertEquals("fr-CA", result)
    }

    @Test
    fun whenLocaleMatchesSupportedLanguageLocale_shouldReturnLanguageKey() {
        Locale.setDefault(Locale("fr", "FR"))

        val result = getMatchedCode(null, listOf("es", "fr"), supportedLanguages)

        assertEquals("fr", result)
    }

    @Test
    fun whenNoMatch_shouldReturnNull() {
        Locale.setDefault(Locale("de", "DE"))

        val result = getMatchedCode(null, listOf("es", "fr"), supportedLanguages)

        assertNull(result)
    }
}
