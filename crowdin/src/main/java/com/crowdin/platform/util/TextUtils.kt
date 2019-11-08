package com.crowdin.platform.util

import android.content.res.Resources
import android.util.AttributeSet
import android.view.Menu
import com.crowdin.platform.data.parser.XmlParserUtils

internal object TextUtils {

    fun getTextForAttribute(attrs: AttributeSet, index: Int, resources: Resources): CharSequence? {
        var text: CharSequence? = null
        val value = attrs.getAttributeValue(index)
        if (value != null && value.startsWith("@")) {
            text = try {
                resources.getText(attrs.getAttributeResourceValue(index, 0))
            } catch (exception: Resources.NotFoundException) {
                null
            }
        }

        return text
    }

    fun getTextAttributeKey(res: Resources, attrs: AttributeSet, index: Int): String? {
        var id: String? = null
        val value = attrs.getAttributeValue(index)
        if (value != null && value.startsWith("@")) {
            id = try {
                res.getResourceEntryName(attrs.getAttributeResourceValue(index, 0))
            } catch (exception: Resources.NotFoundException) {
                null
            }
        }

        return id
    }

    fun updateMenuItemsText(menuRes: Int, menu: Menu, resources: Resources) {
        val itemStrings = XmlParserUtils.getMenuItemsStrings(menuRes, resources)

        for (i in 0 until itemStrings.size()) {
            val itemKey = itemStrings.keyAt(i)
            val itemValue = itemStrings.valueAt(i)

            if (itemValue.title != 0) {
                menu.findItem(itemKey)?.title = resources.getString(itemValue.title)
            }

            if (itemValue.titleCondensed != 0) {
                menu.findItem(itemKey)?.titleCondensed =
                    resources.getString(itemValue.titleCondensed)
            }
        }
    }
}
