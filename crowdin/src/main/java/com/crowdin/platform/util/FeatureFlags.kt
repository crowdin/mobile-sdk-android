package com.crowdin.platform.util

import com.crowdin.platform.CrowdinConfig

internal object FeatureFlags {
    private lateinit var config: CrowdinConfig

    fun registerConfig(config: CrowdinConfig) {
        this.config = config
    }

    val isRealTimeUpdateEnabled: Boolean
        get() = config.isRealTimeUpdateEnabled

    val isScreenshotEnabled: Boolean
        get() = config.isScreenshotEnabled
}
