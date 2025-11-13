package com.crowdin.platform

import android.content.Context
import android.content.res.Resources
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.transformer.ViewTransformerManager
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class CrowdinContextWrapperTest {
    @Test
    fun testUsesApplicationContextResources() {
        // Given
        val baseContext = mock(Context::class.java)
        val applicationContext = mock(Context::class.java)
        val applicationResources = mock(Resources::class.java)
        val dataManager = mock(DataManager::class.java)
        val viewTransformerManager = mock(ViewTransformerManager::class.java)

        `when`(baseContext.applicationContext).thenReturn(applicationContext)
        `when`(applicationContext.resources).thenReturn(applicationResources)

        // When
        val wrappedContext = CrowdinContextWrapper.wrap(
            baseContext,
            dataManager,
            viewTransformerManager
        )

        // Then
        // The wrapped context should exist and be a CrowdinContextWrapper
        assertThat(wrappedContext is CrowdinContextWrapper, `is`(true))
    }

    @Test
    fun testReturnsOriginalContextWhenDataManagerIsNull() {
        // Given
        val baseContext = mock(Context::class.java)
        val viewTransformerManager = mock(ViewTransformerManager::class.java)

        // When
        val wrappedContext = CrowdinContextWrapper.wrap(
            baseContext,
            null,
            viewTransformerManager
        )

        // Then
        assertThat(wrappedContext, `is`(baseContext))
    }
}
