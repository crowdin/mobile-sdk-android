package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences
import com.crowdin.platform.recurringwork.RecurringManager
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class RecurringManagerTest {

    @Test
    fun whenSetPeriodicUpdates_shouldUpdatePref() {
        // Given
        val config = CrowdinConfig.Builder()
                .withDistributionHash("test")
                .build()
        val json = Gson().toJson(config)
        val sharedPrefs = mock(SharedPreferences::class.java)!!
        `when`(sharedPrefs.getString("crowdin_config", "")).thenReturn(json)
        val context = mock(Context::class.java)
        `when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)

        // When
        val actualConfig = RecurringManager.getConfig(context)

        // Then
        assertThat(actualConfig, instanceOf(CrowdinConfig::class.java))
    }
}