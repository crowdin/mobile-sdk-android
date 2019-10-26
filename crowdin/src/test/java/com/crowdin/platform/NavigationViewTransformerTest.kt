package com.crowdin.platform

import android.view.Menu
import com.crowdin.platform.transformer.NavigationViewTransformer
import com.google.android.material.navigation.NavigationView
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class NavigationViewTransformerTest {

    @Test
    fun getMenuTest() {
        // Given
        val viewTransformer = NavigationViewTransformer()
        val mockView = mock(NavigationView::class.java)
        val expectedMenu = mock(Menu::class.java)
        `when`(mockView.menu).thenReturn(expectedMenu)

        // When
        val actualMenu = viewTransformer.getMenu(mockView)

        // Then
        assertThat(actualMenu, `is`(expectedMenu))
    }
}