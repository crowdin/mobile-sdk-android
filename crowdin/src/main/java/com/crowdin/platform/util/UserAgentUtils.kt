package com.crowdin.platform.util

import android.os.Build

object UserAgentUtils {

    private const val VERSION_MOZILLA = "5.0"
    private const val VERSION_APPLE_KIT_SAFARI = "537.36"
    private const val VERSION_CHROME = "74.0.3729.157"

    fun getUserAgent(): String {
        val androidVersion = Build.VERSION.RELEASE
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        return "Mozilla/$VERSION_MOZILLA (Linux; Android $androidVersion; $deviceName) " +
                "AppleWebKit/$VERSION_APPLE_KIT_SAFARI (KHTML, like Gecko) " +
                "Chrome/$VERSION_CHROME " +
                "Mobile Safari/$VERSION_APPLE_KIT_SAFARI"
    }
}