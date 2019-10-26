package com.crowdin.platform

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.AttributeSet
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toolbar
import com.crowdin.platform.transformer.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

class ViewTransformerManagerTest {

    private lateinit var textViewTransformer: TextViewTransformer
    private lateinit var toolbarTransformer: ToolbarTransformer
    private lateinit var supportToolbarTransformer: SupportToolbarTransformer
    private lateinit var bottomNavigationViewTransformer: BottomNavigationViewTransformer
    private lateinit var navigationViewTransformer: NavigationViewTransformer
    private lateinit var spinnerTransformer: SpinnerTransformer

    @Before
    fun setUp() {
        initCrowdin()
        textViewTransformer = mock(TextViewTransformer::class.java)
        `when`(textViewTransformer.viewType).thenReturn(TextView::class.java)
        toolbarTransformer = mock(ToolbarTransformer::class.java)
        `when`(toolbarTransformer.viewType).thenReturn(Toolbar::class.java)
        supportToolbarTransformer = mock(SupportToolbarTransformer::class.java)
        `when`(supportToolbarTransformer.viewType).thenReturn(androidx.appcompat.widget.Toolbar::class.java)
        bottomNavigationViewTransformer = mock(BottomNavigationViewTransformer::class.java)
        `when`(bottomNavigationViewTransformer.viewType).thenReturn(BottomNavigationView::class.java)
        navigationViewTransformer = mock(NavigationViewTransformer::class.java)
        `when`(navigationViewTransformer.viewType).thenReturn(NavigationView::class.java)
        spinnerTransformer = mock(SpinnerTransformer::class.java)
        `when`(spinnerTransformer.viewType).thenReturn(Spinner::class.java)
    }

    @Test
    fun whenTransformTextView_shouldDelegateToTextViewTransformer() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()
        val mockAttributes = mock(AttributeSet::class.java)
        val context = mock(Context::class.java)
        val textView = mock(TextView::class.java)
        `when`(textView.context).thenReturn(context)

        // When
        viewTransformerManager.transform(textView, mockAttributes)

        // Then
        verify(textViewTransformer).transform(textView, mockAttributes)
    }

    @Test
    fun whenTransformToolbar_shouldDelegateToToolbarTransformer() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()
        val mockAttributes = mock(AttributeSet::class.java)
        val context = mock(Context::class.java)
        val toolbar = mock(Toolbar::class.java)
        `when`(toolbar.context).thenReturn(context)

        // When
        viewTransformerManager.transform(toolbar, mockAttributes)

        // Then
        verify(toolbarTransformer).transform(toolbar, mockAttributes)
    }

    @Test
    fun whenTransformSupportToolbar_shouldDelegateToSupportToolbarTransformer() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()
        val mockAttributes = mock(AttributeSet::class.java)
        val context = mock(Context::class.java)
        val toolbar = mock(androidx.appcompat.widget.Toolbar::class.java)
        `when`(toolbar.context).thenReturn(context)

        // When
        viewTransformerManager.transform(toolbar, mockAttributes)

        // Then
        verify(supportToolbarTransformer).transform(toolbar, mockAttributes)
    }

    @Test
    fun whenTransformBottomNavView_shouldDelegateToBottomNavTransformer() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()
        val mockAttributes = mock(AttributeSet::class.java)
        val context = mock(Context::class.java)
        val bottomNavigationView = mock(BottomNavigationView::class.java)
        `when`(bottomNavigationView.context).thenReturn(context)

        // When
        viewTransformerManager.transform(bottomNavigationView, mockAttributes)

        // Then
        verify(bottomNavigationViewTransformer).transform(bottomNavigationView, mockAttributes)
    }

    @Test
    fun whenTransformNavView_shouldDelegateToNavViewTransformer() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()
        val mockAttributes = mock(AttributeSet::class.java)
        val context = mock(Context::class.java)
        val navigationView = mock(NavigationView::class.java)
        `when`(navigationView.context).thenReturn(context)

        // When
        viewTransformerManager.transform(navigationView, mockAttributes)

        // Then
        verify(navigationViewTransformer).transform(navigationView, mockAttributes)
    }

    @Test
    fun whenTransformSpinner_shouldDelegateToSpinnerTransformer() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()
        val mockAttributes = mock(AttributeSet::class.java)
        val context = mock(Context::class.java)
        val spinner = mock(Spinner::class.java)
        `when`(spinner.context).thenReturn(context)

        // When
        viewTransformerManager.transform(spinner, mockAttributes)

        // Then
        verify(spinnerTransformer).transform(spinner, mockAttributes)
    }

    @Test
    fun whenInvalidate_shouldInvalidateAllTransformers() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()

        // When
        viewTransformerManager.invalidate()

        // Then
        verify(textViewTransformer).invalidate()
        verify(toolbarTransformer).invalidate()
        verify(supportToolbarTransformer).invalidate()
        verify(bottomNavigationViewTransformer).invalidate()
        verify(navigationViewTransformer).invalidate()
        verify(spinnerTransformer).invalidate()
    }

    @Test
    fun whenGetViewData_shouldCollectViewsFromAllTransformers() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()

        // When
        viewTransformerManager.getViewData()

        // Then
        verify(textViewTransformer).getViewDataFromWindow()
        verify(toolbarTransformer).getViewDataFromWindow()
        verify(supportToolbarTransformer).getViewDataFromWindow()
        verify(bottomNavigationViewTransformer).getViewDataFromWindow()
        verify(navigationViewTransformer).getViewDataFromWindow()
        verify(spinnerTransformer).getViewDataFromWindow()
    }

    @Test
    fun whenSetOnViewsChangeListener_shouldSetListenerToAllTransformers() {
        // Given
        val viewTransformerManager = givenViewTransformerManager()
        val listener = mock(ViewsChangeListener::class.java)

        // When
        viewTransformerManager.setOnViewsChangeListener(listener)

        // Then
        verify(textViewTransformer).setOnViewsChangeListener(listener)
        verify(toolbarTransformer).setOnViewsChangeListener(listener)
        verify(supportToolbarTransformer).setOnViewsChangeListener(listener)
        verify(bottomNavigationViewTransformer).setOnViewsChangeListener(listener)
        verify(navigationViewTransformer).setOnViewsChangeListener(listener)
        verify(spinnerTransformer).setOnViewsChangeListener(listener)
    }

    private fun givenViewTransformerManager(): ViewTransformerManager {
        val manager = ViewTransformerManager()
        manager.registerTransformer(textViewTransformer)
        manager.registerTransformer(toolbarTransformer)
        manager.registerTransformer(supportToolbarTransformer)
        manager.registerTransformer(bottomNavigationViewTransformer)
        manager.registerTransformer(navigationViewTransformer)
        manager.registerTransformer(spinnerTransformer)

        return manager
    }

    private fun initCrowdin() {
        val config = CrowdinConfig.Builder()
                .withFilePaths("test")
                .withDistributionHash("test")
                .build()
        val sharedPrefs = mock(SharedPreferences::class.java)!!
        val context = mock(Context::class.java)
        `when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        val connectivityManager = mock(ConnectivityManager::class.java)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)

        Crowdin.init(context, config)
    }
}