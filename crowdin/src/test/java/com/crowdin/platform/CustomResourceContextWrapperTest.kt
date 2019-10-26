package com.crowdin.platform

import android.content.Context
import android.content.res.Resources
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.mock

class CustomResourceContextWrapperTest {

    @Test
    fun getResourcesTest() {
        // Given
        val context = mock(Context::class.java)
        val expectedResources = mock(Resources::class.java)
        val wrapper = CustomResourcesContextWrapper(context, expectedResources)

        // When
        val actualResources = wrapper.resources

        // Then
        assertThat(actualResources, `is`(expectedResources))
    }
}