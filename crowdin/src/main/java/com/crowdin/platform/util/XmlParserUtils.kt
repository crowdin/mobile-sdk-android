package com.crowdin.platform.util

import android.content.res.Resources
import android.util.AttributeSet
import android.util.Pair
import android.util.SparseArray
import android.util.Xml
import com.crowdin.platform.transformer.Attributes
import com.crowdin.platform.transformer.MenuItemStrings
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal object XmlParserUtils {

    private const val XML_MENU = "menu"
    private const val XML_ITEM = "item"

    fun getMenuItemsStrings(resources: Resources, resId: Int): SparseArray<MenuItemStrings> {
        val parser = resources.getLayout(resId)
        val attrs = Xml.asAttributeSet(parser)
        return try {
            parseMenu(parser, attrs)
        } catch (e: XmlPullParserException) {
            SparseArray()
        } catch (e: IOException) {
            SparseArray()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMenu(parser: XmlPullParser, attrs: AttributeSet): SparseArray<MenuItemStrings> {
        var eventType = parser.eventType
        var tagName: String

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.name
                if (tagName == XML_MENU) {
                    eventType = parser.next()
                    break
                }

                throw RuntimeException("Expecting menu, got $tagName")
            }
            eventType = parser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)

        val menuItems = SparseArray<MenuItemStrings>()

        var reachedEndOfMenu = false
        while (!reachedEndOfMenu) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    tagName = parser.name
                    if (tagName == XML_ITEM) {
                        val item = parseMenuItem(attrs)
                        if (item != null) {
                            menuItems.put(item.first, item.second)
                        }
                    }
                }
                XmlPullParser.END_DOCUMENT -> reachedEndOfMenu = true
            }

            eventType = parser.next()
        }
        return menuItems
    }

    private fun parseMenuItem(attrs: AttributeSet): Pair<Int, MenuItemStrings>? {
        var menuId = 0
        var menuItemStrings: MenuItemStrings? = null
        val attributeCount = attrs.attributeCount
        loop@ for (index in 0 until attributeCount) {
            when (attrs.getAttributeName(index)) {
                Attributes.ATTRIBUTE_ANDROID_ID, Attributes.ATTRIBUTE_ID -> {
                    menuId = attrs.getAttributeResourceValue(index, 0)
                }
                Attributes.ATTRIBUTE_ANDROID_TITLE, Attributes.ATTRIBUTE_TITLE -> {
                    val value = attrs.getAttributeValue(index)
                    if (value == null || !value.startsWith("@")) break@loop
                    if (menuItemStrings == null) {
                        menuItemStrings = MenuItemStrings()
                    }
                    menuItemStrings.title = attrs.getAttributeResourceValue(index, 0)
                }
                Attributes.ATTRIBUTE_ANDROID_TITLE_CONDENSED, Attributes.ATTRIBUTE_TITLE_CONDENSED -> {
                    val value = attrs.getAttributeValue(index)
                    if (value == null || !value.startsWith("@")) break@loop
                    if (menuItemStrings == null) {
                        menuItemStrings = MenuItemStrings()
                    }
                    menuItemStrings.titleCondensed = attrs.getAttributeResourceValue(index, 0)
                }
            }
        }
        return if (menuId != 0 && menuItemStrings != null) {
            Pair(menuId, menuItemStrings)
        } else {
            null
        }
    }
}
