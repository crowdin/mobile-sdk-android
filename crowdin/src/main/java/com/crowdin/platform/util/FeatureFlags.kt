package com.crowdin.platform.util

import com.crowdin.platform.CrowdinConfig

internal object FeatureFlags {

    private lateinit var config: CrowdinConfig

    fun registerConfig(config: CrowdinConfig) {
        this.config = config
    }

    var isRealTimeUpdateEnabled: Boolean = false
        get() = config.isRealTimeUpdateEnabled
}