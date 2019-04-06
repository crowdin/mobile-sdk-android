package com.crowdin.platform.utils

import java.util.*

object FilePathPlaceholder {

    @JvmStatic
    fun getAndroidCode(): String {
        return "${Locale.getDefault().language}-r${Locale.getDefault().country}"
    }

    fun getLanguage(): String {
        return Locale.getDefault().language
    }
}