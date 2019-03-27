package com.crowdin.platform.utils

import android.content.res.Resources
import android.util.AttributeSet
import android.view.Menu

internal object TextUtils {

    fun getTextForAttribute(attrs: AttributeSet, index: Int, resources: Resources): CharSequence? {
        var text: CharSequence? = null
        val value = attrs.getAttributeValue(index)
        if (value != null && value.startsWith("@")) {
            text = resources.getText(attrs.getAttributeResourceValue(index, 0))
        }

        return text
    }

    fun updateMenuItemsText(menu: Menu, resources: Resources, resId: Int) {
        val itemStrings = XmlParserUtils.getMenuItemsStrings(resources, resId)

        for (i in 0 until itemStrings.size()) {
            val itemKey = itemStrings.keyAt(i)
            val itemValue = itemStrings.valueAt(i)

            if (itemValue.title != 0) {
                menu.findItem(itemKey)?.title = resources.getString(itemValue.title)
            }
            if (itemValue.titleCondensed != 0) {
                menu.findItem(itemKey)?.titleCondensed = resources.getString(itemValue.titleCondensed)
            }
        }
    }
}
