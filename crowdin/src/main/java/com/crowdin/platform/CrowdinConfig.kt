package com.crowdin.platform

/**
 * Contains configuration properties for initializing Crowdin.
 */
class CrowdinConfig private constructor() {

    var isPersist: Boolean = true
    var distributionKey: String? = null
    var filePaths: Array<out String>? = null

    class Builder {

        private var persist: Boolean = true
        private var distributionKey: String? = null
        private var filePaths: Array<out String>? = null

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

        fun build(): CrowdinConfig {
            val config = CrowdinConfig()
            config.isPersist = persist
            config.distributionKey = distributionKey
            config.filePaths = filePaths
            return config
        }
    }
}