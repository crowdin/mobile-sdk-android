package com.crowdin.platform.utils

import java.util.Locale

object LocaleUtils {

    val currentLanguage: String
        get() = Locale.getDefault().language

    val currentLocale: Locale
        get() = Locale.getDefault()
}
