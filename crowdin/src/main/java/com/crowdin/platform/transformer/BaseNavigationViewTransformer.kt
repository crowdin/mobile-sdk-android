package com.crowdin.platform.transformer

import android.util.AttributeSet
import android.view.Menu
import android.view.View
import com.crowdin.platform.util.TextUtils

/**
 * A transformer which transforms navigation views: it transforms the texts coming from the menu.
 */
internal abstract class BaseNavigationViewTransformer : BaseTransformer() {

    protected abstract fun getMenu(view: View): Menu

    override fun transform(view: View, attrs: AttributeSet): View {
        if (!viewType.isInstance(view)) {
            return view
        }

        for (index in 0 until attrs.attributeCount) {
            when (attrs.getAttributeName(index)) {
                Attributes.ATTRIBUTE_APP_MENU, Attributes.ATTRIBUTE_MENU ->
                    updateText(view, attrs, index)
            }
        }

        return view
    }

    private fun updateText(view: View, attrs: AttributeSet, index: Int) {
        val resources = view.context.resources
        val value = attrs.getAttributeValue(index)
        if (value == null || !value.startsWith("@")) return

        val resId = attrs.getAttributeResourceValue(index, 0)
        val menu = getMenu(view)
        TextUtils.updateMenuItemsText(menu, resources, resId)
    }
}
