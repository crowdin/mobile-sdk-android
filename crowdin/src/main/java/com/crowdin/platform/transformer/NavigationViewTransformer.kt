package com.crowdin.platform.transformer

import android.view.Menu
import android.view.View
import com.google.android.material.navigation.NavigationView

/**
 * A transformer which transforms NavigationView: it transforms the texts coming from the menu.
 */
internal class NavigationViewTransformer : BaseNavigationViewTransformer() {

    override val viewType = NavigationView::class.java

    override fun getMenu(view: View): Menu {
        return (view as NavigationView).menu
    }
}