package com.crowdin.platform.transformers

import android.support.design.widget.NavigationView
import android.view.Menu
import android.view.View

/**
 * A transformer which transforms NavigationView: it transforms the texts coming from the menu.
 */
internal class NavigationViewTransformer : BaseNavigationViewTransformer() {

    override val viewType: Class<out View>
        get() = NavigationView::class.java

    override fun getMenu(view: View): Menu {
        return (view as NavigationView).menu
    }
}
