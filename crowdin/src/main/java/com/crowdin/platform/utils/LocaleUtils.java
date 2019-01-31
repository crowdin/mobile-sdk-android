package com.crowdin.platform.utils;

import java.util.Locale;

public class LocaleUtils {

    private LocaleUtils() {
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
