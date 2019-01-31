package com.crowdin.platform.utils;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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
}
