package com.crowdin.platform

import android.content.res.Configuration
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.local.MemoryLocalRepository
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.LanguageDetails
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.data.model.SupportedLanguages
import com.crowdin.platform.data.remote.RemoteRepository
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.getMatchedCode
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.lang.reflect.Type
import java.util.Locale

/**
 * Guards the contract between the two halves of locale resolution: the language [getMatchedCode]
 * picks has to be findable under one of the keys [DataManager.getLocaleKeys] asks for. A language
 * that resolves but cannot be read is the same as no translation at all.
 */
class LocaleResolutionEndToEndTest {
    /** Minimal in-memory [Preferences] so the stored locale key survives within a test. */
    private class FakePreferences : Preferences {
        private val strings = mutableMapOf<String, String>()

        override fun setString(
            key: String,
            value: String,
        ) {
            strings[key] = value
        }

        // Matches CrowdinPreferences: a missing key reads back as an empty string, not null.
        override fun getString(key: String): String? = strings[key] ?: ""

        override fun setLastUpdate(lastUpdate: Long) = Unit

        override fun getLastUpdate(): Long = 0L

        override fun saveData(
            type: String,
            data: Any?,
        ) = Unit

        override fun <T> getData(
            type: String,
            classType: Type,
        ): T? = null
    }

    // The OrangeTheory project from the reported ticket.
    private val project: SupportedLanguages =
        mapOf(
            "ar" to LanguageDetails("Arabic", "ar-SA"),
            "zh-CN" to LanguageDetails("Chinese Simplified", "zh-CN"),
            "fr" to LanguageDetails("French", "fr-FR"),
            "fr-CA" to LanguageDetails("French, Canada", "fr-CA"),
            "de" to LanguageDetails("German", "de-DE"),
            "es" to LanguageDetails("Spanish", "es-ES"),
            "es-419" to LanguageDetails("Spanish, Latin America", "es-419"),
        )
    private val manifestLanguages = project.keys.toList()

    private lateinit var defaultLocale: Locale

    @Before
    fun setUp() {
        defaultLocale = Locale.getDefault()
        FeatureFlags.registerConfig(mock(CrowdinConfig::class.java))
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

    /**
     * @param syncLocale locale of the context handed to `Crowdin.init` / `forceUpdate`.
     * @param readLocale locale of the wrapped activity the strings are read from.
     * @return the string a user would see, or `null` when the SDK falls back to bundled resources.
     */
    private fun translationShownTo(
        syncLocale: Locale,
        readLocale: Locale = syncLocale,
    ): String? {
        Locale.setDefault(syncLocale)

        val dataManager =
            DataManager(
                mock(RemoteRepository::class.java),
                MemoryLocalRepository(),
                FakePreferences(),
                mock(LocalDataChangeObserver::class.java),
            )

        val matched = getMatchedCode(configurationOf(syncLocale), manifestLanguages, project)
        if (matched != null) {
            val languageData = LanguageData(project.getValue(matched).locale)
            languageData.resources.add(StringData("greeting", "greeting from $matched"))
            dataManager.refreshData(languageData)
        }

        return dataManager.getLocaleKeys(configurationOf(readLocale)).firstNotNullOfOrNull {
            dataManager.getString(it, "greeting")
        }
    }

    @Test
    fun latinAmericanDeviceReadsLatinAmericanSpanish() {
        assertEquals("greeting from es-419", translationShownTo(Locale("es", "MX")))
        assertEquals("greeting from es-419", translationShownTo(Locale("es", "PA")))
        assertEquals("greeting from es-419", translationShownTo(Locale("es", "AR")))
    }

    @Test
    fun castilianDeviceReadsCastilianSpanish() {
        assertEquals("greeting from es", translationShownTo(Locale("es", "ES")))
    }

    @Test
    fun deviceRegionWithoutItsOwnTranslationReadsTheLanguageTranslation() {
        // Austria has no dedicated target language, so the German translation has to be readable
        // even though it is stored under de-DE.
        assertEquals("greeting from de", translationShownTo(Locale("de", "AT")))
    }

    @Test
    fun regionsMatchingTheirCrowdinLocaleKeepWorking() {
        assertEquals("greeting from de", translationShownTo(Locale("de", "DE")))
        assertEquals("greeting from fr", translationShownTo(Locale("fr", "FR")))
        assertEquals("greeting from fr-CA", translationShownTo(Locale("fr", "CA")))
        assertEquals("greeting from zh-CN", translationShownTo(Locale("zh", "CN")))
    }

    @Test
    fun activityPinnedToEs419ReadsLatinAmericanSpanish() {
        // The workaround some integrations apply in `attachBaseContext`.
        val shown = translationShownTo(syncLocale = Locale("es", "PA"), readLocale = Locale.forLanguageTag("es-419"))

        assertEquals("greeting from es-419", shown)
    }

    @Test
    fun languageOutsideTheProjectFallsBackToBundledResources() {
        assertEquals(null, translationShownTo(Locale("pl", "PL")))
    }

    @Test
    fun localeSwitchedAfterSyncDoesNotServeThePreviousLanguage() {
        val shown = translationShownTo(syncLocale = Locale("es", "MX"), readLocale = Locale("fr", "FR"))

        assertEquals(null, shown)
    }
}
