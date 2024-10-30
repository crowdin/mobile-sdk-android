package com.crowdin.platform

import com.crowdin.platform.data.model.AuthConfig
import org.junit.Assert
import org.junit.Test

class CrowdinConfigTest {
    @Test
    fun whenDistributionHashEmpty_shouldThrowException() {
        // Given
        val distributionHash = ""

        // When
        try {
            CrowdinConfig
                .Builder()
                .withDistributionHash(distributionHash)
                .build()
            Assert.fail("SDK initialization with empty `distribution hash` not valid.")
        } catch (exception: IllegalArgumentException) {
            // Then
            // exception expected
        }
    }

    @Test
    fun whenSourceLanguageEmptyWithRealTimeEnabled_shouldThrowException() {
        // Given
        val sourceLanguage = ""

        // When
        try {
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withRealTimeUpdates()
                .withSourceLanguage(sourceLanguage)
                .build()
            Assert.fail("SDK initialization with empty `sourceLanguage` when realTime updates enabled - not valid.")
        } catch (exception: IllegalArgumentException) {
            // Then
            // exception expected
        }
    }

    @Test
    fun whenSourceLanguageEmptyWithScreenshotEnabled_shouldThrowException() {
        // Given
        val sourceLanguage = ""

        // When
        try {
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withScreenshotEnabled()
                .withSourceLanguage(sourceLanguage)
                .build()
            Assert.fail("SDK initialization with empty `sourceLanguage` when screenshots enabled - not valid.")
        } catch (exception: IllegalArgumentException) {
            // Then
            // exception expected
        }
    }

    @Test
    fun whenAuthConfigEmptyValues_shouldThrowException() {
        // Given
        val authConfig = AuthConfig(" ", "")

        // When
        try {
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withAuthConfig(authConfig)
                .build()
            Assert.fail("SDK initialization with empty `AuthConfig` values - not valid.")
        } catch (exception: IllegalArgumentException) {
            // Then
            // exception expected
        }
    }

    @Test
    fun whenUpdateIntervalLess15Minutes_shouldUseDefault() {
        // Given
        val smallInterval = 10 * 60L

        // When
        val configWithSmallInterval =
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withUpdateInterval(smallInterval)
                .build()

        // Then
        Assert.assertTrue(configWithSmallInterval.updateInterval == 15 * 60 * 1000L)
    }

    @Test
    fun whenUpdateIntervalMore15Minutes_shouldBeSuccess() {
        // Given
        val bigInterval = 20 * 60L

        // When
        val configWithBigInterval =
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withUpdateInterval(bigInterval)
                .build()

        // Then
        Assert.assertTrue(configWithBigInterval.updateInterval == bigInterval * 1000)
    }

    @Test
    fun whenAuthConfigWithRequestAuthDialog_shouldBeTrueRequestAuthDialog() {
        // When
        val config =
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withAuthConfig(AuthConfig("clientId", "cliendSecret", requestAuthDialog = true))
                .build()

        // Then
        Assert.assertTrue(config.authConfig?.requestAuthDialog == true)
    }

    @Test
    fun whenAuthConfigWithRequestAuthDialog_shouldBeFalseRequestAuthDialog() {
        // When
        val config =
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withAuthConfig(AuthConfig("clientId", "cliendSecret", requestAuthDialog = false))
                .build()

        // Then
        Assert.assertTrue(config.authConfig?.requestAuthDialog == false)
    }

    @Test
    fun whenEmptyAuthConfig_shouldBeTrueRequestAuthDialog() {
        // When
        val config =
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .build()

        // Then

        val requestAuthDialog = config.authConfig?.requestAuthDialog == false

        Assert.assertFalse(requestAuthDialog)
    }

    @Test
    fun whenAuthConfigWithoutRequestAuthDialog_shouldBeTrueRequestAuthDialog() {
        // When
        val config =
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withAuthConfig(AuthConfig("clientId", "cliendSecret"))
                .build()

        // Then
        Assert.assertTrue(config.authConfig?.requestAuthDialog == true)
    }

    @Test
    fun whenInitSyncDisabled_isInitSyncEnabledShouldBeFalse() {
        // When
        val config =
            CrowdinConfig
                .Builder()
                .withDistributionHash("distributionHash")
                .withInitSyncDisabled()
                .build()

        // Then
        Assert.assertTrue(config.isInitSyncEnabled == false)
    }
}
