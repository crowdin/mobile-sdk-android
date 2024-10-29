package com.crowdin.platform

import com.crowdin.platform.util.FeatureFlags
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FeatureFlagTest {

    @Test
    fun whenSetRealtimeUpdatesEnabled_shouldPersistDuringSession() {
        // Given
        val config = mock(CrowdinConfig::class.java)
        `when`(config.isRealTimeUpdateEnabled).thenReturn(true)

        // When
        FeatureFlags.registerConfig(config)

        // Then
        assertThat(FeatureFlags.isRealTimeUpdateEnabled, `is`(true))
    }

    @Test
    fun whenSetScreenshotsEnabled_shouldPersistDuringSession() {
        // Given
        val config = mock(CrowdinConfig::class.java)
        `when`(config.isScreenshotEnabled).thenReturn(true)

        // When
        FeatureFlags.registerConfig(config)

        // Then
        assertThat(FeatureFlags.isScreenshotEnabled, `is`(true))
    }
}
