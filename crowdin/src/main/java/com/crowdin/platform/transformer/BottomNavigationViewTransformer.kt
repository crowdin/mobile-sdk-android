package com.crowdin.platform.transformer

import android.view.Menu
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A transformer which transforms BottomNavigationView: it transforms the texts coming from the menu.
 */
internal class BottomNavigationViewTransformer : BaseNavigationViewTransformer() {
    override val viewType = BottomNavigationView::class.java

    override fun getMenu(view: View): Menu = (view as BottomNavigationView).menu
}
