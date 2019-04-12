package com.crowdin.platform

import com.crowdin.platform.repository.remote.NetworkType

/**
 * Contains configuration properties for initializing Crowdin.
 */
class CrowdinConfig private constructor() {

    var isPersist: Boolean = true
    var distributionKey: String? = null
    var filePaths: Array<out String>? = null
    var networkType: NetworkType = NetworkType.ALL
    var isRealTimeUpdateEnabled: Boolean = false

    class Builder {

        private var persist: Boolean = true
        private var distributionKey: String? = null
        private var filePaths: Array<out String>? = null
        private var networkType: NetworkType = NetworkType.ALL
        private var isRealTimeUpdateEnabled: Boolean = false

        fun persist(persist: Boolean): Builder {
            this.persist = persist
            return this
        }

        fun withDistributionKey(distributionKey: String): Builder {
            this.distributionKey = distributionKey
            return this
        }

        fun withFilePaths(vararg filePaths: String): Builder {
            this.filePaths = filePaths
            return this
        }

        fun withNetworkType(networkType: NetworkType): Builder {
            this.networkType = networkType
            return this
        }

        fun withRealTimeUpdates(isRealTimeUpdateEnabled: Boolean): Builder {
            this.isRealTimeUpdateEnabled = isRealTimeUpdateEnabled
            return this
        }

        fun build(): CrowdinConfig {
            val config = CrowdinConfig()
            config.isPersist = persist
            config.distributionKey = distributionKey
            config.filePaths = filePaths
            config.networkType = networkType
            config.isRealTimeUpdateEnabled = isRealTimeUpdateEnabled

            return config
        }
    }
}