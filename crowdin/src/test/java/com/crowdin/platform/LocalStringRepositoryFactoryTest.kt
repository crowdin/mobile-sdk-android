package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences
import com.crowdin.platform.data.local.LocalStringRepositoryFactory
import com.crowdin.platform.data.local.MemoryLocalRepository
import com.crowdin.platform.data.local.SharedPrefLocalRepository
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class LocalStringRepositoryFactoryTest {

    @Test
    fun whenCreateRepositoryWithDefaultConfig_shouldCreateSharedPrefRepository() {
        // Given
        val context = givenContext()
        val config = CrowdinConfig.Builder()
                .withFilePaths("testFilePath")
                .withDistributionHash("testHash")
                .build()

        // When
        val repository =
                LocalStringRepositoryFactory.createLocalRepository(context, config)

        // Then
        assertThat(repository, instanceOf(SharedPrefLocalRepository::class.java))
    }

    @Test
    fun whenCreateRepositoryWithoutPersistence_shouldCreateMemoryRepository() {
        // Given
        val context = givenContext()
        val config = CrowdinConfig.Builder()
                .persist(false)
                .withFilePaths("testFilePath")
                .withDistributionHash("testHash")
                .build()

        // When
        val repository =
                LocalStringRepositoryFactory.createLocalRepository(context, config)

        // Then
        assertThat(repository, instanceOf(MemoryLocalRepository::class.java))
    }

    private fun givenContext(): Context {
        val context = mock(Context::class.java)
        val sharedPrefs = mock(SharedPreferences::class.java)!!
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        return context
    }
}