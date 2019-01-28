package com.crowdin.platform.utils;

import java.util.Locale;

public class RestringUtils {

    private RestringUtils() {
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
