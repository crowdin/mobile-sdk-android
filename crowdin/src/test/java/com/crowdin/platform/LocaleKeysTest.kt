package com.crowdin.platform

import android.content.res.Configuration
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.local.LocalRepository
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.remote.RemoteRepository
import com.crowdin.platform.util.FeatureFlags
import com.crowdin.platform.util.getFormattedCode
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.util.Locale

/**
 * The lookup key used at runtime has to point at the key the translations were actually stored
 * under, otherwise a matched language is downloaded and then never read.
 */
class LocaleKeysTest {
    private lateinit var mockLocalRepository: LocalRepository
    private lateinit var mockRemoteRepository: RemoteRepository
    private lateinit var mockPreferences: Preferences
    private lateinit var mockLocalDataChangeObserver: LocalDataChangeObserver
    private lateinit var dataManager: DataManager
    private lateinit var defaultLocale: Locale

    @Before
    fun setUp() {
        defaultLocale = Locale.getDefault()
        mockLocalRepository = mock(LocalRepository::class.java)
        mockRemoteRepository = mock(RemoteRepository::class.java)
        mockPreferences = mock(Preferences::class.java)
        mockLocalDataChangeObserver = mock(LocalDataChangeObserver::class.java)
        dataManager =
            DataManager(
                mockRemoteRepository,
                mockLocalRepository,
                mockPreferences,
                mockLocalDataChangeObserver,
            )
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

    private fun givenStoredKey(key: String?) {
        `when`(mockPreferences.getString(DataManager.TRANSLATION_LOCALE_KEY)).thenReturn(key)
    }

    @Test
    fun whenNothingStoredYet_shouldOnlyUseTheFormattedDeviceCode() {
        givenStoredKey(null)

        assertEquals(listOf("es-MX"), dataManager.getLocaleKeys(configurationOf(Locale("es", "MX"))))
    }

    @Test
    fun whenNothingStoredYetAndPreferencesReturnEmpty_shouldOnlyUseTheFormattedDeviceCode() {
        // CrowdinPreferences defaults a missing key to an empty string rather than null.
        givenStoredKey("")

        assertEquals(listOf("es-MX"), dataManager.getLocaleKeys(configurationOf(Locale("es", "MX"))))
    }

    @Test
    fun whenStoredKeyIsForTheSameLanguage_shouldFallBackToIt() {
        givenStoredKey("es-419")

        assertEquals(listOf("es-MX", "es-419"), dataManager.getLocaleKeys(configurationOf(Locale("es", "MX"))))
    }

    @Test
    fun whenStoredKeyIsTheCanonicalRegion_shouldFallBackToIt() {
        // Project targets German only; an Austrian device must still read the German translations.
        givenStoredKey("de-DE")

        assertEquals(listOf("de-AT", "de-DE"), dataManager.getLocaleKeys(configurationOf(Locale("de", "AT"))))
    }

    @Test
    fun whenStoredKeyIsForAnotherLanguage_shouldNotBeUsed() {
        // Locale switched after the last sync: keep serving bundled French, never stale Spanish.
        givenStoredKey("es-419")

        assertEquals(listOf("fr-FR"), dataManager.getLocaleKeys(configurationOf(Locale("fr", "FR"))))
    }

    @Test
    fun whenStoredKeyEqualsTheFormattedCode_shouldNotBeDuplicated() {
        givenStoredKey("es-ES")

        assertEquals(listOf("es-ES"), dataManager.getLocaleKeys(configurationOf(Locale("es", "ES"))))
    }

    @Test
    fun whenLanguageUsesALegacyAndroidCode_shouldStillMatchTheStoredKey() {
        // Android reports Hebrew as `iw`; Crowdin stores it as `he`.
        givenStoredKey("he-IL")

        assertEquals(listOf("he-IL"), dataManager.getLocaleKeys(configurationOf(Locale("iw", "IL"))))
    }

    @Test
    fun theDeviceCodeIsAlwaysTriedFirst() {
        // Guarantees a lookup that resolves today cannot start resolving somewhere else.
        givenStoredKey("es-419")

        listOf(Locale("es", "MX"), Locale("es", "ES"), Locale("es")).forEach { locale ->
            val keys = dataManager.getLocaleKeys(configurationOf(locale))

            assertEquals(locale.toString(), locale.getFormattedCode(), keys.first())
        }
    }

    @Test
    fun refreshData_shouldRememberTheKeyTranslationsWereStoredUnder() {
        FeatureFlags.registerConfig(mock(CrowdinConfig::class.java))

        dataManager.refreshData(LanguageData("es-419"))

        verify(mockPreferences).setString(DataManager.TRANSLATION_LOCALE_KEY, "es-419")
    }

    @Test
    fun refreshData_shouldIgnoreAnEmptyLanguage() {
        FeatureFlags.registerConfig(mock(CrowdinConfig::class.java))

        dataManager.refreshData(LanguageData())

        verifyNoInteractions(mockPreferences)
    }
}
