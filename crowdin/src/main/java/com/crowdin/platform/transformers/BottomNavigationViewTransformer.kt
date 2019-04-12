package com.crowdin.platform.transformers

import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.View

/**
 * A transformer which transforms BottomNavigationView: it transforms the texts coming from the menu.
 */
internal class BottomNavigationViewTransformer : BaseNavigationViewTransformer() {

    override val viewType = BottomNavigationView::class.java

    override fun getMenu(view: View): Menu {
        return (view as BottomNavigationView).menu
    }
}
