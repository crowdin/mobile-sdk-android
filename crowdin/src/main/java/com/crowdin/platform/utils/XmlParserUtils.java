package com.crowdin.platform.utils;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.util.Xml;

import com.crowdin.platform.transformers.Attributes;
import com.crowdin.platform.transformers.MenuItemStrings;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

class XmlParserUtils {

    private static final String XML_MENU = "menu";
    private static final String XML_ITEM = "item";

    private XmlParserUtils() {
    }

    static SparseArray<MenuItemStrings> getMenuItemsStrings(Resources resources, int resId) {
        XmlResourceParser parser = resources.getLayout(resId);
        AttributeSet attrs = Xml.asAttributeSet(parser);
        try {
            return parseMenu(parser, attrs);
        } catch (XmlPullParserException | IOException e) {
            return new SparseArray<>();
        }
    }

    private static SparseArray<MenuItemStrings> parseMenu(XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        String tagName;

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.getName();
                if (tagName.equals(XML_MENU)) {
                    eventType = parser.next();
                    break;
                }

                throw new RuntimeException("Expecting menu, got " + tagName);
            }
            eventType = parser.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);

        SparseArray<MenuItemStrings> menuItems = new SparseArray<>();

        boolean reachedEndOfMenu = false;
        int menuLevel = 0;
        while (!reachedEndOfMenu) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals(XML_ITEM)) {
                        Pair<Integer, MenuItemStrings> item = parseMenuItem(attrs);
                        if (item != null) {
                            menuItems.put(item.first, item.second);
                        }
                    } else if (tagName.equals(XML_MENU)) {
                        menuLevel++;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals(XML_MENU)) {
                        menuLevel--;
                        if (menuLevel <= 0) {
                            reachedEndOfMenu = true;
                        }
                    }
                    break;

                case XmlPullParser.END_DOCUMENT:
                    reachedEndOfMenu = true;
                    break;

                default:
                    break;
            }

            eventType = parser.next();
        }
        return menuItems;
    }

    private static Pair<Integer, MenuItemStrings> parseMenuItem(AttributeSet attrs) {
        int menuId = 0;
        MenuItemStrings menuItemStrings = null;
        int attributeCount = attrs.getAttributeCount();
        for (int index = 0; index < attributeCount; index++) {
            switch (attrs.getAttributeName(index)) {
                case Attributes.ATTRIBUTE_ANDROID_ID:
                case Attributes.ATTRIBUTE_ID: {
                    menuId = attrs.getAttributeResourceValue(index, 0);
                    break;
                }
                case Attributes.ATTRIBUTE_ANDROID_TITLE:
                case Attributes.ATTRIBUTE_TITLE: {
                    String value = attrs.getAttributeValue(index);
                    if (value == null || !value.startsWith("@")) break;
                    if (menuItemStrings == null) {
                        menuItemStrings = new MenuItemStrings();
                    }
                    menuItemStrings.setTitle(attrs.getAttributeResourceValue(index, 0));
                    break;
                }
                case Attributes.ATTRIBUTE_ANDROID_TITLE_CONDENSED:
                case Attributes.ATTRIBUTE_TITLE_CONDENSED: {
                    String value = attrs.getAttributeValue(index);
                    if (value == null || !value.startsWith("@")) break;
                    if (menuItemStrings == null) {
                        menuItemStrings = new MenuItemStrings();
                    }
                    menuItemStrings.setTitleCondensed(attrs.getAttributeResourceValue(index, 0));
                    break;
                }
                default:
                    break;
            }
        }
        return (menuId != 0 && menuItemStrings != null)
                ? new Pair<>(menuId, menuItemStrings)
                : null;
    }
}
