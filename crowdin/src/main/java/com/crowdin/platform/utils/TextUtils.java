package com.crowdin.platform.utils;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crowdin.platform.transformers.MenuItemStrings;

public class TextUtils {

    private TextUtils() {
    }

    @Nullable
    public static String getTextForAttribute(AttributeSet attrs, int index, View view, Resources resources) {
        String text = null;
        String value = attrs.getAttributeValue(index);
        if (value != null && value.startsWith("@")) {
            text = resources.getString(attrs.getAttributeResourceValue(index, 0));
        }

        return text;
    }

    public static void updateMenuItemsText(Menu menu, Resources resources, int resId) {
        SparseArray<MenuItemStrings> itemStrings = XmlParserUtils.getMenuItemsStrings(resources, resId);

        for (int i = 0; i < itemStrings.size(); i++) {
            int itemKey = itemStrings.keyAt(i);
            MenuItemStrings itemValue = itemStrings.valueAt(i);

            if (itemValue.getTitle() != 0) {
                MenuItem menuItem = menu.findItem(itemKey);
                if (menuItem != null) {
                    menuItem.setTitle(resources.getString(itemValue.getTitle()));
                }
            }
            if (itemValue.getTitleCondensed() != 0) {
                MenuItem menuItem = menu.findItem(itemKey);
                if (menuItem != null) {
                    menuItem.setTitleCondensed(resources.getString(itemValue.getTitleCondensed()));
                }
            }
        }
    }
}
