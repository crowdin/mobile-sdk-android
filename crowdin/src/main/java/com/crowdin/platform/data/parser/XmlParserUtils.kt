package com.crowdin.platform.data.parser

import android.content.res.Resources
import android.util.AttributeSet
import android.util.Pair
import android.util.SparseArray
import android.util.Xml
import com.crowdin.platform.transformer.Attributes
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal object XmlParserUtils {

    private const val XML_MENU = "menu"
    private const val XML_ITEM = "item"

    fun getMenuItemsStrings(resources: Resources, resId: Int): SparseArray<MenuItemStrings> {
        val xmlResourceParser = resources.getLayout(resId)
        val attributeSet = Xml.asAttributeSet(xmlResourceParser)
        return try {
            parseMenu(xmlResourceParser, attributeSet)
        } catch (e: XmlPullParserException) {
            SparseArray()
        } catch (e: IOException) {
            SparseArray()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMenu(xmlPullParser: XmlPullParser, attributeSet: AttributeSet): SparseArray<MenuItemStrings> {
        var eventType = xmlPullParser.eventType
        var tagName: String

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = xmlPullParser.name
                if (tagName == XML_MENU) {
                    eventType = xmlPullParser.next()
                    break
                }

                throw RuntimeException("Expecting menu, got $tagName")
            }
            eventType = xmlPullParser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)

        val menuItems = SparseArray<MenuItemStrings>()

        var reachedEndOfMenu = false
        while (!reachedEndOfMenu) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    tagName = xmlPullParser.name
                    if (tagName == XML_ITEM) {
                        val item = parseMenuItem(attributeSet)
                        if (item != null) {
                            menuItems.put(item.first, item.second)
                        }
                    }
                }
                XmlPullParser.END_DOCUMENT -> reachedEndOfMenu = true
            }

            eventType = xmlPullParser.next()
        }
        return menuItems
    }

    private fun parseMenuItem(attributeSet: AttributeSet): Pair<Int, MenuItemStrings>? {
        var menuId = 0
        var menuItemStrings: MenuItemStrings? = null
        val attributeCount = attributeSet.attributeCount
        loop@ for (index in 0 until attributeCount) {
            when (attributeSet.getAttributeName(index)) {
                Attributes.ATTRIBUTE_ANDROID_ID, Attributes.ATTRIBUTE_ID -> {
                    menuId = attributeSet.getAttributeResourceValue(index, 0)
                }
                Attributes.ATTRIBUTE_ANDROID_TITLE, Attributes.ATTRIBUTE_TITLE -> {
                    val value = attributeSet.getAttributeValue(index)
                    if (value == null || !value.startsWith("@")) break@loop
                    if (menuItemStrings == null) {
                        menuItemStrings = MenuItemStrings()
                    }
                    menuItemStrings.title = attributeSet.getAttributeResourceValue(index, 0)
                }
                Attributes.ATTRIBUTE_ANDROID_TITLE_CONDENSED, Attributes.ATTRIBUTE_TITLE_CONDENSED -> {
                    val value = attributeSet.getAttributeValue(index)
                    if (value == null || !value.startsWith("@")) break@loop
                    if (menuItemStrings == null) {
                        menuItemStrings = MenuItemStrings()
                    }
                    menuItemStrings.titleCondensed = attributeSet.getAttributeResourceValue(index, 0)
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
