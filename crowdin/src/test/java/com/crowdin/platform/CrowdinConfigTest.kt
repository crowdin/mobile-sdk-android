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
            CrowdinConfig.Builder()
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
            CrowdinConfig.Builder()
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
            CrowdinConfig.Builder()
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
            CrowdinConfig.Builder()
                    .withDistributionHash("distributionHash")
                    .withAuthConfig(authConfig)
                    .build()
            Assert.fail("SDK initialization with empty `AuthConfig` values - not valid.")
        } catch (exception: IllegalArgumentException) {
            // Then
            // exception expected
        }
    }
}
