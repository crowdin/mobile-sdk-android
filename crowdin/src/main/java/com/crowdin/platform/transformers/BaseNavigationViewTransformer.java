package com.crowdin.platform.transformers;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.Xml;
import android.view.Menu;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * A transformer which transforms navigation views: it transforms the texts coming from the menu.
 */
public abstract class BaseNavigationViewTransformer implements ViewTransformerManager.Transformer {

    private static final String TAG = BaseNavigationViewTransformer.class.getSimpleName();

    protected abstract Menu getMenu(View view);

    @Override
    public View transform(View view, AttributeSet attrs) {
        if (view == null || !getViewType().isInstance(view)) {
            return view;
        }

        for (int index = 0; index < attrs.getAttributeCount(); index++) {
            String attributeName = attrs.getAttributeName(index);
            switch (attributeName) {
                case Constants.ATTRIBUTE_APP_MENU:
                case Constants.ATTRIBUTE_MENU:
                    updateText(view, attrs, index);
                    break;

                default:
                    break;
            }
        }

        return view;
    }

    private void updateText(View view, AttributeSet attrs, int index) {
        Resources resources = view.getContext().getResources();
        String value = attrs.getAttributeValue(index);
        if (value == null || !value.startsWith("@")) return;

        int resId = attrs.getAttributeResourceValue(index, 0);
        SparseArray<MenuItemStrings> itemStrings = getMenuItemsStrings(resources, resId);

        Menu menu = getMenu(view);
        for (int i = 0; i < itemStrings.size(); i++) {
            int itemKey = itemStrings.keyAt(i);
            MenuItemStrings itemValue = itemStrings.valueAt(i);

            if (itemValue.title != 0) {
                menu.findItem(itemKey).setTitle(resources.getString(itemValue.title));
            }
            if (itemValue.titleCondensed != 0) {
                menu.findItem(itemKey).setTitleCondensed(resources.getString(itemValue.titleCondensed));
            }
        }
    }

    private SparseArray<MenuItemStrings> getMenuItemsStrings(Resources resources, int resId) {
        XmlResourceParser parser = resources.getLayout(resId);
        AttributeSet attrs = Xml.asAttributeSet(parser);
        try {
            return parseMenu(parser, attrs);
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, "getMenuItemsStrings: ", e.getCause());
            return new SparseArray<>();
        }
    }

    private SparseArray<MenuItemStrings> parseMenu(XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        String tagName;

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.getName();
                if (tagName.equals(Constants.XML_MENU)) {
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
                    if (tagName.equals(Constants.XML_ITEM)) {
                        Pair<Integer, MenuItemStrings> item = parseMenuItem(attrs);
                        if (item != null) {
                            menuItems.put(item.first, item.second);
                        }
                    } else if (tagName.equals(Constants.XML_MENU)) {
                        menuLevel++;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (tagName.equals(Constants.XML_MENU)) {
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

    private Pair<Integer, MenuItemStrings> parseMenuItem(AttributeSet attrs) {
        int menuId = 0;
        MenuItemStrings menuItemStrings = null;
        int attributeCount = attrs.getAttributeCount();
        for (int index = 0; index < attributeCount; index++) {
            switch (attrs.getAttributeName(index)) {
                case Constants.ATTRIBUTE_ANDROID_ID:
                case Constants.ATTRIBUTE_ID: {
                    menuId = attrs.getAttributeResourceValue(index, 0);
                    break;
                }
                case Constants.ATTRIBUTE_ANDROID_TITLE:
                case Constants.ATTRIBUTE_TITLE: {
                    String value = attrs.getAttributeValue(index);
                    if (value == null || !value.startsWith("@")) break;
                    if (menuItemStrings == null) {
                        menuItemStrings = new MenuItemStrings();
                    }
                    menuItemStrings.title = attrs.getAttributeResourceValue(index, 0);
                    break;
                }
                case Constants.ATTRIBUTE_ANDROID_TITLE_CONDENSED:
                case Constants.ATTRIBUTE_TITLE_CONDENSED: {
                    String value = attrs.getAttributeValue(index);
                    if (value == null || !value.startsWith("@")) break;
                    if (menuItemStrings == null) {
                        menuItemStrings = new MenuItemStrings();
                    }
                    menuItemStrings.titleCondensed = attrs.getAttributeResourceValue(index, 0);
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

    private class MenuItemStrings {

        private int title;
        private int titleCondensed;
    }
}
