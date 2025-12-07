package com.crowdin.platform

import android.os.Build
import android.util.Log
import com.crowdin.platform.data.model.ApiAuthConfig
import com.crowdin.platform.data.model.AuthConfig
import com.crowdin.platform.data.remote.NetworkType

/**
 * Contains configuration properties for initializing Crowdin.
 */
class CrowdinConfig private constructor() {
    var isPersist: Boolean = true
    var distributionHash: String = ""
    var networkType: NetworkType = NetworkType.ALL
    var isRealTimeUpdateEnabled: Boolean = false
    var isScreenshotEnabled: Boolean = false
    var updateInterval: Long = -1
    var sourceLanguage: String = ""
    var authConfig: AuthConfig? = null
    var apiAuthConfig: ApiAuthConfig? = null
    var isInitSyncEnabled: Boolean = true
    var organizationName: String? = null
    var isRealTimeComposeEnabled: Boolean = false

    class Builder {
        private var isPersist: Boolean = true
        private var distributionHash: String = ""
        private var networkType: NetworkType = NetworkType.ALL
        private var isRealTimeUpdateEnabled: Boolean = false
        private var isScreenshotEnabled: Boolean = false
        private var updateInterval: Long = -1
        private var sourceLanguage: String = ""
        private var authConfig: AuthConfig? = null
        private var apiAuthConfig: ApiAuthConfig? = null
        private var isInitSyncEnabled: Boolean = true
        private var organizationName: String? = null
        private var isRealTimeComposeEnabled: Boolean = false

        fun persist(isPersist: Boolean): Builder {
            this.isPersist = isPersist
            return this
        }

        fun withDistributionHash(distributionHash: String): Builder {
            this.distributionHash = distributionHash
            return this
        }

        fun withNetworkType(networkType: NetworkType): Builder {
            this.networkType = networkType
            return this
        }

        fun withRealTimeUpdates(): Builder {
            this.isRealTimeUpdateEnabled = true
            return this
        }

        fun withScreenshotEnabled(): Builder {
            this.isScreenshotEnabled = true
            return this
        }

        fun withSourceLanguage(sourceLanguage: String): Builder {
            this.sourceLanguage = sourceLanguage
            return this
        }

        fun withUpdateInterval(updateInterval: Long): Builder {
            this.updateInterval = updateInterval * 1000
            return this
        }

        fun withAuthConfig(authConfig: AuthConfig): Builder {
            this.authConfig = authConfig
            // Required for backward compatibility
            authConfig.organizationName?.let {
                this.organizationName = it
            }
            return this
        }

        fun withApiAuthConfig(apiAuthConfig: ApiAuthConfig): Builder {
            this.apiAuthConfig = apiAuthConfig
            return this
        }

        fun withInitSyncDisabled(): Builder {
            this.isInitSyncEnabled = false
            return this
        }

        fun withOrganizationName(organizationName: String): Builder {
            this.organizationName = organizationName
            return this
        }

        /**
         * Enables real-time update functionality for Jetpack Compose.
         * It is recommended to tie this to a build-time flag.
         */
        fun withRealTimeComposeEnabled(enabled: Boolean): Builder {
            this.isRealTimeComposeEnabled = enabled
            return this
        }

        fun build(): CrowdinConfig {
            val config = CrowdinConfig()
            config.isPersist = isPersist
            require(distributionHash.isNotEmpty()) { "Crowdin: `distributionHash` cannot be empty" }

            config.distributionHash = distributionHash

            if (distributionHash.startsWith(ORGANIZATION_PREFIX) && organizationName.isNullOrEmpty()) {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "Crowdin: the `organizationName` cannot be empty for Crowdin Enterprise. Add it to the `CrowdingConfig` " +
                        "using the `.withOrganizationName(...)` method",
                )
            }

            config.organizationName = organizationName
            config.networkType = networkType
            config.isRealTimeUpdateEnabled = isRealTimeUpdateEnabled
            config.isScreenshotEnabled = isScreenshotEnabled

            if (isRealTimeUpdateEnabled || isScreenshotEnabled) {
                require(sourceLanguage.isNotEmpty()) {
                    "Crowdin: `sourceLanguage` cannot be empty"
                }
            }

            if (updateInterval < MIN_PERIODIC_INTERVAL_MILLIS) {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "`updateInterval` must be not less than 15 minutes. Will be used default value - 15 minutes",
                )
                config.updateInterval = MIN_PERIODIC_INTERVAL_MILLIS
            } else {
                config.updateInterval = updateInterval
            }

            config.sourceLanguage = sourceLanguage

            authConfig?.let {
                require(it.clientId.trim().isNotEmpty() && it.clientSecret.trim().isNotEmpty()) {
                    "Crowdin: `AuthConfig` values cannot be empty"
                }
            }
            config.authConfig = authConfig
            config.apiAuthConfig = apiAuthConfig
            config.isInitSyncEnabled = isInitSyncEnabled
            config.isRealTimeComposeEnabled = isRealTimeComposeEnabled

            if (isRealTimeComposeEnabled && !isRealTimeUpdateEnabled) {
                require(false) {
                    "Crowdin: `withRealTimeComposeEnabled` requires `withRealTimeUpdates()` to be enabled. " +
                        "Real-time Compose support needs the WebSocket connection for receiving translation updates. " +
                        "Please add `.withRealTimeUpdates()` to your CrowdinConfig.Builder."
                }
            }

            if (isRealTimeComposeEnabled && Build.VERSION.SDK_INT < BuildConfig.MIN_COMPOSE_API_LEVEL) {
                Log.w(
                    Crowdin.CROWDIN_TAG,
                    "Crowdin: Real-time Compose support is disabled on API level ${Build.VERSION.SDK_INT}. " +
                        "Minimum required API level is ${BuildConfig.MIN_COMPOSE_API_LEVEL}. " +
                        "Real-time Compose updates use ConcurrentHashMap.computeIfAbsent and Map.putIfAbsent which are only available on API 24+. " +
                        "Compose translations will still work, but real-time updates will not be available on this device."
                )
                config.isRealTimeComposeEnabled = false
            }

            return config
        }

        companion object {
            private const val ORGANIZATION_PREFIX = "e-"
            private const val MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L // 15 minutes.
        }
    }
}
