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
    fun whenFilePathsEmpty_shouldThrowException() {
        // Given
        val filePaths: Array<out String> = arrayOf("test", "   ", "", "test")

        // When
        try {
            CrowdinConfig.Builder()
                    .withDistributionHash("distributionHash")
                    .withFilePaths(*filePaths)
                    .build()
            Assert.fail("SDK initialization with empty `filePaths` not valid.")
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
                    .withFilePaths("filePath")
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
                    .withFilePaths("filePath")
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
                    .withFilePaths("filePath")
                    .withAuthConfig(authConfig)
                    .build()
            Assert.fail("SDK initialization with empty `AuthConfig` values - not valid.")
        } catch (exception: IllegalArgumentException) {
            // Then
            // exception expected
        }
    }
}
