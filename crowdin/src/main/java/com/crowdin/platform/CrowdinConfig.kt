package com.crowdin.platform

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

            // Validate ApiAuthConfig to prevent authentication bypass
            apiAuthConfig?.let {
                require(it.apiToken.trim().isNotEmpty()) {
                    "Crowdin: `ApiAuthConfig.apiToken` cannot be empty or blank"
                }
            }
            config.apiAuthConfig = apiAuthConfig
            config.isInitSyncEnabled = isInitSyncEnabled

            return config
        }

        companion object {
            private const val ORGANIZATION_PREFIX = "e-"
            private const val MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L // 15 minutes.
        }
    }
}
