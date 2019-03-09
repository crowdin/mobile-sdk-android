package com.crowdin.platform.utils

import java.util.Locale

internal object LocaleUtils {

    val currentLanguage: String
        get() = Locale.getDefault().language

    val currentLocale: Locale
        get() = Locale.getDefault()
}
