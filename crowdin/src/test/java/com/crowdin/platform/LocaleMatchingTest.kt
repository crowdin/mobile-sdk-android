package com.crowdin.platform

import android.content.res.Configuration
import com.crowdin.platform.data.model.LanguageDetails
import com.crowdin.platform.data.model.SupportedLanguages
import com.crowdin.platform.util.encodeDistributionPath
import com.crowdin.platform.util.getMatchedCode
import com.crowdin.platform.util.parentLocaleCodes
import com.crowdin.platform.util.withCrowdinSupportedCheck
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Locale

class LocaleMatchingTest {
    private lateinit var defaultLocale: Locale

    private val spanishProject: SupportedLanguages =
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

    private fun configurationOf(locale: Locale): Configuration {
        val configuration = Configuration()

        @Suppress("DEPRECATION")
        configuration.locale = locale

        return configuration
    }

    @Test
    fun whenLatinAmericanRegionAndBothSpanishVariantsPresent_shouldMatchEs419() {
        Locale.setDefault(Locale("es", "MX"))

        assertEquals("es-419", getMatchedCode(null, listOf("es", "es-419", "fr"), spanishProject))
    }

    @Test
    fun whenLatinAmericanRegionAndOnlyEsPresent_shouldFallBackToEs() {
        Locale.setDefault(Locale("es", "PA"))

        assertEquals("es", getMatchedCode(null, listOf("es", "fr"), spanishProject))
    }

    @Test
    fun whenLatinAmericanRegionAndOnlyEs419Present_shouldMatchEs419() {
        Locale.setDefault(Locale("es", "AR"))

        assertEquals("es-419", getMatchedCode(null, listOf("es-419", "fr"), spanishProject))
    }

    @Test
    fun whenCastilianRegion_shouldMatchEs() {
        Locale.setDefault(Locale("es", "ES"))

        assertEquals("es", getMatchedCode(null, listOf("es", "es-419"), spanishProject))
    }

    @Test
    fun whenRegionKeepsCastilianParentInCldr_shouldNotMatchEs419() {
        // Equatorial Guinea, Canary Islands and the Philippines keep the plain `es` parent.
        listOf("GQ", "IC", "PH").forEach { region ->
            Locale.setDefault(Locale("es", region))

            assertEquals(
                "es-$region must not resolve to es-419",
                "es",
                getMatchedCode(null, listOf("es", "es-419"), spanishProject),
            )
        }
    }

    @Test
    fun whenDeviceLocaleIsEs419Itself_shouldMatchExactly() {
        Locale.setDefault(Locale.forLanguageTag("es-419"))

        assertEquals("es-419", getMatchedCode(null, listOf("es", "es-419"), spanishProject))
    }

    @Test
    fun whenProjectAlsoTargetsTheExactRegion_shouldPreferIt() {
        val project = spanishProject + ("es-MX" to LanguageDetails("Spanish, Mexico", "es-MX"))
        Locale.setDefault(Locale("es", "MX"))

        assertEquals("es-MX", getMatchedCode(null, listOf("es", "es-419", "es-MX"), project))
    }

    @Test
    fun whenSupportedLanguagesIsStale_shouldIgnoreLanguagesTheManifestDoesNotShip() {
        // languages.json is served from a separate CDN cache and can still advertise a language
        // that was already removed from the project. Matching it downloads nothing at all.
        val staleLanguages =
            mapOf(
                "es-ES" to LanguageDetails("Spanish", "es-ES"),
                "es-MX" to LanguageDetails("Spanish, Mexico", "es-MX"),
                "es-419" to LanguageDetails("Spanish, Latin America", "es-419"),
            )
        val freshManifest = listOf("es-ES", "es-419")
        Locale.setDefault(Locale("es", "MX"))

        assertEquals("es-419", getMatchedCode(null, freshManifest, staleLanguages))
    }

    @Test
    fun whenAppAppliedItsOwnLocale_shouldUseThatRegionNotTheDeviceRegion() {
        // The app pinned Castilian Spanish; the device merely happens to be in Argentina.
        Locale.setDefault(Locale("es", "AR"))

        val result = getMatchedCode(configurationOf(Locale("es", "ES")), listOf("es", "es-419"), spanishProject)

        assertEquals("es", result)
    }

    @Test
    fun whenAppAppliedEs419_shouldMatchEs419ExactlyRegardlessOfDeviceRegion() {
        Locale.setDefault(Locale("es", "ES"))

        val result =
            getMatchedCode(
                configurationOf(Locale.forLanguageTag("es-419")),
                listOf("es", "es-419"),
                spanishProject,
            )

        assertEquals("es-419", result)
    }

    @Test
    fun whenChineseRegionHasNoBareZh_shouldResolveThroughScriptParent() {
        val project =
            mapOf(
                "zh-CN" to LanguageDetails("Chinese Simplified", "zh-CN"),
                "zh-TW" to LanguageDetails("Chinese Traditional", "zh-TW"),
            )
        val list = listOf("zh-CN", "zh-TW")

        Locale.setDefault(Locale("zh", "HK"))
        assertEquals("zh-TW", getMatchedCode(null, list, project))

        Locale.setDefault(Locale("zh", "MO"))
        assertEquals("zh-TW", getMatchedCode(null, list, project))

        Locale.setDefault(Locale("zh", "SG"))
        assertEquals("zh-CN", getMatchedCode(null, list, project))
    }

    @Test
    fun whenChineseMacauAndHongKongIsTargeted_shouldPreferHongKong() {
        val project =
            mapOf(
                "zh-HK" to LanguageDetails("Chinese Traditional, Hong Kong", "zh-HK"),
                "zh-TW" to LanguageDetails("Chinese Traditional", "zh-TW"),
            )
        Locale.setDefault(Locale("zh", "MO"))

        assertEquals("zh-HK", getMatchedCode(null, listOf("zh-HK", "zh-TW"), project))
    }

    @Test
    fun whenPortugueseRegionOutsideBrazil_shouldResolveToPtPt() {
        val project =
            mapOf(
                "pt-PT" to LanguageDetails("Portuguese", "pt-PT"),
                "pt-BR" to LanguageDetails("Portuguese, Brazilian", "pt-BR"),
            )
        Locale.setDefault(Locale("pt", "MZ"))

        assertEquals("pt-PT", getMatchedCode(null, listOf("pt-PT", "pt-BR"), project))
    }

    @Test
    fun whenPortugueseBrazil_shouldNotResolveToPtPt() {
        val project =
            mapOf(
                "pt-PT" to LanguageDetails("Portuguese", "pt-PT"),
                "pt-BR" to LanguageDetails("Portuguese, Brazilian", "pt-BR"),
            )
        Locale.setDefault(Locale("pt", "BR"))

        assertEquals("pt-BR", getMatchedCode(null, listOf("pt-PT", "pt-BR"), project))
    }

    @Test
    fun parentLocaleCodes_shouldFollowCldrParentLocales() {
        assertEquals(listOf("pt-PT"), parentLocaleCodes("pt", "MZ", ""))
        assertEquals(emptyList<String>(), parentLocaleCodes("pt", "BR", ""))
        assertEquals(emptyList<String>(), parentLocaleCodes("pt", "PT", ""))

        assertEquals(listOf("zh-TW"), parentLocaleCodes("zh", "HK", ""))
        assertEquals(listOf("zh-HK", "zh-TW"), parentLocaleCodes("zh", "MO", ""))
        assertEquals(listOf("zh-CN"), parentLocaleCodes("zh", "SG", ""))
        assertEquals(emptyList<String>(), parentLocaleCodes("zh", "", ""))

        // An explicit script subtag wins over the region default.
        assertEquals(listOf("zh-CN"), parentLocaleCodes("zh", "HK", "Hans"))
        assertEquals(listOf("zh-TW"), parentLocaleCodes("zh", "CN", "Hant"))

        assertEquals(listOf("sr-CS"), parentLocaleCodes("sr", "RS", "Latn"))
        assertEquals(emptyList<String>(), parentLocaleCodes("sr", "RS", "Cyrl"))
        assertEquals(emptyList<String>(), parentLocaleCodes("sr", "RS", ""))

        assertEquals(emptyList<String>(), parentLocaleCodes("de", "AT", ""))
        assertEquals(emptyList<String>(), parentLocaleCodes("en", "NZ", ""))
    }

    @Test
    fun parentLocaleCodes_es419GroupMatchesCldrExactly() {
        // CLDR supplementalData.xml: <parentLocale parent="es-419" locales="..."/>
        val cldrEs419 =
            setOf(
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
        val allRegions = Locale.getISOCountries().toSet() + "419"

        allRegions.forEach { region ->
            val expected = if (region in cldrEs419) listOf("es-419") else emptyList()

            assertEquals("region $region", expected, parentLocaleCodes("es", region, ""))
        }
    }

    @Test
    fun encodeDistributionPath_shouldEncodeBcp47ResourceFolders() {
        // A literal `+` on the wire comes back as 403 from the distribution.
        assertEquals(
            "/content/app/src/main/res/values-b%2Bes%2B419/strings.xml",
            "/content/app/src/main/res/values-b+es+419/strings.xml".encodeDistributionPath(),
        )
        assertEquals(
            "/content/values-b%2Bsr%2BLatn/strings.xml",
            "/content/values-b+sr+Latn/strings.xml".encodeDistributionPath(),
        )
    }

    @Test
    fun encodeDistributionPath_shouldLeavePlainPathsUntouched() {
        listOf(
            "/content/app/src/main/res/values-es-rES/strings.xml",
            "/content/es-419/strings.xml",
            "/mapping/app/src/main/res/values-en-rUS/strings.xml",
        ).forEach { path ->
            assertEquals(path, path.encodeDistributionPath())
        }
    }

    @Test
    fun obsoleteAndroidLanguageCodes_shouldMapToCrowdinCodes() {
        // Android's Locale.getLanguage() returns the pre-1989 codes: `iw` for Hebrew, `in` for
        // Indonesian and `ji` for Yiddish. Crowdin targets `he`, `id` and `yi`.
        assertEquals("he", "iw".withCrowdinSupportedCheck())
        assertEquals("id", "in".withCrowdinSupportedCheck())
        assertEquals("yi", "ji".withCrowdinSupportedCheck())

        // Current codes pass through untouched.
        assertEquals("he", "he".withCrowdinSupportedCheck())
        assertEquals("id", "id".withCrowdinSupportedCheck())
        assertEquals("yi", "yi".withCrowdinSupportedCheck())
        assertEquals("es", "es".withCrowdinSupportedCheck())
    }

    @Test
    fun whenSupportedLanguageLocaleMatches_shouldReturnLanguageKey() {
        Locale.setDefault(Locale("fr", "FR"))

        assertEquals("fr", getMatchedCode(null, listOf("es", "fr"), spanishProject))
    }

    @Test
    fun whenExactCodePresentInList_shouldMatchExactCode() {
        Locale.setDefault(Locale("fr", "CA"))

        assertEquals("fr-CA", getMatchedCode(null, listOf("fr", "fr-CA"), spanishProject))
    }

    @Test
    fun whenNothingMatches_shouldReturnNull() {
        Locale.setDefault(Locale("de", "DE"))

        assertNull(getMatchedCode(null, listOf("es", "fr"), spanishProject))
    }

    @Test
    fun whenListIsNull_shouldReturnFormattedCode() {
        Locale.setDefault(Locale("es", "MX"))

        assertEquals("es-MX", getMatchedCode(null, null, null))
    }
}
